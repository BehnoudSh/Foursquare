package ir.behnoudsh.aroundme.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import ir.behnoudsh.aroundme.data.api.ApiClient
import ir.behnoudsh.aroundme.data.room.FoursquarePlace
import ir.behnoudsh.aroundme.data.model.Venues.ResponseVenues
import ir.behnoudsh.aroundme.data.pref.Prefs
import ir.behnoudsh.aroundme.data.room.FoursquarePlacesDao
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception

class PlacesRepository(val foursquareplacesDao: FoursquarePlacesDao, application: Application) {
    private val apiHandler = ApiClient.apiinterface

    val allPlacesSuccessLiveData = MediatorLiveData<ArrayList<FoursquarePlace>>()
    val allPlacesFailureLiveData = MutableLiveData<Boolean>()
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


    /*suspend*/ fun getPlaces(lat_lng: String, offset: Int) {
        try {
//            val response = apiHandler.getVenues(lat_lng, offset)

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
                                )

                                placesList.add(item)

                            }

                            GlobalScope.launch(Dispatchers.IO) {
                                addPlacesToDB(placesList as ArrayList<FoursquarePlace>)
                            }
                            prefs.previousOffset += 20
                            allPlacesSuccessLiveData.postValue(placesList as ArrayList<FoursquarePlace>?)
                            allPlacesFailureLiveData.postValue(false)


                        } else {
                            allPlacesFailureLiveData.postValue(true)
                        }
                    }

                    override fun onFailure(call: Call<ResponseVenues>, t: Throwable) {
                        allPlacesFailureLiveData.postValue(false)
                    }
                })


//            if (response?.isSuccessful!!) {
//
//                var placesList: List<FoursquarePlace> = ArrayList()
//
//                response.body()?.response?.groups?.get(0)?.items?.forEach() {
//
//                    var address: String = ""
//                    it.venue.location.formattedAddress.forEach()
//                    {
//                        address += it
//                        address += "\n"
//                    }
//
//                    var item: FoursquarePlace = FoursquarePlace(
//                        it.venue.id,
//                        it.venue.name,
//                        address,
//                        it.venue.location.distance,
//                        it.venue.location.lat.toString(),
//                        it.venue.location.lng.toString(),
//                    )
//
//                    placesList.toMutableList().add(item)
//
//                }
//
//                allPlacesSuccessLiveData.postValue(placesList)
//                allPlacesFailureLiveData.postValue(false)
//
//            } else {
//                allPlacesFailureLiveData.postValue(true)
//            }

        } catch (ex: Exception) {
            allPlacesFailureLiveData.postValue(true)
        }
    }


}