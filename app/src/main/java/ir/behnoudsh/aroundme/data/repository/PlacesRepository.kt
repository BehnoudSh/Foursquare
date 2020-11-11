package ir.behnoudsh.aroundme.data.repository

import androidx.lifecycle.MutableLiveData
import ir.behnoudsh.aroundme.data.api.ApiClient
import ir.behnoudsh.aroundme.data.model.Venues.ResponseVenues
import java.lang.Exception

class PlacesRepository {
    private val apiHandler = ApiClient.apiinterface

    val allPlacesSuccessLiveData = MutableLiveData<ResponseVenues>()
    val allPlacesFailureLiveData = MutableLiveData<Boolean>()

    suspend fun getPlaces(lat_lng: String, offset: Int) {
        try {
            val response = apiHandler.getVenues(lat_lng, offset)
            if (response?.isSuccessful!!) {
                allPlacesSuccessLiveData.postValue(response?.body())
                allPlacesFailureLiveData.postValue(false)
            } else {
                allPlacesFailureLiveData.postValue(true)
            }
        } catch (ex: Exception) {

            allPlacesFailureLiveData.postValue(true)

        }

    }


}