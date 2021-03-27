package com.saveo.assignment.ui.popular_movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.saveo.assignment.data.repository.NetworkState
import com.saveo.assignment.data.vo.Movie
import io.reactivex.disposables.CompositeDisposable

class PopularMovieViewModel(private val movieRepository : MoviePagedListRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val  moviePagedList : LiveData<PagedList<Movie>> by lazy {
        movieRepository.fetchLiveMoviePagedList(compositeDisposable)
    }

    val  networkState : LiveData<NetworkState> by lazy {
        movieRepository.getNetworkState()
    }

    fun listIsEmpty(): Boolean {
        return moviePagedList.value?.isEmpty() ?: true
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}