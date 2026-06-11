package com.curso.banca

import android.os.Bundle
import android.text.*
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.curso.banca.databinding.FragmentMontoConceptoBinding
import com.curso.banca.network.RetrofitClient
import com.curso.banca.network.TransactionRequest
import com.curso.banca.network.apiCall
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToLong

class MontoConceptoFragment : Fragment() {

    private var _binding: FragmentMontoConceptoBinding? = null
    private val binding get() = _binding!!

    private val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    private var saldoCentavos: Long = 0L     // saldo real, cargado de la API
    private var saldoCargado = false
    private var montoPesos: Double = 0.0
    private var beneficiarioId = ""
    private var nombreDestino = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMontoConceptoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nombreDestino = arguments?.getString("beneficiarioNombre") ?: ""
        beneficiarioId = arguments?.getString("beneficiarioId") ?: ""
        val banco = arguments?.getString("beneficiarioBanco") ?: ""

        binding.tvBeneficiarioNombre.text = nombreDestino
        binding.tvBeneficiarioBanco.text  = banco

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnCambiar.setOnClickListener { findNavController().popBackStack() }
        binding.cardBeneficiarioSeleccionado.setOnClickListener { findNavController().popBackStack() }

        binding.tvSaldoDisponible.text = "Cargando saldo..."
        cargarSaldo()

        // Chips de montos sugeridos: click directo sobre cada chip
        // (los chips del XML no son checkable, por eso el listener de
        //  selección del ChipGroup nunca disparaba)
        for (i in 0 until binding.chipGroupSugeridos.childCount) {
            val chip = binding.chipGroupSugeridos.getChildAt(i) as? Chip ?: continue
            chip.setOnClickListener {
                // "$1,000" -> "1000" · "$5K" -> "5000"
                val monto = chip.text.toString()
                    .replace("$", "")
                    .replace(",", "")
                    .replace("K", "000", ignoreCase = true)
                    .trim()
                binding.etMonto.setText(monto)
                binding.etMonto.setSelection(binding.etMonto.text.length)
            }
        }

        binding.etMonto.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val raw = s.toString().replace("$", "").replace(",", "").trim()
                validarMonto(raw.toDoubleOrNull() ?: 0.0)
            }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        })

        binding.btnTransferir.setOnClickListener {
            if (beneficiarioId.isEmpty()) {
                Toast.makeText(requireContext(),
                    "No se encontró el beneficiario seleccionado",
                    Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmar transferencia")
                .setMessage("¿Transferir ${fmt.format(montoPesos)} a $nombreDestino?")
                .setPositiveButton("Transferir") { _, _ -> transferir() }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    /** Carga el saldo real de la cuenta desde la API. */
    private fun cargarSaldo() {
        viewLifecycleOwner.lifecycleScope.launch {
            apiCall { RetrofitClient.api.getAccount() }
                .onSuccess { cuenta ->
                    if (_binding == null) return@launch
                    saldoCentavos = cuenta.balance
                    saldoCargado = true
                    // Revalida el monto que esté escrito con el saldo ya cargado
                    val raw = binding.etMonto.text.toString()
                        .replace("$", "").replace(",", "").trim()
                    validarMonto(raw.toDoubleOrNull() ?: 0.0)
                }
                .onFailure { e ->
                    if (_binding == null) return@launch
                    binding.tvSaldoDisponible.text = "⚠ No se pudo cargar tu saldo"
                    binding.tvSaldoDisponible.setTextColor(
                        resources.getColor(R.color.errorText, null))
                    Toast.makeText(requireContext(),
                        "Error al cargar saldo: ${e.message}",
                        Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun validarMonto(monto: Double) {
        montoPesos = monto
        val centavos = (monto * 100).roundToLong()
        when {
            !saldoCargado || monto <= 0 -> {
                binding.btnTransferir.isEnabled = false
                binding.btnTransferir.text = "Transferir"
                if (saldoCargado) mostrarSaldoOk()
            }
            centavos > saldoCentavos -> {
                binding.btnTransferir.isEnabled = false
                binding.btnTransferir.text = "Transferir"
                binding.tvSaldoDisponible.text = "⚠ El monto excede tu saldo"
                binding.tvSaldoDisponible.setTextColor(
                    resources.getColor(R.color.errorText, null))
            }
            else -> {
                binding.btnTransferir.isEnabled = true
                binding.btnTransferir.text = "Transferir ${fmt.format(monto)}"
                mostrarSaldoOk()
            }
        }
    }

    private fun mostrarSaldoOk() {
        binding.tvSaldoDisponible.text =
            "✓ Saldo disponible: ${fmt.format(saldoCentavos / 100.0)} MXN"
        binding.tvSaldoDisponible.setTextColor(
            resources.getColor(R.color.success, null))
    }

    /** Ejecuta la transferencia real contra la API. */
    private fun transferir() {
        binding.btnTransferir.isEnabled = false

        val concepto = binding.etConcepto.editText?.text?.toString()?.trim()
            .takeUnless { it.isNullOrEmpty() }

        viewLifecycleOwner.lifecycleScope.launch {
            apiCall {
                RetrofitClient.api.createTransaction(
                    TransactionRequest(
                        toBeneficiaryId = beneficiarioId,
                        amount = (montoPesos * 100).roundToLong(),
                        description = concepto
                    )
                )
            }
                .onSuccess {
                    if (_binding == null) return@launch
                    Toast.makeText(requireContext(),
                        "Transferencia realizada con éxito ✓",
                        Toast.LENGTH_LONG).show()
                    // Al cerrar, Home recarga saldo y movimientos en onResume
                    requireActivity().finish()
                }
                .onFailure { e ->
                    if (_binding == null) return@launch
                    binding.btnTransferir.isEnabled = true
                    AlertDialog.Builder(requireContext())
                        .setTitle("No se pudo transferir")
                        .setMessage(e.message ?: "Error desconocido")
                        .setPositiveButton("Entendido", null)
                        .show()
                }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}