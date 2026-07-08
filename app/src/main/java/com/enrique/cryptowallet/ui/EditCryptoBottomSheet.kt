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

class EditCryptoBottomSheet(
    private val cryptoName: String,
    private val currentQuantity: Double,
    private val onSaveConfirmed: (Double) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tvEditTitle)
        val tvQuantity = view.findViewById<TextView>(R.id.tvCurrentQuantity)
        val etNewQuantity = view.findViewById<EditText>(R.id.etNewQuantity)
        val btnSave = view.findViewById<Button>(R.id.btnSaveEdit)

        tvTitle.text = "Editar $cryptoName"
        tvQuantity.text = "Saldo atual: $currentQuantity"

        etNewQuantity.setText(currentQuantity.toString())

        btnSave.setOnClickListener {
            val quantityText = etNewQuantity.text.toString()

            if (quantityText.isNotEmpty()) {
                val newQuantity = quantityText.toDoubleOrNull()

                if (newQuantity != null && newQuantity >= 0) {
                    onSaveConfirmed(newQuantity)
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