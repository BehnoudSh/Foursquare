package ir.behnoudsh.aroundme.ui.viewmodels

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ir.behnoudsh.aroundme.data.model.LocationLiveData
import ir.behnoudsh.aroundme.data.model.Venues.ResponseVenues
import ir.behnoudsh.aroundme.data.repository.PlacesRepository
import kotlinx.coroutines.launch

class PlacesViewModel(application: Application) : AndroidViewModel(application) {

    private val locationData = LocationLiveData(application)
    var mPlacesData: MutableLiveData<ResponseVenues>? = null
    lateinit var firstLocation: LocationLiveData
    var firstLocationSet: Boolean = false
    lateinit var currentLocation: LocationLiveData
    var offset: Int = 0

    val placesRepository: PlacesRepository = PlacesRepository()
    val allPlacesSuccessLiveData = placesRepository.allPlacesSuccessLiveData


    fun getLocationData(): LocationLiveData {
        if (!firstLocationSet) {
            firstLocation = locationData
            firstLocationSet = true
        }
        currentLocation = locationData

        if (distance(
                firstLocation.value!!.latitude,
                firstLocation.value!!.longitude,
                currentLocation.value!!.latitude,
                currentLocation.value!!.longitude
            ) > 100
        ) {

            firstLocation = currentLocation
            //db bayad pak beshe
            //offset 0

        }

        getAllPlaces(firstLocation, offset)

        return locationData;
    }

    fun getAllPlaces(location: LocationLiveData, offset: Int) {

        //get data based on firstlocation
        viewModelScope.launch {
            placesRepository.getPlaces(
                location.value?.latitude.toString()
                        + "," +
                        location.value?.longitude.toString(),
                offset
            )
        }

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