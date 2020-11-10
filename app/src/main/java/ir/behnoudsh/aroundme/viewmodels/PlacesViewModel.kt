package ir.behnoudsh.aroundme.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ir.behnoudsh.aroundme.data.LocationLiveData

class PlacesViewModel(application: Application) : AndroidViewModel(application) {

    private val locationData = LocationLiveData(application)

    fun getLocationData() = locationData
}