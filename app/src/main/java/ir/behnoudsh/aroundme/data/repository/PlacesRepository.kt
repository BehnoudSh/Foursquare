package ir.behnoudsh.aroundme.data.repository

import androidx.lifecycle.MutableLiveData
import ir.behnoudsh.aroundme.data.api.ApiHelper
import ir.behnoudsh.aroundme.data.model.Venues.ResponseVenues

class PlacesRepository(private val apiHelper: ApiHelper) {

    var allPlaces: ResponseVenues? = null

    suspend fun getPlaces(lat_lng: String, offset: Int): MutableLiveData<ResponseVenues>? {

        allPlaces = apiHelper.getvenues(lat_lng, offset);

        val temp: MutableLiveData<ResponseVenues> = MutableLiveData()
        temp.value = allPlaces
        return temp

    }


}