package com.capstone.fishguard.ui.komunitas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.capstone.fishguard.databinding.FragmentDetailStoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailStoryFragment : Fragment() {

    private var _binding: FragmentDetailStoryBinding? = null
    private val binding get() = _binding!!

    private val arguments by navArgs<DetailStoryFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEnterTransition()

        handleBackNavigation()

        displayStoryDetails()
    }

    private fun setupEnterTransition() {
        postponeEnterTransition()
        binding.ivDetailPhoto.post { startPostponedEnterTransition() }
    }

    private fun handleBackNavigation() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    private fun displayStoryDetails() {
        val storyDetails = arguments.story

        loadImage(storyDetails.photoUrl)
        setSharedElementTransitionNames(storyDetails.id)
        bindStoryContent(storyDetails.username, storyDetails.description)
    }

    private fun loadImage(photoUrl: String) {
        Glide.with(requireContext())
            .load(photoUrl)
            .into(binding.ivDetailPhoto)
    }

    private fun setSharedElementTransitionNames(storyId: String) {
        ViewCompat.setTransitionName(binding.ivDetailPhoto, "image_$storyId")
        ViewCompat.setTransitionName(binding.tvDetailName, "name_$storyId")
        ViewCompat.setTransitionName(binding.tvDetailDescription, "description_$storyId")
    }

    private fun bindStoryContent(name: String, description: String) {
        binding.tvDetailName.text = name
        binding.tvDetailDescription.text = description
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
