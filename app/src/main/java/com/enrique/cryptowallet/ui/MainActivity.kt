package com.enrique.cryptowallet.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.enrique.cryptowallet.CryptoApplication
import com.enrique.cryptowallet.R
import com.enrique.cryptowallet.ui.adapter.CryptoAdapter
import com.enrique.cryptowallet.ui.viewmodel.CryptoViewModel
import com.enrique.cryptowallet.ui.viewmodel.CryptoViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: CryptoViewModel by viewModels {
        CryptoViewModelFactory((application as CryptoApplication).repository)
    }

    private lateinit var adapter: CryptoAdapter
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    // Variável para sabermos se é a primeira vez que o app está abrindo
    private var isFirstLoad = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Botão Carteira no topo
        val btnWallet = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnWallet)
        btnWallet.setOnClickListener {
            val intent = Intent(this, WalletActivity::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()

        // NOVO: Assim que o app abre, já forçamos a rodinha a aparecer e girar
        swipeRefresh.post {
            swipeRefresh.isRefreshing = true
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewCryptos)

        // Deixamos a lista invisível assim que a tela é criada
        recyclerView.alpha = 0f

        adapter = CryptoAdapter { clickedCrypto ->
            val bottomSheet = BuyCryptoBottomSheet(
                cryptoName = clickedCrypto.name,
                cryptoPrice = clickedCrypto.currentPrice
            ) { purchasedAmount ->
                viewModel.buyCrypto(clickedCrypto, purchasedAmount)
                Toast.makeText(this, "Compra realizada com sucesso!", Toast.LENGTH_SHORT).show()
            }
            bottomSheet.show(supportFragmentManager, "BuyCryptoBottomSheet")
        }

        recyclerView.adapter = adapter
    }

    private fun setupSwipeRefresh() {
        swipeRefresh = findViewById(R.id.swipeRefresh)

        swipeRefresh.setOnRefreshListener {

            // Esconde a lista suavemente deixando apenas a rodinha visível
            recyclerView.animate().alpha(0f).setDuration(200).start()

            viewModel.refreshData()

            swipeRefresh.postDelayed({
                // Trava a lista no topo (Bitcoin)
                recyclerView.scrollToPosition(0)

                // Faz a lista reaparecer suavemente
                recyclerView.animate().alpha(1f).setDuration(300).start()

                swipeRefresh.isRefreshing = false
                Toast.makeText(this, "Mercado atualizado!", Toast.LENGTH_SHORT).show()
            }, 1500)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cryptoList.collect { cryptos ->

                    adapter.submitList(cryptos) {

                        // Executa isso apenas na primeira vez que o app abre e tem dados
                        if (isFirstLoad && cryptos.isNotEmpty()) {
                            isFirstLoad = false // Desliga a trava na mesma hora para não repetir

                            // Damos o mesmo tempo de 1.5s para o banco de dados processar tudo
                            // por baixo dos panos sem o usuário ver a tela pulando
                            recyclerView.postDelayed({

                                // Trava cravado no topo
                                recyclerView.scrollToPosition(0)

                                // Mostra a lista com o efeito de "Fade In"
                                recyclerView.animate().alpha(1f).setDuration(300).start()

                                // Desliga a rodinha do carregamento inicial
                                swipeRefresh.isRefreshing = false

                            }, 1500)
                        }
                    }
                }
            }
        }
    }
}