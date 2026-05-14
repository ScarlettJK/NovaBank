package com.app.novabank.home.beneficiarios

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.novabank.home.beneficiarios.AdapterMode
import com.app.novabank.home.beneficiarios.Beneficiario
import com.app.novabank.home.beneficiarios.BeneficiarioAdapter
import com.app.novabank.databinding.FragmentBeneficiariosBinding

class BeneficiariosFragment : Fragment() {

    private var _binding: FragmentBeneficiariosBinding? = null
    private val binding get() = _binding!!

    private val listaBeneficiarios = mutableListOf(
        Beneficiario(1, "Juan Carlos López", "BBVA", "****4521"),
        Beneficiario(2, "Ana Sofía Méndez", "Banorte", "****8832"),
        Beneficiario(3, "Roberto García", "Santander", "****1209")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        binding.rvBeneficiarios.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvBeneficiarios.adapter = adapter

        actualizarEstado(adapter)
    }

    private fun actualizarEstado(adapter: BeneficiarioAdapter) {
        val empty = listaBeneficiarios.isEmpty()

        binding.layoutEmptyBeneficiarios.visibility =
            if (empty) View.VISIBLE else View.GONE

        binding.rvBeneficiarios.visibility =
            if (empty) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}