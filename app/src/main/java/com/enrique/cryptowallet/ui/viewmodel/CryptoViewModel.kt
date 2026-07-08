package com.enrique.cryptowallet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.enrique.cryptowallet.data.model.Crypto
import com.enrique.cryptowallet.data.repository.CryptoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CryptoViewModel(private val repository: CryptoRepository) : ViewModel() {

    // Utilizei o MutableStateFlow para gerenciar o estado da lista de moedas.
    private val _cryptoList = MutableStateFlow<List<Crypto>>(emptyList())

    // Exponho apenas a versão 'somente leitura' (StateFlow) para a UI.
    // Isso garante que a View (Activity/Fragment) não possa alterar os dados diretamente,
    // respeitando fortemente o princípio de encapsulamento da arquitetura MVVM.
    val cryptoList: StateFlow<List<Crypto>> = _cryptoList.asStateFlow()

    init {
        // Assim que o ViewModel é instanciado na memória, eu inicio a observação do banco de dados
        // e peço para o Repositório buscar os preços mais recentes na API.
        observeDatabase()
        refreshData()
    }

    private fun observeDatabase() {
        // O viewModelScope garante que essa coroutine seja cancelada automaticamente
        // quando o ViewModel for destruído pelo sistema, evitando memory leaks (vazamento de memória).
        viewModelScope.launch {
            repository.getAllCryptosFlow().collect { cryptos ->
                // Sempre que o banco de dados local mudar, esta variável é atualizada.
                // A tela, que estará observando essa variável, vai se redesenhar automaticamente.
                _cryptoList.value = cryptos
            }
        }
    }

    // Função pública que a tela pode chamar, por exemplo, ao usar um botão de atualizar
    // ou a ação de arrastar a lista para baixo (Swipe to Refresh).
    fun refreshData() {
        viewModelScope.launch {
            repository.syncCryptosFromNetwork()
        }
    }

    fun buyCrypto(crypto: Crypto, amountPurchased: Double) {
        viewModelScope.launch {
            // Pegamos a moeda atual e criamos uma cópia dela com a quantidade somada
            val updatedCrypto = crypto.copy(
                ownedQuantity = crypto.ownedQuantity + amountPurchased
            )

            // Usamos a função que você já tinha criado no Repositório!
            repository.updateCryptoBalance(updatedCrypto)
        }
    }

    // Função para atualizar a quantidade exata da moeda (Edição) ou remover (se passar 0.0)
    fun updateCryptoQuantity(crypto: Crypto, newQuantity: Double) {
        viewModelScope.launch {
            // Criamos uma cópia da moeda substituindo pela quantidade exata informada
            val updatedCrypto = crypto.copy(
                ownedQuantity = newQuantity
            )

            // Salvamos no banco de dados
            repository.updateCryptoBalance(updatedCrypto)
        }
    }
}

// Como o nosso CryptoViewModel precisa receber o Repository no construtor,
// criei esta Factory. É um padrão de projeto necessário no Android quando não
// utilizamos bibliotecas de Injeção de Dependência (como Hilt ou Dagger).
class CryptoViewModelFactory(private val repository: CryptoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CryptoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CryptoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}