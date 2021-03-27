package com.saveo.assignment.ui.single_movie_details

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.saveo.assignment.BuildConfig
import com.saveo.assignment.R
import com.saveo.assignment.data.api.TheMovieDBClient
import com.saveo.assignment.data.api.TheMovieDBInterface
import com.saveo.assignment.data.repository.NetworkState
import com.saveo.assignment.data.vo.MovieDetails
import com.saveo.assignment.databinding.ActivitySingleMovieBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SingleMovie : AppCompatActivity() {
    private lateinit var binding: ActivitySingleMovieBinding
    private lateinit var viewModel: SingleMovieViewModel
    private lateinit var movieRepository: MovieDetailsRepository

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivitySingleMovieBinding>(this,R.layout.activity_single_movie)

        val movieId: Int = intent.getIntExtra("id",1)
        val apiService : TheMovieDBInterface = TheMovieDBClient.getClient()
        movieRepository = MovieDetailsRepository(apiService)
        viewModel = getViewModel(movieId)
        viewModel.movieDetails.observe(this, Observer {
            bindUI(it)
        })
        viewModel.networkState.observe(this, Observer {
            binding.progressBar.visibility = if (it == NetworkState.LOADING) View.VISIBLE else View.GONE
            if (it == NetworkState.ERROR) {
                binding.txtError.visibility = View.VISIBLE
                binding.containerMovieDetails.visibility = View.GONE
            } else {
                binding.txtError.visibility = View.GONE
                binding.containerMovieDetails.visibility = View.VISIBLE
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun bindUI(it: MovieDetails){
        val formatter = DateTimeFormatter.ofPattern("dd MMM,yyyy")
        binding.apply {
            tvMovieName.text = it.title
            tvMovieLengthDate.text = "R | "+it.runtime+" minutes | "+ LocalDate.parse(it.releaseDate).format(formatter)
            tvSynopsisDesc.text = it.overview
            movieRating.rating = (it.rating/2).toFloat()
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
