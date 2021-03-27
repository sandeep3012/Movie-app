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
import androidx.recyclerview.widget.GridLayoutManager
import com.saveo.assignment.R
import com.saveo.assignment.data.api.TheMovieDBClient
import com.saveo.assignment.data.api.TheMovieDBInterface
import com.saveo.assignment.data.repository.NetworkState
import com.saveo.assignment.databinding.FragmentPopularMoviesBinding
import com.saveo.assignment.databinding.FragmentSingleMovieBinding
import com.saveo.assignment.ui.popular_movie.MoviePagedListRepository
import com.saveo.assignment.ui.popular_movie.PopularMoviePagedListAdapter
import com.saveo.assignment.ui.popular_movie.PopularMovieViewModel
import kotlinx.android.synthetic.main.activity_main.*

class PopularMoviesFragment : Fragment() {
    lateinit var binding: FragmentPopularMoviesBinding
    private lateinit var viewModel: PopularMovieViewModel
    lateinit var movieRepository: MoviePagedListRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentPopularMoviesBinding>(inflater, R.layout.fragment_popular_movies, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val apiService : TheMovieDBInterface = TheMovieDBClient.getClient()
        movieRepository = MoviePagedListRepository(apiService)
        viewModel = getViewModel()
        val movieAdapter = PopularMoviePagedListAdapter(requireContext())
        val gridLayoutManager = GridLayoutManager(requireActivity(), 3)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = movieAdapter.getItemViewType(position)
                if (viewType == movieAdapter.MOVIE_VIEW_TYPE) return  1
                else return 3
            }
        }
        binding.apply {
            rvPopularMoviesList.layoutManager = gridLayoutManager
            rvPopularMoviesList.setHasFixedSize(true)
            rvPopularMoviesList.adapter = movieAdapter
        }
        viewModel.moviePagedList.observe(viewLifecycleOwner, Observer {
            movieAdapter.submitList(it)
        })
        viewModel.networkState.observe(viewLifecycleOwner, Observer {
            binding.progressBarPopular.visibility = if (viewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            binding.txtErrorPopular.visibility = if (viewModel.listIsEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE

            if (!viewModel.listIsEmpty()) {
                movieAdapter.setNetworkState(it)
            }
        })
    }

    private fun getViewModel(): PopularMovieViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PopularMovieViewModel(movieRepository) as T
            }
        })[PopularMovieViewModel::class.java]
    }

}