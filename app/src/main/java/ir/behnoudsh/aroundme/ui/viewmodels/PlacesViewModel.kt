package ir.behnoudsh.aroundme.ui.viewmodels

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ir.behnoudsh.aroundme.data.model.LocationLiveData
import ir.behnoudsh.aroundme.data.model.LocationModel
import ir.behnoudsh.aroundme.data.pref.Prefs
import ir.behnoudsh.aroundme.data.repository.PlacesRepository
import ir.behnoudsh.aroundme.data.room.AppDataBase
import ir.behnoudsh.aroundme.data.room.FoursquarePlace
import ir.behnoudsh.aroundme.utilities.DistanceUtils
import ir.behnoudsh.aroundme.utilities.InternetUtils
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

    var message = placesRepository.message
    val prefs: Prefs = Prefs(application)
    var dataReadFromDB: Boolean = false
    val internetUtils: InternetUtils = InternetUtils(application)
    val placeDetailsSuccessLiveData = placesRepository.placeDetailsSuccessLiveData
    val placeDetailsFailureLiveData = placesRepository.placeDetailsFailureLiveData
    val distanceUtils: DistanceUtils = DistanceUtils()

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

        var distanceFromOldPlace = distanceUtils.distance(
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
            if (internetUtils.isOnline(getApplication())) {
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
                            message.postValue("مکانی در دیتابیس یافت نشد")
                        } else {
                            allPlacesSuccessLiveData.postValue(getPlacesFromDB() as ArrayList<FoursquarePlace>?)
                            message.postValue("")
                        }
                        dataReadFromDB = true
                    }
                }
                if (datetimeDiff < 86400000)
                    message.postValue("اینترنت ندارید و از مکان قبلی " + distanceFromOldPlace + " متر جابجا شده‌اید. هم‌چنین آخرین اطلاعات دریافتی مربوط به امروز است.")
                else
                    message.postValue("اینترنت ندارید و از مکان قبلی " + distanceFromOldPlace + " متر جابجا شده‌اید. هم‌چنین آخرین اطلاعات دریافتی مربوط به روزهای پیشین است.")
            }
        } else {
            if (internetUtils.isOnline(getApplication())) {
                if (datetimeDiff < 86400000) {
                    GlobalScope.launch(Dispatchers.IO) {
                        if (!dataReadFromDB) {
                            if (getPlacesFromDB().size == 0) {
                                message.postValue("مکانی در دیتابیس یافت نشد")
                            } else {
                                allPlacesSuccessLiveData.postValue(getPlacesFromDB() as ArrayList<FoursquarePlace>?)
                                message.postValue("")
                            }

                            prefs.previousOffset = 0
                            deletePlacesFromDB()
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
                            message.postValue("مکانی در دیتابیس یافت نشد")
                        } else {
                            allPlacesSuccessLiveData.postValue(getPlacesFromDB() as ArrayList<FoursquarePlace>?)
                            message.postValue("")
                        }
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

    fun getPlaceDetails(place: FoursquarePlace) {
//        viewModelScope.launch {
        placesRepository.getPlaceDetails(place)
//        }
    }

    suspend fun getPlacesFromDB(): List<FoursquarePlace> {

        return placesRepository.getPlacesFromDB()
    }

    fun deletePlacesFromDB() = viewModelScope.launch(Dispatchers.IO) {
        placesRepository.deletePlacesFromDB()
    }

}