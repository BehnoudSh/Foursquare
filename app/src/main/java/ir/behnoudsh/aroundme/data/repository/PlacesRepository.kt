package ir.behnoudsh.aroundme.data.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import ir.behnoudsh.aroundme.data.api.ApiClient
import ir.behnoudsh.aroundme.data.model.Venue.ResponseVenue
import ir.behnoudsh.aroundme.data.model.Venues.ResponseVenues
import ir.behnoudsh.aroundme.data.pref.Prefs
import ir.behnoudsh.aroundme.data.room.FoursquarePlace
import ir.behnoudsh.aroundme.data.room.FoursquarePlacesDao
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Response

class PlacesRepository(val foursquareplacesDao: FoursquarePlacesDao, application: Application) {
    private val apiHandler = ApiClient.apiinterface
    val allPlacesSuccessLiveData = MutableLiveData<ArrayList<FoursquarePlace>>()
    val allPlacesFailureLiveData = MutableLiveData<Boolean>()
    val noLocationFoundLiveData2 = MutableLiveData<Boolean>()
    val placeDetailsSuccessLiveData = MutableLiveData<FoursquarePlace>()
    val placeDetailsFailureLiveData = MutableLiveData<Boolean>()
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

    /*suspend*/ fun getPlaceDetails(place: FoursquarePlace) {
        ApiClient.apiinterface.getVenueDetails(place.id)
            ?.enqueue(object : retrofit2.Callback<ResponseVenue> {
                override fun onResponse(
                    call: Call<ResponseVenue>,
                    response: Response<ResponseVenue>
                ) {
                    if (response.body() != null) {
                        place.link = response.body()!!.response.venue.canonicalUrl
                        placeDetailsSuccessLiveData.postValue(place)
                    } else {
                        placeDetailsFailureLiveData.postValue(true)
                    }
                }
                override fun onFailure(call: Call<ResponseVenue>, t: Throwable) {
                    placeDetailsFailureLiveData.postValue(true)
                }
            })
    }

    /*suspend*/ fun getPlaces(lat_lng: String, offset: Int) {
        try {

            ApiClient.apiinterface.getVenues(lat_lng, offset)
                ?.enqueue(object : retrofit2.Callback<ResponseVenues> {
                    override fun onResponse(
                        call: Call<ResponseVenues>,
                        response: Response<ResponseVenues>
                    ) {
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

                            if (offset == 0 && placesList.size == 0) {
                                noLocationFoundLiveData2.postValue(true)
                            }

                        } else {
                            allPlacesFailureLiveData.postValue(true)
                        }
                    }

                    override fun onFailure(call: Call<ResponseVenues>, t: Throwable) {
                        allPlacesFailureLiveData.postValue(true)
                    }
                })

        } catch (ex: Exception) {
            allPlacesFailureLiveData.postValue(true)
        }
    }


}