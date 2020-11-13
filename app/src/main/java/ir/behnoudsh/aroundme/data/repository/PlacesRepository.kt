package ir.behnoudsh.aroundme.data.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import ir.behnoudsh.aroundme.data.api.ApiClient
import ir.behnoudsh.aroundme.data.room.FoursquarePlace
import ir.behnoudsh.aroundme.data.model.Venues.ResponseVenues
import ir.behnoudsh.aroundme.data.pref.Prefs
import ir.behnoudsh.aroundme.data.room.FoursquarePlacesDao
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception

class PlacesRepository(val foursquareplacesDao: FoursquarePlacesDao, application: Application) {
    private val apiHandler = ApiClient.apiinterface

    val allPlacesSuccessLiveData = MutableLiveData<ArrayList<FoursquarePlace>>()
    val allPlacesFailureLiveData = MutableLiveData<Boolean>()

    val prefs: Prefs = Prefs(application)


    fun getPlacesFromDB() = foursquareplacesDao.getPlaces()


    suspend fun addPlacesToDB(places: List<FoursquarePlace>) =
        foursquareplacesDao.insertPlaces(places)


    suspend fun deletePlacesFromDB() = foursquareplacesDao.deletePlaces()


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