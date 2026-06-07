package com.curso.banca.home.inicio

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.curso.banca.home.transferencia.TransferirActivity
import com.curso.banca.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // El RecyclerView está vacío, mostramos el empty state
        binding.layoutEmptyMovimientos.visibility = View.VISIBLE
        binding.rvMovimientos.visibility = View.GONE

        binding.btnTransferir.setOnClickListener {
            startActivity(Intent(requireContext(), TransferirActivity::class.java))
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}