package com.enrique.cryptowallet.data.remote

import com.enrique.cryptowallet.data.model.Crypto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// Interface que define os contratos de comunicação com a API da CoinGecko.
// Utilizei o Retrofit pois é o padrão de mercado atual para consumo de APIs REST no Android.
interface CryptoApiService {

    // Endpoint principal para buscar a lista do mercado de moedas.
    // Utilizei uma função 'suspend' para que a chamada de rede seja executada
    // de forma assíncrona pelas Coroutines, garantindo que a tela do usuário não congele.
    @GET("api/v3/coins/markets")
    suspend fun getCryptoMarkets(

        // Parâmetro obrigatório da API para definir a moeda base dos preços.
        @Query("vs_currency") currency: String = "usd",

        // Limitei o número de resultados para otimizar o consumo de dados do usuário
        // e o tempo de carregamento da lista.
        @Query("per_page") perPage: Int = 50,

        // Mantém a busca na primeira página de resultados.
        @Query("page") page: Int = 1

        // O retorno é encapsulado na classe 'Response' do Retrofit para facilitar
        // o tratamento de erros (como erro 404, 500) na camada do ViewModel.
    ): Response<List<Crypto>>

}