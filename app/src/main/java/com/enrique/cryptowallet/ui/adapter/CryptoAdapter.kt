package com.enrique.cryptowallet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.enrique.cryptowallet.R
import com.enrique.cryptowallet.data.model.Crypto

// Utilizei o ListAdapter com DiffUtil em vez do antigo RecyclerView.Adapter.
// Isso melhora drasticamente a performance, pois o DiffUtil calcula a diferença entre
// a lista velha e a nova em uma thread separada, atualizando na tela apenas
// as moedas que realmente mudaram de preço, evitando recarregar a lista inteira.
class CryptoAdapter(
    private val onItemClicked: (Crypto) -> Unit
) : ListAdapter<Crypto, CryptoAdapter.CryptoViewHolder>(CryptoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoViewHolder {
        // Infla o nosso arquivo XML 'item_crypto' para transformá-lo em uma View visual.
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_crypto, parent, false)
        return CryptoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
        val crypto = getItem(position)
        holder.bind(crypto)
    }

    // O ViewHolder é o responsável por encontrar as referências dos TextViews e ImageView
    // dentro do XML e preenchê-los com os dados da classe Crypto.
    inner class CryptoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCryptoIcon: ImageView = itemView.findViewById(R.id.ivCryptoIcon)
        private val tvCryptoName: TextView = itemView.findViewById(R.id.tvCryptoName)
        private val tvCryptoSymbol: TextView = itemView.findViewById(R.id.tvCryptoSymbol)
        private val tvCryptoPrice: TextView = itemView.findViewById(R.id.tvCryptoPrice)
        private val tvOwnedQuantity: TextView = itemView.findViewById(R.id.tvOwnedQuantity)

        fun bind(crypto: Crypto) {
            tvCryptoName.text = crypto.name
            tvCryptoSymbol.text = crypto.symbol
            tvCryptoPrice.text = "$ ${crypto.currentPrice}" // Formatação simples para dólar

            // Só exibo o texto de "quantidade possuída" se o usuário tiver saldo dessa moeda.
            if (crypto.ownedQuantity > 0) {
                tvOwnedQuantity.visibility = View.VISIBLE
                tvOwnedQuantity.text = "Possui: ${crypto.ownedQuantity}"
            } else {
                tvOwnedQuantity.visibility = View.GONE
            }

            // Utilizei a biblioteca Coil para carregar a imagem de forma assíncrona.
            // Ela é extremamente leve e faz o cache das imagens automaticamente.
            ivCryptoIcon.load(crypto.imageUrl) {
                crossfade(true) // Adiciona uma transição suave ao carregar a imagem
                placeholder(R.drawable.ic_launcher_background) // Imagem padrão enquanto carrega
            }

            itemView.setOnClickListener {
                onItemClicked(crypto)
            }
        }
    }

    // Classe de utilitário responsável pela lógica de comparação de performance da lista.
    class CryptoDiffCallback : DiffUtil.ItemCallback<Crypto>() {
        override fun areItemsTheSame(oldItem: Crypto, newItem: Crypto): Boolean {
            // Verifica se as moedas são a mesma entidade através do ID único.
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Crypto, newItem: Crypto): Boolean {
            // Verifica se algum dado interno da moeda (como o preço ou saldo) mudou.
            return oldItem == newItem
        }
    }
}