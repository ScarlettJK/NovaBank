package com.curso.banca.home.beneficiarios

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.curso.banca.databinding.FragmentBeneficiariosBinding
import androidx.lifecycle.lifecycleScope
import com.curso.banca.network.RetrofitClient
import kotlinx.coroutines.launch
import android.content.Intent
import com.curso.banca.home.beneficiarios.AddBeneficiarioActivity


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

                val intent = Intent(
                    requireContext(),
                    AddBeneficiarioActivity::class.java
                )

                intent.putExtra("ID", beneficiario.id)
                intent.putExtra("NOMBRE", beneficiario.nombre)
                intent.putExtra("ALIAS", beneficiario.banco)
                intent.putExtra("CUENTA", beneficiario.cuentaOculta)
                startActivity(intent)
            },

            onDelete = { b ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar beneficiario")
                    .setMessage("¿Eliminar a ${b.nombre}?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        listaBeneficiarios.remove(b)
                        actualizarEstado(adapter)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        binding.rvBeneficiarios.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBeneficiarios.adapter = adapter
        binding.btnAddBeneficiario.setOnClickListener {
            abrirAgregarBeneficiario()
        }

        binding.btnAgregarPrimero.setOnClickListener {
            abrirAgregarBeneficiario()
        }
        actualizarEstado(adapter)
        cargarBeneficiarios()
    }


    override fun onResume() {
        super.onResume()
        cargarBeneficiarios()
    }


    private fun cargarBeneficiarios() {
        lifecycleScope.launch {
            try {

                val respuesta =
                    RetrofitClient.api.getBeneficiaries()

                android.util.Log.d(
                    "BENEFICIARIOS",
                    respuesta.toString()
                )

                val lista =
                    respuesta.map {

                        Beneficiario(
                            id = it.id,
                            nombre = "${it.name} ${it.lastName}",
                            banco = it.alias,
                            cuentaOculta =
                                "****${it.accountNumber.takeLast(4)}"
                        )
                    }

                android.util.Log.d("BENEFICIARIOS", "respuesta size = ${respuesta.size}")
                android.util.Log.d("BENEFICIARIOS", "lista size = ${lista.size}")

                listaBeneficiarios.clear()
                listaBeneficiarios.addAll(lista)

                android.util.Log.d(
                    "BENEFICIARIOS",
                    "listaBeneficiarios size = ${listaBeneficiarios.size}"
                )

                adapter.actualizar(listaBeneficiarios)

                binding.rvBeneficiarios.visibility = View.VISIBLE
                binding.layoutEmptyBeneficiarios.visibility = View.GONE


                actualizarEstado(adapter)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun abrirAgregarBeneficiario() {
        startActivity(
            Intent(
                requireContext(),
                AddBeneficiarioActivity::class.java
            )
        )
    }


    private fun eliminarBeneficiario(
        beneficiario: Beneficiario
    ) {
        lifecycleScope.launch {

            try {

                RetrofitClient.api.deleteBeneficiary(
                    beneficiario.id
                )

                listaBeneficiarios.remove(beneficiario)

                adapter.actualizar(listaBeneficiarios)

                actualizarEstado(adapter)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun actualizarEstado(adapter: BeneficiarioAdapter) {
        val empty = listaBeneficiarios.isEmpty()
        binding.layoutEmptyBeneficiarios.visibility = if (empty) View.VISIBLE else View.GONE
        binding.rvBeneficiarios.visibility = if (empty) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}