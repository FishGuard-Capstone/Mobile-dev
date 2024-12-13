package com.capstone.fishguard.ui.komunitas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.fishguard.R
import com.capstone.fishguard.databinding.FragmentListStoryBinding
import com.capstone.fishguard.adapter.StoryAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ListStoryFragment : Fragment() {

    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!
    private val storyViewModel: ListStoryViewModel by viewModels()
    private lateinit var adapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerView()
        setupRefreshListener()
        observeViewModelData()
        setupFabAction()

        if (storyViewModel.stories.value.isNullOrEmpty()) {
            storyViewModel.loadStories()
        }
    }

    private fun initializeRecyclerView() {
        adapter = StoryAdapter { story, photoView, nameView, descriptionView ->
            val sharedElements = FragmentNavigatorExtras(
                photoView to "image_${story.id}",
                nameView to "name_${story.id}",
                descriptionView to "description_${story.id}"
            )
            val direction = ListStoryFragmentDirections.actionListStoryFragmentToDetailStoryFragment(story)
            findNavController().navigate(direction, sharedElements)
        }

        binding.rvStoryList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ListStoryFragment.adapter
        }
    }

    private fun setupRefreshListener() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            storyViewModel.loadStories()
        }
    }

    private fun setupFabAction() {
        binding.fabAddStory.setOnClickListener {
            findNavController().navigate(R.id.action_listStoryFragment_to_addStoryFragment)
        }
    }

    private fun observeViewModelData() {
        storyViewModel.stories.observe(viewLifecycleOwner) { storyList ->
            adapter.submitList(storyList)
            binding.swipeRefreshLayout.isRefreshing = false
            binding.emptyStateView.visibility = if (storyList.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        storyViewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        storyViewModel.errorState.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                storyViewModel.clearErrorState()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
