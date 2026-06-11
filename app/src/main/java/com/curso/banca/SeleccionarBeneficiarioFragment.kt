package com.curso.banca

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.curso.banca.databinding.FragmentSeleccionarBeneficiarioBinding
import com.curso.banca.home.beneficiarios.AdapterMode
import com.curso.banca.home.beneficiarios.AddBeneficiarioActivity
import com.curso.banca.home.beneficiarios.Beneficiario
import com.curso.banca.home.beneficiarios.BeneficiarioAdapter
import com.curso.banca.network.RetrofitClient
import com.curso.banca.network.apiCall
import kotlinx.coroutines.launch

class SeleccionarBeneficiarioFragment : Fragment() {

    private var _binding: FragmentSeleccionarBeneficiarioBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BeneficiarioAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSeleccionarBeneficiarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { requireActivity().finish() }

        adapter = BeneficiarioAdapter(
            mutableListOf(),
            mode = AdapterMode.SELECT,
            onSelect = { b ->
                val bundle = Bundle().apply {
                    putString("beneficiarioId", b.id)
                    putString("beneficiarioNombre", b.nombreCompleto)
                    putString("beneficiarioBanco", b.banco)
                    putString("beneficiarioCuenta", b.cuenta)
                }
                findNavController().navigate(R.id.action_seleccionar_a_monto, bundle)
            }
        )

        binding.rvBeneficiariosSelector.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBeneficiariosSelector.adapter = adapter

        // Buscador: filtra por nombre, alias o cuenta
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { adapter.filtrar(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        })

        // Agregar un nuevo beneficiario desde el flujo de transferencia
        binding.cardAddNuevo.setOnClickListener {
            startActivity(Intent(requireContext(), AddBeneficiarioActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Se recarga al volver (p. ej. después de agregar uno nuevo)
        cargarBeneficiarios()
    }

    private fun cargarBeneficiarios() {
        viewLifecycleOwner.lifecycleScope.launch {
            apiCall { RetrofitClient.api.getBeneficiaries() }
                .onSuccess { respuesta ->
                    if (_binding == null) return@launch
                    val lista = respuesta.map {
                        Beneficiario(
                            id = it.id,
                            nombre = it.name,
                            apellido = it.lastName,
                            banco = it.alias,
                            cuenta = it.accountNumber
                        )
                    }
                    adapter.actualizar(lista)
                }
                .onFailure { e ->
                    if (_binding == null) return@launch
                    Toast.makeText(requireContext(),
                        "Error al cargar beneficiarios: ${e.message}",
                        Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}