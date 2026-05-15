package com.app.novabank.home.beneficiarios

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.novabank.databinding.FragmentBeneficiariosBinding

class BeneficiariosFragment : Fragment() {

    private var _binding: FragmentBeneficiariosBinding? = null
    private val binding get() = _binding!!

    private val listaBeneficiarios = mutableListOf(
        Beneficiario(id = "1", nombre = "Juan Carlos López", banco = "BBVA",      cuentaOculta = "****4521"),
        Beneficiario(id = "2", nombre = "Ana Sofía Méndez",  banco = "Banorte",   cuentaOculta = "****8832"),
        Beneficiario(id = "3", nombre = "Roberto García",    banco = "Santander", cuentaOculta = "****1209")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBeneficiariosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lateinit var adapter: BeneficiarioAdapter

        adapter = BeneficiarioAdapter(
            listaBeneficiarios,
            mode = AdapterMode.NORMAL,
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
        actualizarEstado(adapter)
    }

    private fun actualizarEstado(adapter: BeneficiarioAdapter) {
        val empty = listaBeneficiarios.isEmpty()
        binding.layoutEmptyBeneficiarios.visibility = if (empty) View.VISIBLE else View.GONE
        binding.rvBeneficiarios.visibility = if (empty) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}