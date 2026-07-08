package com.enrique.cryptowallet.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.enrique.cryptowallet.CryptoApplication
import com.enrique.cryptowallet.R
import com.enrique.cryptowallet.ui.adapter.CryptoAdapter
import com.enrique.cryptowallet.ui.viewmodel.CryptoViewModel
import com.enrique.cryptowallet.ui.viewmodel.CryptoViewModelFactory
import kotlinx.coroutines.launch
import java.util.Locale

class WalletActivity : AppCompatActivity() {

    private val viewModel: CryptoViewModel by viewModels {
        CryptoViewModelFactory((application as CryptoApplication).repository)
    }

    private lateinit var adapter: CryptoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_wallet)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recyclerViewWallet)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        // botão de voltar
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewWallet)

        adapter = CryptoAdapter { clickedCrypto ->

            // 1. Cria a janelinha passando o nome e o saldo atual da moeda clicada
            val bottomSheet = EditCryptoBottomSheet(
                cryptoName = clickedCrypto.name,
                currentQuantity = clickedCrypto.ownedQuantity
            ) { newQuantity ->

                // 2. O que acontece quando o usuário clica em "Salvar Alteração"
                viewModel.updateCryptoQuantity(clickedCrypto, newQuantity)
                Toast.makeText(this, "Saldo atualizado!", Toast.LENGTH_SHORT).show()
            }

            // 3. Faz a janelinha aparecer na tela
            bottomSheet.show(supportFragmentManager, "EditCryptoBottomSheet")
        }

        recyclerView.adapter = adapter

        // Ativa a funcionalidade de arrastar para excluir na lista
        setupSwipeToDelete(recyclerView)
    }

    private fun setupSwipeToDelete(recyclerView: RecyclerView) {
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val cryptoToDelete = adapter.currentList[position]

                val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(this@WalletActivity)
                    .setTitle("Remover ${cryptoToDelete.name}?")
                    .setMessage("Tem certeza que deseja zerar o saldo e remover esta moeda da carteira?")
                    .setPositiveButton("Sim, remover") { _, _ ->
                        viewModel.updateCryptoQuantity(cryptoToDelete, 0.0)
                        Toast.makeText(this@WalletActivity, "${cryptoToDelete.name} removida!", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancelar") { _, _ ->
                        adapter.notifyItemChanged(position)
                    }
                    .setOnCancelListener {
                        adapter.notifyItemChanged(position)
                    }
                    .show()

                val btnPositive = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                val btnNegative = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)

                btnPositive.setTextColor(Color.parseColor("#F44336"))
                btnNegative.setTextColor(Color.GRAY)
            }

            // Esta função desenha o fundo vermelho e a lixeira por baixo do item enquanto ele é arrastado
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val background = ColorDrawable(Color.parseColor("#F44336")) // Cor vermelha

                // Pega o ícone de lixeira nativo do próprio Android
                val deleteIcon = ContextCompat.getDrawable(this@WalletActivity, android.R.drawable.ic_menu_delete)

                // Desenha apenas se estiver arrastando para a esquerda (dX < 0)
                if (dX < 0) {
                    background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    background.draw(c)

                    // Desenha o ícone da lixeira centralizado na altura do item
                    deleteIcon?.let { icon ->
                        val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
                        val iconTop = itemView.top + iconMargin
                        val iconBottom = iconTop + icon.intrinsicHeight
                        val iconRight = itemView.right - iconMargin
                        val iconLeft = iconRight - icon.intrinsicWidth

                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        icon.draw(c)
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        // Anexa esse comportamento na RecyclerView
        ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView)
    }

    private fun observeViewModel() {
        val tvTotalBalance = findViewById<TextView>(R.id.tvTotalBalance)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewWallet)
        val layoutEmptyState = findViewById<android.widget.LinearLayout>(R.id.layoutEmptyState)
        val swipeRefreshLayout = findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.swipeRefreshLayout)

        // Configura a ação de puxar para atualizar
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshData()

            swipeRefreshLayout.postDelayed({
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(this, "Preços atualizados!", Toast.LENGTH_SHORT).show()
            }, 1500)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cryptoList.collect { cryptos ->

                    val ownedCryptos = cryptos.filter { it.ownedQuantity > 0 }
                    val totalBalance = ownedCryptos.sumOf { it.ownedQuantity * it.currentPrice }

                    val formattedValue = String.format(Locale.US, "%.2f", totalBalance)
                    tvTotalBalance.text = getString(R.string.balance_format, formattedValue)

                    if (ownedCryptos.isEmpty()) {
                        recyclerView.visibility = android.view.View.GONE
                        layoutEmptyState.visibility = android.view.View.VISIBLE
                    } else {
                        recyclerView.visibility = android.view.View.VISIBLE
                        layoutEmptyState.visibility = android.view.View.GONE
                    }

                    adapter.submitList(ownedCryptos)
                }
            }
        }
    }
}