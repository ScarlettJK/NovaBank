package com.app.novabank

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.novabank.databinding.FragmentSeleccionarBeneficiarioBinding

class SeleccionarBeneficiarioFragment : Fragment() {

    private var _binding: FragmentSeleccionarBeneficiarioBinding? = null
    private val binding get() = _binding!!

    private val beneficiarios = mutableListOf(
        Beneficiario(1, "Juan Carlos López", "BBVA", "****4521"),
        Beneficiario(2, "Ana Sofía Méndez", "Banorte", "****8832"),
        Beneficiario(3, "Roberto García", "Santander", "****1209")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSeleccionarBeneficiarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { requireActivity().finish() }

        val adapter = BeneficiarioAdapter(
            beneficiarios,
            mode = AdapterMode.SELECT,
            onSelect = { b ->
                val action = SeleccionarBeneficiarioFragmentDirections
                    .actionSeleccionarAMonto(b.id, b.nombre, b.banco)
                findNavController().navigate(action)
            }
        )

        binding.rvBeneficiariosSelector.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBeneficiariosSelector.adapter = adapter
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

