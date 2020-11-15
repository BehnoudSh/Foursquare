package ir.behnoudsh.aroundme.data.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import ir.behnoudsh.aroundme.data.api.ApiClient
import ir.behnoudsh.aroundme.data.pref.Prefs
import ir.behnoudsh.aroundme.data.room.FoursquarePlace
import ir.behnoudsh.aroundme.data.room.FoursquarePlacesDao
import kotlinx.coroutines.*

class PlacesRepository(val foursquareplacesDao: FoursquarePlacesDao, application: Application) {
    val allPlacesSuccessLiveData = MutableLiveData<ArrayList<FoursquarePlace>>()
    val allPlacesFailureLiveData = MutableLiveData<Boolean>()
    val placeDetailsSuccessLiveData = MutableLiveData<FoursquarePlace>()
    val placeDetailsFailureLiveData = MutableLiveData<Boolean>()
    val message = MutableLiveData<String>()
    val prefs: Prefs = Prefs(application)

    suspend fun getPlacesFromDB(): List<FoursquarePlace> = runBlocking(Dispatchers.Default) {
        val result = async { foursquareplacesDao.getPlaces() }.await()
        return@runBlocking result as List<FoursquarePlace>
    }

    suspend fun addPlacesToDB(places: List<FoursquarePlace>) {
        withContext(Dispatchers.IO) {
            foursquareplacesDao.insertPlaces(places)
        }
    }

    suspend fun deletePlacesFromDB() {
        withContext(Dispatchers.IO) { foursquareplacesDao.deletePlaces() }
    }

    suspend fun getPlaceDetails(place: FoursquarePlace) {
        try {

            val response = ApiClient.apiinterface.getVenueDetails(place.id)
            if (response?.body() != null) {
                place.link = response?.body()!!.response.venue.canonicalUrl
                placeDetailsSuccessLiveData.postValue(place)
            } else {
                placeDetailsFailureLiveData.postValue(true)
            }

        } catch (ex: Exception) {
            placeDetailsFailureLiveData.postValue(true)

        }
    }

    suspend fun getPlaces(lat_lng: String, offset: Int) {
        try {

            val response = ApiClient.apiinterface.getVenues(lat_lng, offset)

            if (response?.body() != null) {

                if (response.body() != null) {
                    var placesList: MutableCollection<FoursquarePlace> =
                        mutableListOf<FoursquarePlace>()
                    response.body()?.response?.groups?.get(0)?.items?.forEach() {
                        var address: String = ""
                        it.venue.location.formattedAddress.forEach()
                        {
                            address += it
                            address += "\n"
                        }

                        var item: FoursquarePlace = FoursquarePlace(
                            it.venue.id,
                            it.venue.name,
                            address,
                            it.venue.location.distance,
                            it.venue.location.lat.toString(),
                            it.venue.location.lng.toString(),
                            ""
                        )
                        placesList.add(item)
                    }

                    GlobalScope.launch(Dispatchers.IO) {
                        addPlacesToDB(placesList as ArrayList<FoursquarePlace>)
                    }
                    prefs.lastUpdated = System.currentTimeMillis()
                    prefs.previousOffset += 20
                    allPlacesSuccessLiveData.postValue(placesList as ArrayList<FoursquarePlace>?)
                    allPlacesFailureLiveData.postValue(false)
                    message.postValue("مکان‌های اطراف به‌روزرسانی شدند")

                } else {
                    allPlacesFailureLiveData.postValue(true)
                }

            }

        } catch (ex: Exception) {
            allPlacesFailureLiveData.postValue(true)
        }
    }
}




