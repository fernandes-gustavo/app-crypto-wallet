package com.enrique.cryptowallet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.enrique.cryptowallet.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BuyCryptoBottomSheet(
    private val cryptoName: String,
    private val cryptoPrice: Double,
    private val onBuyConfirmed: (Double) -> Unit // Uma função que devolve a quantidade digitada
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.bottom_sheet_buy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tvBuyTitle)
        val tvPrice = view.findViewById<TextView>(R.id.tvBuyPrice)
        val etQuantity = view.findViewById<EditText>(R.id.etQuantity)
        val btnConfirm = view.findViewById<Button>(R.id.btnConfirmBuy)

        tvTitle.text = "Comprar $cryptoName"
        tvPrice.text = "Preço atual: $ $cryptoPrice"

        btnConfirm.setOnClickListener {
            val quantityText = etQuantity.text.toString()

            if (quantityText.isNotEmpty()) {
                val quantity = quantityText.toDoubleOrNull()

                if (quantity != null && quantity > 0) {
                    // Devolve o valor para a MainActivity e fecha a janelinha
                    onBuyConfirmed(quantity)
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Digite um valor válido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "A quantidade não pode ser vazia", Toast.LENGTH_SHORT).show()
            }
        }
    }
}