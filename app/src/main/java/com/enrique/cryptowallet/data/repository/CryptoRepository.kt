package com.enrique.cryptowallet.data.repository

import com.enrique.cryptowallet.data.local.CryptoDao
import com.enrique.cryptowallet.data.model.Crypto
import com.enrique.cryptowallet.data.remote.CryptoApiService
import kotlinx.coroutines.flow.Flow

// Criei esta classe utilizando o padrão Repository Pattern.
// O objetivo aqui é ser a "Única Fonte da Verdade" (Single Source of Truth) para o aplicativo.
// As telas não precisam saber se a internet está ligada ou desligada, o Repositório decide
// de onde buscar os dados e como sincronizá-los.
class CryptoRepository(
    private val apiService: CryptoApiService,
    private val dao: CryptoDao
) {

    // Retorna o fluxo (Flow) diretamente do banco de dados local.
    // Como a UI estará observando este Flow, qualquer alteração que fizermos no banco local
    // vai refletir automaticamente na tela do usuário, sem precisarmos avisar a tela manualmente.
    fun getAllCryptosFlow(): Flow<List<Crypto>> {
        return dao.getAllCryptos()
    }

    // Função responsável por bater na API e atualizar o banco local com os preços mais recentes.
    suspend fun syncCryptosFromNetwork() {
        try {
            // Fazemos a chamada de rede utilizando o cliente do Retrofit.
            val response = apiService.getCryptoMarkets()

            if (response.isSuccessful) {
                // Se a resposta for 200 (OK), pegamos o corpo (a lista de moedas)
                response.body()?.let { remoteCryptos ->

                    // Lógica de Negócio: Não podemos simplesmente jogar a lista da internet no banco.
                    // Se fizermos isso, o 'REPLACE' do banco apagaria a 'ownedQuantity' (o saldo) do usuário.
                    // Portanto, verifico cada moeda antes de salvar.
                    remoteCryptos.forEach { remoteCrypto ->

                        val localCrypto = dao.getCryptoById(remoteCrypto.id)

                        if (localCrypto != null) {
                            // Se o usuário já tinha essa moeda salva, eu preservo o saldo dele
                            // e atualizo apenas os preços que vieram da internet.
                            remoteCrypto.ownedQuantity = localCrypto.ownedQuantity
                        }

                        // Agora sim, salvo a moeda atualizada de forma segura no Room.
                        dao.insertCrypto(remoteCrypto)
                    }
                }
            }
        } catch (e: Exception) {
            // Caso falhe (ex: celular sem internet), a exceção é capturada aqui.
            // O app não vai "crashar" (fechar sozinho) e o usuário continuará vendo os dados
            // que já estavam salvos no banco local graças ao Flow.
            e.printStackTrace()
        }
    }

    // Função simples para quando o usuário realizar uma compra ou venda,
    // permitindo atualizar apenas a quantidade da moeda específica no banco de dados.
    suspend fun updateCryptoBalance(crypto: Crypto) {
        dao.update(crypto)
    }

}