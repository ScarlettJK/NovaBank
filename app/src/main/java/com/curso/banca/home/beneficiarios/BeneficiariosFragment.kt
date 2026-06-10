package com.curso.banca.home.beneficiarios

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.curso.banca.databinding.FragmentBeneficiariosBinding
import com.curso.banca.network.RetrofitClient
import com.curso.banca.network.apiCall
import com.curso.banca.network.apiCallSinCuerpo
import kotlinx.coroutines.launch

class BeneficiariosFragment : Fragment() {

    private var _binding: FragmentBeneficiariosBinding? = null
    private val binding get() = _binding!!

    private val listaBeneficiarios = mutableListOf<Beneficiario>()
    private lateinit var adapter: BeneficiarioAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBeneficiariosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = BeneficiarioAdapter(
            listaBeneficiarios,
            mode = AdapterMode.NORMAL,

            onSelect = { beneficiario ->
                // Pasamos TODOS los datos para que el editor los precargue
                val intent = Intent(requireContext(), AddBeneficiarioActivity::class.java)
                intent.putExtra("ID", beneficiario.id)
                intent.putExtra("NOMBRE", beneficiario.nombre)
                intent.putExtra("APELLIDO", beneficiario.apellido)
                intent.putExtra("CUENTA", beneficiario.cuenta)
                intent.putExtra("ALIAS", beneficiario.banco)
                startActivity(intent)
            },

            onDelete = { b ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar beneficiario")
                    .setMessage("¿Eliminar a ${b.nombreCompleto}?")
                    .setPositiveButton("Eliminar") { _, _ -> eliminarBeneficiario(b) }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        binding.rvBeneficiarios.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBeneficiarios.adapter = adapter

        binding.btnAddBeneficiario.setOnClickListener { abrirAgregarBeneficiario() }
        binding.btnAgregarPrimero.setOnClickListener { abrirAgregarBeneficiario() }

        // Buscador: filtra por nombre, alias o cuenta
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { adapter.filtrar(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        })

        actualizarEstado()
    }

    override fun onResume() {
        super.onResume()
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

                    listaBeneficiarios.clear()
                    listaBeneficiarios.addAll(lista)
                    adapter.actualizar(listaBeneficiarios)
                    actualizarEstado()
                }
                .onFailure { e ->
                    if (_binding == null) return@launch
                    Toast.makeText(requireContext(),
                        "Error al cargar beneficiarios: ${e.message}",
                        Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun abrirAgregarBeneficiario() {
        startActivity(Intent(requireContext(), AddBeneficiarioActivity::class.java))
    }

    private fun eliminarBeneficiario(beneficiario: Beneficiario) {
        viewLifecycleOwner.lifecycleScope.launch {
            apiCallSinCuerpo { RetrofitClient.api.deleteBeneficiary(beneficiario.id) }
                .onSuccess {
                    if (_binding == null) return@launch
                    listaBeneficiarios.remove(beneficiario)
                    adapter.actualizar(listaBeneficiarios)
                    actualizarEstado()
                    Toast.makeText(requireContext(),
                        "Beneficiario eliminado", Toast.LENGTH_SHORT).show()
                }
                .onFailure { e ->
                    if (_binding == null) return@launch
                    Toast.makeText(requireContext(),
                        "No se pudo eliminar: ${e.message}",
                        Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun actualizarEstado() {
        val empty = listaBeneficiarios.isEmpty()
        binding.layoutEmptyBeneficiarios.visibility = if (empty) View.VISIBLE else View.GONE
        binding.rvBeneficiarios.visibility = if (empty) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}