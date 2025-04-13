package com.smartpixel.themovielist.presentation.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.smartpixel.themovielist.R
import com.smartpixel.themovielist.presentation.details.DetailsViewModel
import com.smartpixel.themovielist.presentation.details.MovieDetailsState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private val viewModel: DetailsViewModel by viewModels()

    private lateinit var posterImage: ImageView
    private lateinit var titleText: TextView
    private lateinit var releaseDateText: TextView
    private lateinit var overviewText: TextView
    private lateinit var genresText: TextView
    private lateinit var runtimeText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)

        // Initialize views with updated IDs
        posterImage = view.findViewById(R.id.poster)
        titleText = view.findViewById(R.id.title)
        releaseDateText = view.findViewById(R.id.releaseDate)
        overviewText = view.findViewById(R.id.overview)
        genresText = view.findViewById(R.id.genres)
        runtimeText = view.findViewById(R.id.runtime)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add custom back press handling
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("DetailsFragment", "Back pressed, popping back stack to HomeFragment")
                findNavController().popBackStack(R.id.homeFragment, false)  // Pop back to HomeFragment
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        val movieId = arguments?.getInt("movieId") ?: return
        viewModel.loadMovieDetails(movieId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.movieDetails.collect { state ->
                when (state) {
                    is MovieDetailsState.Initial -> {
                        // Do nothing or show initial state
                    }
                    is MovieDetailsState.Loading -> {
                        Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
                    }
                    is MovieDetailsState.Success -> {
                        val details = state.details
                        titleText.text = details.title
                        releaseDateText.text = details.releaseDate ?: "N/A"
                        overviewText.text = details.overview ?: "No description available"
                        genresText.text = details.genres?.ifEmpty { "N/A" } ?: "N/A"
                        runtimeText.text = details.runtime?.toString() ?: "N/A"
                        Glide.with(this@DetailsFragment)
                            .load(details.posterPath)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.error)
                            .into(posterImage)
                    }
                    is MovieDetailsState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}