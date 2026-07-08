package com.enrique.cryptowallet

import android.app.Application
import com.enrique.cryptowallet.data.local.CryptoDatabase
import com.enrique.cryptowallet.data.remote.RetrofitClient
import com.enrique.cryptowallet.data.repository.CryptoRepository

class CryptoApplication : Application() {

    // O banco de dados só será inicializado quando for realmente necessário (lazy)
    private val database by lazy { CryptoDatabase.getDatabase(this) }

    // O Repositório é instanciado aqui, conectando a internet e o banco de dados local.
    val repository by lazy {
        CryptoRepository(RetrofitClient.apiService, database.cryptoDao())
    }
}