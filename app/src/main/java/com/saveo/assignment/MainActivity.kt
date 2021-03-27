package com.saveo.assignment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.*
import com.saveo.assignment.data.api.TheMovieDBClient
import com.saveo.assignment.data.api.TheMovieDBInterface
import com.saveo.assignment.data.repository.NetworkState
import com.saveo.assignment.databinding.ActivityMainBinding
import com.saveo.assignment.ui.popular_movie.MoviePagedListRepository
import com.saveo.assignment.ui.popular_movie.PopularMoviePagedListAdapter
import com.saveo.assignment.ui.popular_movie.PopularMovieViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: PopularMovieViewModel
    lateinit var movieRepository: MoviePagedListRepository
   

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val apiService : TheMovieDBInterface = TheMovieDBClient.getClient()
        movieRepository = MoviePagedListRepository(apiService)
        viewModel = getViewModel()
        val movieAdapter = PopularMoviePagedListAdapter(this)
        val gridLayoutManager = GridLayoutManager(this, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = movieAdapter.getItemViewType(position)
                if (viewType == movieAdapter.MOVIE_VIEW_TYPE) return  1
                else return 3
            }
        }
        gridLayoutManager.isAutoMeasureEnabled = true
        binding.rvMovieList.layoutManager = gridLayoutManager
        binding.rvMovieList.setHasFixedSize(true)
        binding.rvMovieList.adapter = movieAdapter
        //binding.rvMovieList.isNestedScrollingEnabled = false
        ViewCompat.setNestedScrollingEnabled(binding.rvMovieList, false)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        layoutManager.isAutoMeasureEnabled = true
        val snapHelper: SnapHelper = PagerSnapHelper()
        binding.rvTopMovie.layoutManager = layoutManager
        snapHelper.attachToRecyclerView(binding.rvTopMovie)
        binding.rvTopMovie.adapter = movieAdapter
        ViewCompat.setNestedScrollingEnabled(binding.rvMovieList, true)

        binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (gridLayoutManager.findFirstVisibleItemPosition() >= 12){
                binding.tvMovie.text = "Now Showing"
            } else binding.tvMovie.text = "Movies"
        })

        viewModel.moviePagedList.observe(this, Observer {
            movieAdapter.submitList(it)
        })

        viewModel.networkState.observe(this, Observer {
            progress_bar_popular.visibility = if (viewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txt_error_popular.visibility = if (viewModel.listIsEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE

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
