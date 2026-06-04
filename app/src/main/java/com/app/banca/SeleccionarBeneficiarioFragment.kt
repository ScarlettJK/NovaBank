package com.app.banca

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.banca.databinding.FragmentSeleccionarBeneficiarioBinding
import com.app.banca.home.beneficiarios.AdapterMode
import com.app.banca.home.beneficiarios.Beneficiario
import com.app.banca.home.beneficiarios.BeneficiarioAdapter

class SeleccionarBeneficiarioFragment : Fragment() {

    private var _binding: FragmentSeleccionarBeneficiarioBinding? = null
    private val binding get() = _binding!!

    // id ahora es String — compatible con Firestore
    private val beneficiarios = mutableListOf(
        Beneficiario(id = "1", nombre = "Juan Carlos López", banco = "BBVA",      cuentaOculta = "****4521"),
        Beneficiario(id = "2", nombre = "Ana Sofía Méndez",  banco = "Banorte",   cuentaOculta = "****8832"),
        Beneficiario(id = "3", nombre = "Roberto García",    banco = "Santander", cuentaOculta = "****1209")
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
                val bundle = Bundle().apply {
                    putString("beneficiarioId",     b.id)
                    putString("beneficiarioNombre", b.nombre)
                    putString("beneficiarioBanco",  b.banco)
                }
                findNavController().navigate(R.id.action_seleccionar_a_monto, bundle)
            }
        )

        binding.rvBeneficiariosSelector.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBeneficiariosSelector.adapter = adapter
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}