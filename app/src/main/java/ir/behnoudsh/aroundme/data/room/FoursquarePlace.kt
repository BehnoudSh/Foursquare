package ir.behnoudsh.aroundme.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")

public data class FoursquarePlace(
    @PrimaryKey
    @ColumnInfo(name = "Id")
    val id: String,
    @ColumnInfo(name = "Name")
    val name: String,
    @ColumnInfo(name = "Address")
    val address: String,
    @ColumnInfo(name = "Distance")
    val distance: Int,
    @ColumnInfo(name = "Lat")
    val lat: String,
    @ColumnInfo(name = "Long")
    val long: String
)
