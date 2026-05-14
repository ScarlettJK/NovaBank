package com.app.novabank

import android.os.Bundle
import android.text.*
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app.novabank.databinding.FragmentMontoConceptoBinding
import com.google.android.material.snackbar.Snackbar


class MontoConceptoFragment : Fragment() {

    private var _binding: FragmentMontoConceptoBinding? = null
    private val binding get() = _binding!!
    private val args: MontoConceptoFragmentArgs by navArgs()
    private val saldoDisponible = 12450.75

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMontoConceptoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mostrar datos del beneficiario recibidos por argumento
        binding.tvBeneficiarioNombre.text = args.beneficiarioNombre
        binding.tvBeneficiarioBanco.text  = args.beneficiarioBanco

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.cardBeneficiarioSeleccionado.setOnClickListener { findNavController().popBackStack() }


        // ChipGroup: autocompletar monto
        val montos = mapOf("$500" to "500", "$1,000" to "1000", "$1,500" to "1500", "$5K" to "5000")
        binding.chipGroupSugeridos.setOnCheckedStateChangeListener { group, _ ->
            val chipId = group.checkedChipId
            if (chipId != View.NO_ID) {
                val chip = group.findViewById<com.google.android.material.chip.Chip>(chipId)
                val m = montos[chip.text.toString()] ?: ""
                binding.etMonto.setText("$ $m")
                validarMonto(m.toDoubleOrNull() ?: 0.0)
            }
        }

        // TextWatcher para validar en tiempo real
        binding.etMonto.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val raw = s.toString().replace("$", "").replace(",", "").trim()
                validarMonto(raw.toDoubleOrNull() ?: 0.0)
            }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        })

        binding.btnTransferir.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmar transferencia")
                .setMessage("¿Transferir ${binding.btnTransferir.text} a ${args.beneficiarioNombre}?")
                .setPositiveButton("Transferir") { _, _ ->
                    Snackbar.make(binding.root, "Transferencia realizada con éxito ✓", Snackbar.LENGTH_LONG).show()
                    requireActivity().finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun validarMonto(monto: Double) {
        when {
            monto <= 0 -> {
                binding.btnTransferir.isEnabled = false
                binding.btnTransferir.text = "Transferir"
            }
            monto > saldoDisponible -> {
                binding.btnTransferir.isEnabled = false
                binding.tvSaldoDisponible.text = "⚠ El monto excede tu saldo"
                binding.tvSaldoDisponible.setTextColor(resources.getColor(R.color.errorText, null))
            }
            else -> {
                binding.btnTransferir.isEnabled = true
                binding.btnTransferir.text = "Transferir $%.2f".format(monto)
                binding.tvSaldoDisponible.text = "✓ Saldo disponible: $12,450.75 MXN"
                binding.tvSaldoDisponible.setTextColor(resources.getColor(R.color.success, null))
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
