package ir.behnoudsh.aroundme.ui.viewmodels

import android.app.Application
import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.*
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
    var loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val foursquareplacesDao = AppDataBase.getDatabase(application).foursquareplacesDao()
    val placesRepository: PlacesRepository = PlacesRepository(foursquareplacesDao, application)
    val allPlacesSuccessLiveData = placesRepository.allPlacesSuccessLiveData
    val allPlacesFailureLiveData = placesRepository.allPlacesFailureLiveData
    var newLocationLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var noLocationFoundLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var noLocationFoundLiveData2 = placesRepository.noLocationFoundLiveData2
    var message: MutableLiveData<String> = MutableLiveData()
    val prefs: Prefs = Prefs(application)
    var dataReadFromDB: Boolean = false

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

        if (TextUtils.isEmpty(prefs.myLocationLat) || TextUtils.isEmpty(prefs.myLocationLong)) {
            prefs.myLocationLat = location.latitude.toString()
            prefs.myLocationLong = location.longitude.toString()
        }

        var distanceFromOldPlace = distance(
            location.latitude,
            location.longitude,
            prefs.myLocationLat.toDouble(),
            prefs.myLocationLong.toDouble()
        )

        var datetimeDiff: Long = 0
        if (prefs.lastUpdated != 0L)
            datetimeDiff = System.currentTimeMillis() - prefs.lastUpdated


        if (distanceFromOldPlace > 100
        ) {
            prefs.previousOffset = 0;
            prefs.myLocationLat = location.latitude.toString()
            prefs.myLocationLong = location.longitude.toString()
            if (isOnline(getApplication())) {
                deletePlacesFromDB()
                newLocationLiveData.postValue(true)
                getAllPlaces(
                    LocationModel(prefs.myLocationLong.toDouble(), prefs.myLocationLat.toDouble()),
                    prefs.previousOffset
                )
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    if (!dataReadFromDB) {
                        if (getPlacesFromDB().size == 0) {
                            noLocationFoundLiveData.postValue(true)
                            message.postValue("مکانی یافت نشد")
                        } else
                            allPlacesSuccessLiveData.postValue(getPlacesFromDB() as ArrayList<FoursquarePlace>?)

                        dataReadFromDB = true
                    }
                }
                if (datetimeDiff < 86400000)
                    message.postValue("اینترنت ندارید و از مکان قبلی " + distanceFromOldPlace + " متر جابجا شده‌اید. هم‌چنین آخرین اطلاعات دریافتی مربوط به امروز است.")
                else
                    message.postValue("اینترنت ندارید و از مکان قبلی " + distanceFromOldPlace + " متر جابجا شده‌اید. هم‌چنین آخرین اطلاعات دریافتی مربوط به روزهای پیشین است.")
            }
        } else {
            if (isOnline(getApplication())) {
                if (datetimeDiff < 86400000) {
                    GlobalScope.launch(Dispatchers.IO) {
                        if (!dataReadFromDB) {
                            if (getPlacesFromDB().size == 0) {
                                noLocationFoundLiveData.postValue(true)
                                message.postValue("مکانی یافت نشد")
                            } else
                                allPlacesSuccessLiveData.postValue(getPlacesFromDB() as ArrayList<FoursquarePlace>?)

                            getAllPlaces(
                                LocationModel(
                                    prefs.myLocationLong.toDouble(),
                                    prefs.myLocationLat.toDouble()
                                ),
                                prefs.previousOffset
                            )
                            dataReadFromDB = true
                        }
                    }
                } else {
                    deletePlacesFromDB()
                    prefs.previousOffset = 0;
                    newLocationLiveData.postValue(true)
                    getAllPlaces(
                        LocationModel(
                            prefs.myLocationLong.toDouble(),
                            prefs.myLocationLat.toDouble()
                        ),
                        prefs.previousOffset
                    )
                }
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    if (!dataReadFromDB) {

                        if (getPlacesFromDB().size == 0) {
                            noLocationFoundLiveData.postValue(true)
                            message.postValue("مکانی یافت نشد")
                        } else
                            allPlacesSuccessLiveData.postValue(getPlacesFromDB() as ArrayList<FoursquarePlace>?)



                        dataReadFromDB = true
                    }
                }
                if (datetimeDiff < 86400000)
                    message.postValue("اینترنت ندارید و آخرین اطلاعات دریافتی مربوط به امروز است.")
                else
                    message.postValue("اینترنت ندارید و آخرین اطلاعات دریافتی مربوط به روزهای پیشین است.")
            }
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

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }


}