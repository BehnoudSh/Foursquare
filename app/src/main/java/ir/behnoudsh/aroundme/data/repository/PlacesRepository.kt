package ir.behnoudsh.aroundme.data.repository

import androidx.lifecycle.MutableLiveData
import ir.behnoudsh.aroundme.data.api.ApiClient
import ir.behnoudsh.aroundme.data.model.Venues.ResponseVenues

class PlacesRepository {
    private val apiHandler = ApiClient.apiinterface

    val allPlacesSuccessLiveData = MutableLiveData<ResponseVenues>()

    suspend fun getPlaces(lat_lng: String, offset: Int) {

        val response = apiHandler.getVenues(lat_lng, offset)
        allPlacesSuccessLiveData.postValue(response?.body())

    }


}