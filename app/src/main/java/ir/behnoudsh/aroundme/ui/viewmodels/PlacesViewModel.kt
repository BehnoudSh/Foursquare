package ir.behnoudsh.aroundme.ui.viewmodels

import android.app.Application
import android.location.Location
import androidx.lifecycle.*
import ir.behnoudsh.aroundme.App
import ir.behnoudsh.aroundme.data.model.LocationLiveData
import ir.behnoudsh.aroundme.data.model.LocationModel
import ir.behnoudsh.aroundme.data.model.Venues.ResponseVenues
import ir.behnoudsh.aroundme.data.pref.Prefs
import ir.behnoudsh.aroundme.data.repository.PlacesRepository
import ir.behnoudsh.aroundme.data.room.AppDataBase
import ir.behnoudsh.aroundme.data.room.FoursquarePlace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PlacesViewModel(application: Application) : AndroidViewModel(application) {

    private val locationData = LocationLiveData(application)
    var mPlacesData: MutableLiveData<ResponseVenues>? = null
    lateinit var firstLocation: LocationLiveData
    var firstLocationSet: Boolean = false
    lateinit var currentLocation: LocationLiveData
    var loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val foursquareplacesDao = AppDataBase.getDatabase(application).foursquareplacesDao()
    val placesRepository: PlacesRepository = PlacesRepository(foursquareplacesDao, application)

    val allPlacesSuccessLiveData = placesRepository.allPlacesSuccessLiveData
    val allPlacesFailureLiveData = placesRepository.allPlacesFailureLiveData

    val prefs: Prefs = Prefs(application)


    fun getLocationData(): LocationLiveData {
        return locationData;
    }


    fun loadMore() {
//
//        var list: List<FoursquarePlace> = ArrayList()
//        GlobalScope.launch(Dispatchers.IO) {
//            list = getPlacesFromDB()
//        }

        getAllPlaces(
            LocationModel(prefs.myLocationLong.toDouble(), prefs.myLocationLat.toDouble()),
            prefs.previousOffset
        )

    }

    fun locationChanged(location: LocationModel) {

        if (!firstLocationSet) {
            prefs.myLocationLat = location.latitude.toString()
            prefs.myLocationLong = location.longitude.toString()
            firstLocationSet = true
            prefs.previousOffset = 0
            deletePlacesFromDB()
            getAllPlaces(
                LocationModel(prefs.myLocationLong.toDouble(), prefs.myLocationLat.toDouble()),
                prefs.previousOffset
            )
        }

        if (distance(
                location.latitude,
                location.longitude,
                prefs.myLocationLat.toDouble(),
                prefs.myLocationLong.toDouble()
            ) > 100
        ) {
            deletePlacesFromDB()

            prefs.previousOffset = 0;
            prefs.myLocationLat = location.latitude.toString()
            prefs.myLocationLong = location.longitude.toString()

            getAllPlaces(
                LocationModel(prefs.myLocationLong.toDouble(), prefs.myLocationLat.toDouble()),
                prefs.previousOffset
            )
        }

    }


    fun getAllPlaces(location: LocationModel, offset: Int) {

        loadingLiveData?.postValue(true)

        /* viewModelScope.launch {*/
        placesRepository.getPlaces(
            location.latitude.toString() + "," + location.longitude.toString(),
            offset
        )
//        }

    }

    suspend fun getPlacesFromDB(): List<FoursquarePlace> {

        return placesRepository.getPlacesFromDB()
    }

    fun deletePlacesFromDB() = viewModelScope.launch(Dispatchers.IO) {
        placesRepository.deletePlacesFromDB()
    }


    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val loc1 = Location("")
        loc1.latitude = lat1
        loc1.longitude = lon1
        val loc2 = Location("")
        loc2.latitude = lat2
        loc2.longitude = lon2
        return loc1.distanceTo(loc2)
    }


}