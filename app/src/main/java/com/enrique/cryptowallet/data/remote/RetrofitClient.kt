package com.enrique.cryptowallet.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Utilizei um 'object' (Singleton) para garantir que o aplicativo crie apenas uma única
// instância do Retrofit durante todo o seu ciclo de vida. Isso economiza muita memória RAM.
object RetrofitClient {

    // A URL base da API. Todos os endpoints definidos na interface serão adicionados ao final dela.
    private const val BASE_URL = "https://api.coingecko.com/"

    // Inicialização 'lazy': O bloco abaixo só será executado e alocado na memória
    // no exato momento em que a primeira tela pedir os dados, otimizando o tempo de abertura do app.
    val apiService: CryptoApiService by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // Adiciona o conversor do Gson para traduzir o JSON da internet direto para a nossa Data Class Crypto.
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            // Conecta a nossa interface (o cardápio) com o motor do Retrofit.
            .create(CryptoApiService::class.java)
    }
}