package ir.behnoudsh.aroundme.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoursqaurePlacesDao {

    @Query("SELECT * FROM places")
    fun getPlaces(): LiveData<List<FoursquarePlace>>

    @Insert
    fun insertPlaces(places: List<FoursquarePlace>)

    @Query("DELETE FROM places")
    fun deletePlaces()
}