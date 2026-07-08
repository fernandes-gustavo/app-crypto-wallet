package com.enrique.cryptowallet.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// Modelo único que transita entre a camada de rede (Retrofit/Gson) e o banco de dados (Room).
// Optei por utilizar a mesma classe para ambos os fins para evitar mapeamentos excessivos e manter o projeto enxuto.
@Entity(tableName = "crypto_wallet")
data class Crypto(

    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("symbol")
    val symbol: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("current_price")
    val currentPrice: Double,

    @SerializedName("image")
    val imageUrl: String,

    // Propriedade exclusiva do banco local para armazenar o saldo do usuário.
    // O valor padrão 0.0 garante que o mapeamento do Gson não falhe quando essa variável não vier no JSON da API.
    var ownedQuantity: Double = 0.0
)
