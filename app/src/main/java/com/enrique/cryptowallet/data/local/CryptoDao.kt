package com.enrique.cryptowallet.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.enrique.cryptowallet.data.model.Crypto
import kotlinx.coroutines.flow.Flow

@Dao
interface CryptoDao {

    // Utilizei a estratégia REPLACE no OnConflict. Assim, se o usuário já tiver "bitcoin" salvo
    // e tentar salvar de novo (por exemplo, ao comprar mais), o banco apenas atualiza
    // a linha existente com o novo saldo, em vez de dar crash por duplicidade de ID.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrypto(crypto: Crypto)

    // Optei por retornar um Flow em vez de uma List tradicional.
    // Isso cria um canal de comunicação reativo: qualquer mudança que acontecer no banco
    // (como o usuário alterar a quantidade de uma moeda) refletirá automaticamente na UI,
    // sem precisar fazer uma nova requisição manual.
    @Query("SELECT * FROM crypto_wallet")
    fun getAllCryptos(): Flow<List<Crypto>>

    @Query("SELECT * FROM crypto_wallet WHERE id = :cryptoId")
    suspend fun getCryptoById(cryptoId: String): Crypto?

    @Query("DELETE FROM crypto_wallet WHERE id = :cryptoId")
    suspend fun deleteCrypto(cryptoId: String)

    // Atualiza os dados de uma moeda que já existe no banco
    @Update
    suspend fun update(crypto: Crypto)
}