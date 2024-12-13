package com.capstone.fishguard.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.capstone.fishguard.databinding.FragmentHomeBinding
import com.capstone.fishguard.ui.identifikasi.IdentifikasiIkanActivity
import com.capstone.fishguard.ui.newsapi.BeritaKelautanActivity
import com.capstone.fishguard.ui.komunitas.KomunitasActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Observe data from ViewModel if needed
        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listeners for each card
        binding.cardIdentifikasiIkan.setOnClickListener {
            val intent = Intent(requireContext(), IdentifikasiIkanActivity::class.java)
            startActivity(intent)
        }

        binding.cardBeritaKelautan.setOnClickListener {
            val intent = Intent(requireContext(), BeritaKelautanActivity::class.java)
            startActivity(intent)
        }

        binding.cardKomunitasPerikanan.setOnClickListener {
            val intent = Intent(requireContext(), KomunitasActivity::class.java)
            startActivity(intent)
        }

        binding.cardComingSoon.setOnClickListener {
            viewModel.showComingSoonMessage()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
