package com.example.imagesearchapp.ui.gallery

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.imagesearchapp.data.UnsplashRepository

class GalleryViewModel @ViewModelInject constructor( private val repository: UnsplashRepository ,@Assisted state:SavedStateHandle) : ViewModel()
{

    private val currentQuery = state.getLiveData(CURENT_QUERY, DEFAULT_QUERY)

    val photos = currentQuery.switchMap { queryString ->
        repository.getSearchResults(queryString).cachedIn(viewModelScope)
    }

    fun searchPhotos(query: String) {
        currentQuery.value = query
    }

    companion object {
        private const val CURENT_QUERY = "current_query"
        private const val DEFAULT_QUERY = "cats"
    }

}