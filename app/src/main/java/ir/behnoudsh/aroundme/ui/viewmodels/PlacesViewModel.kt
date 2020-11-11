package ir.behnoudsh.aroundme.ui.viewmodels

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ir.behnoudsh.aroundme.data.model.LocationLiveData
import ir.behnoudsh.aroundme.data.model.LocationModel
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
    val allPlacesFailureLiveData = placesRepository.allPlacesSuccessLiveData


    fun getLocationData(): LocationLiveData {

        return locationData;
    }

    fun locationChanged(location: LocationModel) {

        var offset: Int = 20

        getAllPlaces(LocationModel(51.4238302, 35.7233924), offset)

    }

    fun getAllPlaces(location: LocationModel, offset: Int) {

        viewModelScope.launch {
            placesRepository.getPlaces(
                location.latitude.toString() + "," + location.longitude.toString(),
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