package com.smartpixel.themovielist.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.smartpixel.themovielist.R
import com.smartpixel.themovielist.databinding.FragmentHomeBinding
import com.smartpixel.themovielist.databinding.ItemMovieBinding
import com.smartpixel.themovielist.databinding.ItemLoadStateBinding
import com.smartpixel.themovielist.domain.model.Movie
import com.smartpixel.themovielist.presentation.home.HomeIntent
import com.smartpixel.themovielist.presentation.home.HomeState
import com.smartpixel.themovielist.presentation.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var adapter: MovieAdapter
    private var linearLayoutManager: LinearLayoutManager? = null
    private var gridLayoutManager: GridLayoutManager? = null

    companion object {
        private const val KEY_LINEAR_STATE = "linear_state"
        private const val KEY_GRID_STATE = "grid_state"
        private const val KEY_LAYOUT_TYPE = "layout_type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = MovieAdapter(
            onFavoriteClick = { movie, position ->
                viewModel.processIntent(HomeIntent.ToggleFavorite(movie.id, !movie.isFavorite))
                val updatedMovie = movie.copy(isFavorite = !movie.isFavorite)
                adapter.notifyItemChanged(position)
            },
            onItemClick = { movie ->
                Log.d("HomeFragment", "Navigating to DetailsFragment with movieId: ${movie.id}")
                val bundle = Bundle().apply {
                    putInt("movieId", movie.id)
                }
                try {
                    findNavController().navigate(R.id.action_homeFragment_to_detailsFragment, bundle)
                } catch (e: Exception) {
                    Log.e("HomeFragment", "Navigation failed: ${e.message}")
                    Toast.makeText(context, "Navigation failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeState()
        observeMovies()
        binding.toggleLayout.setOnClickListener {
            viewModel.processIntent(HomeIntent.ToggleLayout)
        }

        // Restore layout type and scroll state
        val isGridLayout = savedInstanceState?.getBoolean(KEY_LAYOUT_TYPE) ?: viewModel.state.value.isGridLayout
        linearLayoutManager = LinearLayoutManager(context)
        gridLayoutManager = GridLayoutManager(context, 2)
        binding.recyclerView.layoutManager = if (isGridLayout) gridLayoutManager else linearLayoutManager

        // Restore scroll position
        if (savedInstanceState != null) {
            if (isGridLayout) {
                gridLayoutManager?.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_GRID_STATE))
            } else {
                linearLayoutManager?.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_LINEAR_STATE))
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadStateAdapter { adapter.retry() }
        )

        // Handle load states
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                val refreshState = loadStates.refresh
                binding.loadingIndicator.isVisible = refreshState is LoadState.Loading
                binding.errorMessage.isVisible = refreshState is LoadState.Error
                binding.recyclerView.isVisible = refreshState !is LoadState.Error

                if (refreshState is LoadState.Error) {
                    binding.errorMessage.text = refreshState.error.localizedMessage ?: "An error occurred"
                }

                // Handle empty state
                val isEmpty = refreshState is LoadState.NotLoading && adapter.itemCount == 0
                if (isEmpty) {
                    binding.errorMessage.isVisible = true
                    binding.errorMessage.text = "No movies available"
                    binding.recyclerView.isVisible = false
                }
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                // Update layout manager based on isGridLayout
                val newLayoutManager = if (state.isGridLayout) {
                    if (binding.recyclerView.layoutManager !is GridLayoutManager) {
                        // Save current scroll state before switching
                        val currentState = binding.recyclerView.layoutManager?.onSaveInstanceState()
                        gridLayoutManager = GridLayoutManager(context, 2).apply {
                            if (currentState != null) {
                                val position = if (binding.recyclerView.layoutManager is LinearLayoutManager) {
                                    (binding.recyclerView.layoutManager as LinearLayoutManager)
                                        .findFirstCompletelyVisibleItemPosition()
                                } else {
                                    (binding.recyclerView.layoutManager as GridLayoutManager)
                                        .findFirstCompletelyVisibleItemPosition()
                                }
                                scrollToPosition(position)
                            }
                        }
                        gridLayoutManager
                    } else {
                        gridLayoutManager
                    }
                } else {
                    if (binding.recyclerView.layoutManager !is LinearLayoutManager) {
                        // Save current scroll state before switching
                        val currentState = binding.recyclerView.layoutManager?.onSaveInstanceState()
                        linearLayoutManager = LinearLayoutManager(context).apply {
                            if (currentState != null) {
                                val position = if (binding.recyclerView.layoutManager is GridLayoutManager) {
                                    (binding.recyclerView.layoutManager as GridLayoutManager)
                                        .findFirstCompletelyVisibleItemPosition()
                                } else {
                                    (binding.recyclerView.layoutManager as LinearLayoutManager)
                                        .findFirstCompletelyVisibleItemPosition()
                                }
                                scrollToPosition(position)
                            }
                        }
                        linearLayoutManager
                    } else {
                        linearLayoutManager
                    }
                }
                binding.recyclerView.layoutManager = newLayoutManager
                binding.toggleLayout.setImageResource(
                    if (state.isGridLayout) R.drawable.ic_list_view else R.drawable.ic_grid_view
                )

                // Show error messages (e.g., from toggleFavorite)
                state.errorMessage?.let { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    viewModel.clearErrorMessage()
                }
            }
        }
    }

    private fun observeMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                state.movies?.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the current layout type
        outState.putBoolean(KEY_LAYOUT_TYPE, viewModel.state.value.isGridLayout)
        // Save the scroll state for both layout managers
        linearLayoutManager?.let {
            outState.putParcelable(KEY_LINEAR_STATE, it.onSaveInstanceState())
        }
        gridLayoutManager?.let {
            outState.putParcelable(KEY_GRID_STATE, it.onSaveInstanceState())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // Clear layout managers to prevent memory leaks
        linearLayoutManager = null
        gridLayoutManager = null
    }
}

class MovieAdapter(
    private val onFavoriteClick: (Movie, Int) -> Unit,
    private val onItemClick: (Movie) -> Unit
) : PagingDataAdapter<Movie, MovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding, onFavoriteClick, onItemClick)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, position) }
    }
}

class MovieViewHolder(
    private val binding: ItemMovieBinding,
    private val onFavoriteClick: (Movie, Int) -> Unit,
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(movie: Movie, position: Int) {
        binding.title.text = movie.title
        binding.releaseDate.text = movie.releaseDate ?: "N/A"
        Glide.with(binding.poster)
            .load(movie.posterPath)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.poster)
        binding.favoriteIcon.setImageResource(
            if (movie.isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
        )
        binding.favoriteIcon.setOnClickListener { onFavoriteClick(movie, position) }
        binding.root.setOnClickListener { onItemClick(movie) }
    }
}

class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
}

class LoadStateAdapter(
    private val retry: () -> Unit
) : androidx.paging.LoadStateAdapter<LoadStateViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = ItemLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
}

class LoadStateViewHolder(
    private val binding: ItemLoadStateBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.retryButton.setOnClickListener { retry() }
    }

    fun bind(loadState: LoadState) {
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.errorMessage.isVisible = loadState is LoadState.Error
        if (loadState is LoadState.Error) {
            binding.errorMessage.text = loadState.error.localizedMessage ?: "An error occurred"
        }
    }
}