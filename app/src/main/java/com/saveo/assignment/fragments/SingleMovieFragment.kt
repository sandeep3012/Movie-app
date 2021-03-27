package com.saveo.assignment.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.saveo.assignment.BuildConfig
import com.saveo.assignment.R
import com.saveo.assignment.data.api.POSTER_BASE_URL
import com.saveo.assignment.data.api.TheMovieDBClient
import com.saveo.assignment.data.api.TheMovieDBInterface
import com.saveo.assignment.data.vo.MovieDetails
import com.saveo.assignment.databinding.FragmentSingleMovieBinding
import com.saveo.assignment.ui.single_movie_details.MovieDetailsRepository
import com.saveo.assignment.ui.single_movie_details.SingleMovieViewModel
import kotlinx.android.synthetic.main.activity_single_movie.*
import kotlinx.android.synthetic.main.fragment_single_movie.*
import java.text.NumberFormat
import java.util.*

class SingleMovieFragment : Fragment() {
    lateinit var binding: FragmentSingleMovieBinding
    private lateinit var viewModel: SingleMovieViewModel
    private lateinit var movieRepository: MovieDetailsRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentSingleMovieBinding>(inflater, R.layout.fragment_single_movie, container, false)

        val apiService : TheMovieDBInterface = TheMovieDBClient.getClient()
        movieRepository = MovieDetailsRepository(apiService)

       // viewModel = getViewModel(movieId)

        viewModel.movieDetails.observe(viewLifecycleOwner, Observer {
            bindUI(it)
        })
        return binding.root
    }

    fun bindUI( it: MovieDetails){
        binding.apply {
            tvMovieName.text = it.title
            tvMovieLengthDate.text = "R | "+it.runtime+" minutes | "+it.releaseDate
            tvSynopsisDesc.text = it.overview
        }
        val moviePosterURL = BuildConfig.POSTER_BASE_URL + it.posterPath
        Glide.with(this)
            .load(moviePosterURL)
            .into(binding.ivMovieBanner)
    }

    private fun getViewModel(movieId:Int): SingleMovieViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SingleMovieViewModel(movieRepository,movieId) as T
            }
        })[SingleMovieViewModel::class.java]
    }
}