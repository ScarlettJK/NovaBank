package com.curso.banca.home.inicio

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.curso.banca.home.transferencia.TransferirActivity
import com.curso.banca.databinding.FragmentHomeBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.curso.banca.MovimientoAdapter
import com.curso.banca.data.repository.UsuarioRepository
import com.curso.banca.network.ApiException
import com.curso.banca.network.RetrofitClient
import com.curso.banca.network.apiCall
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import android.widget.Toast
import java.text.NumberFormat
import java.util.Locale

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

        // El RecyclerView está vacío, mostramos el empty state
        binding.layoutEmptyMovimientos.visibility = View.VISIBLE
        binding.rvMovimientos.visibility = View.GONE

        adapter = MovimientoAdapter(emptyList())

        binding.rvMovimientos.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvMovimientos.adapter = adapter

        binding.btnTransferir.setOnClickListener {
            startActivity(Intent(requireContext(), TransferirActivity::class.java))
        }
        cargarUsuario()
        cargarCuenta()
        cargarMovimientos()
    }

    /** Muestra el nombre del usuario logueado (perfil guardado en Firestore). */
    private fun cargarUsuario() {
        val user = FirebaseAuth.getInstance().currentUser ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val usuario = usuarioRepo.obtener(user.uid)
            if (_binding == null) return@launch   // la vista ya se destruyó

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

            // Si el backend dice que no hay cuenta (p. ej. falló createAccount
            // durante el registro), la creamos y volvemos a pedir el saldo.
            val error = resultado.exceptionOrNull()
            if (error is ApiException && error.statusCode == 404) {
                apiCall { RetrofitClient.api.createAccount() }
                resultado = apiCall { RetrofitClient.api.getAccount() }
            }

            if (_binding == null) return@launch

            resultado
                .onSuccess { cuenta ->
                    val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
                    binding.tvSaldoMonto.text = fmt.format(cuenta.balance / 100.0)
                    binding.tvNumeroCuenta.text =
                        "MXN · Cuenta **** ${cuenta.accountNumber.takeLast(4)}"
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

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}