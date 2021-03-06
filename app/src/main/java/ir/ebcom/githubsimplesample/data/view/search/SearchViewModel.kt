package ir.ebcom.githubsimplesample.data.view.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.ebcom.githubsimplesample.data.response.SearchResponse
import ir.ebcom.githubsimplesample.data.network.Resource
import ir.ebcom.githubsimplesample.data.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.IllegalArgumentException
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
): ViewModel() {
    private val _searchResponse: MutableLiveData<Resource<SearchResponse>> = MutableLiveData()
    val searchResponse: LiveData<Resource<SearchResponse>> = _searchResponse

    @FlowPreview
    fun search(q: String) = viewModelScope.launch {
        _searchResponse.postValue(Resource.Loading)
        searchRepository.search(q)
            .debounce(300)
            .distinctUntilChanged()
            .catch { e ->
                Log.e("SearchVM", "search: " + e.localizedMessage)
            }
            .collect {
                _searchResponse.postValue(it)
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}