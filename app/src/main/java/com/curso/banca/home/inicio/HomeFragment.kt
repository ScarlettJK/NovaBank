package com.curso.banca.home.inicio

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.curso.banca.MovimientoAdapter
import com.curso.banca.data.repository.UsuarioRepository
import com.curso.banca.databinding.FragmentHomeBinding
import com.curso.banca.home.transferencia.TransferirActivity
import com.curso.banca.network.ApiException
import com.curso.banca.network.FundRequest
import com.curso.banca.network.RetrofitClient
import com.curso.banca.network.apiCall
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToLong

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MovimientoAdapter

    private val usuarioRepo = UsuarioRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutEmptyMovimientos.visibility = View.VISIBLE
        binding.rvMovimientos.visibility = View.GONE

        adapter = MovimientoAdapter(emptyList())
        binding.rvMovimientos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMovimientos.adapter = adapter

        binding.btnTransferir.setOnClickListener {
            startActivity(Intent(requireContext(), TransferirActivity::class.java))
        }

        binding.btnFondear.setOnClickListener { mostrarDialogoFondear() }

        cargarUsuario()
    }

    override fun onResume() {
        super.onResume()
        // Se recargan al volver de Transferir/Fondear para reflejar el nuevo saldo y movimientos
        cargarCuenta()
        cargarMovimientos()
    }

    /** Muestra el nombre del usuario logueado (perfil guardado en Firestore). */
    private fun cargarUsuario() {
        val user = FirebaseAuth.getInstance().currentUser ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val usuario = usuarioRepo.obtener(user.uid)
            if (_binding == null) return@launch

            binding.tvUserName.text = when {
                usuario != null && usuario.firstName.isNotEmpty() -> usuario.firstName
                usuario != null && usuario.fullName.isNotEmpty()  -> usuario.fullName
                else -> user.email ?: ""           // fallback si no completó su perfil
            }
        }
    }

    /** Carga el saldo; si la cuenta aún no existe en el backend, la crea y reintenta. */
    private fun cargarCuenta() {
        viewLifecycleOwner.lifecycleScope.launch {
            var resultado = apiCall { RetrofitClient.api.getAccount() }

            val error = resultado.exceptionOrNull()
            if (error is ApiException && error.errorCode == "no_account") {
                apiCall { RetrofitClient.api.createAccount() }
                resultado = apiCall { RetrofitClient.api.getAccount() }
            }

            if (_binding == null) return@launch

            resultado
                .onSuccess { cuenta ->
                    val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
                    binding.tvSaldoMonto.text = fmt.format(cuenta.balance / 100.0)

                    // Se muestra enmascarado por seguridad visual, pero al
                    // tocarlo se copia el número COMPLETO al portapapeles.
                    binding.tvNumeroCuenta.text =
                        "MXN · Cuenta **** ${cuenta.accountNumber.takeLast(4)} (toca para copiar)"
                    binding.tvNumeroCuenta.setOnClickListener {
                        val clipboard = requireContext()
                            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(
                            ClipData.newPlainText("Número de cuenta", cuenta.accountNumber))
                        Toast.makeText(requireContext(),
                            "Número de cuenta completo copiado", Toast.LENGTH_SHORT).show()
                    }
                }
                .onFailure { e ->
                    binding.tvSaldoMonto.text = "—"
                    Toast.makeText(requireContext(),
                        "Error al cargar cuenta: ${e.message}",
                        Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun cargarMovimientos() {
        viewLifecycleOwner.lifecycleScope.launch {
            apiCall { RetrofitClient.api.getTransactions() }
                .onSuccess { movimientos ->
                    if (_binding == null) return@launch
                    if (movimientos.isEmpty()) {
                        binding.layoutEmptyMovimientos.visibility = View.VISIBLE
                        binding.rvMovimientos.visibility = View.GONE
                    } else {
                        binding.layoutEmptyMovimientos.visibility = View.GONE
                        binding.rvMovimientos.visibility = View.VISIBLE
                        adapter.actualizarLista(movimientos)
                    }
                }
                .onFailure { e ->
                    if (_binding == null) return@launch
                    Toast.makeText(requireContext(),
                        "Error al cargar movimientos: ${e.message}",
                        Toast.LENGTH_SHORT).show()
                }
        }
    }

    // ---------- Fondear ----------

    private fun mostrarDialogoFondear() {
        val input = EditText(requireContext()).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Ej. 500.00"
        }
        val container = FrameLayout(requireContext()).apply {
            val pad = (24 * resources.displayMetrics.density).toInt()
            setPadding(pad, pad / 2, pad, 0)
            addView(input)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Fondear cuenta")
            .setMessage("¿Cuánto deseas depositar (MXN)?")
            .setView(container)
            .setPositiveButton("Fondear") { _, _ ->
                val monto = input.text.toString().toDoubleOrNull()
                if (monto == null || monto <= 0) {
                    Toast.makeText(requireContext(),
                        "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
                } else {
                    fondear(monto)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun fondear(montoPesos: Double) {
        binding.btnFondear.isEnabled = false
        viewLifecycleOwner.lifecycleScope.launch {
            apiCall {
                RetrofitClient.api.fundAccount(
                    FundRequest(amount = (montoPesos * 100).roundToLong())
                )
            }
                .onSuccess {
                    if (_binding == null) return@launch
                    binding.btnFondear.isEnabled = true
                    val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
                    Toast.makeText(requireContext(),
                        "Fondeo de ${fmt.format(montoPesos)} exitoso ✓",
                        Toast.LENGTH_SHORT).show()
                    cargarCuenta()
                    cargarMovimientos()
                }
                .onFailure { e ->
                    if (_binding == null) return@launch
                    binding.btnFondear.isEnabled = true
                    Toast.makeText(requireContext(),
                        "No se pudo fondear: ${e.message}",
                        Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}