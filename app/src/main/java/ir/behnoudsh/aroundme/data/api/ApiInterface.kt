package ir.behnoudsh.aroundme.data.api

import ir.behnoudsh.aroundme.data.model.Venue.ResponseVenue
import ir.behnoudsh.aroundme.data.model.Venues.ResponseVenues
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @GET(
        "explore?client_id=WGCTBTV215LV5ONUYVU0X00LBPEHJJTUJYBVAQ2J0HPUWQ52&client_secret=MAXZTYFGJFTY5WH4COY5ZCIUOQWEXWWLUDYYHVHQSAP4UZTC&v=20190218&limit=20&intent=browse&radius=1000"
    )
    suspend fun getVenues(
        @Query("ll") lng_lat: String?,
        @Query("offset") offset: Int
    ): Call<ResponseVenues>?


    @GET(
        "{venue_id}?client_id= WGCTBTV215LV5ONUYVU0X00LBPEHJJTUJYBVAQ2J0HPUWQ52&client_secret=MAXZTYFGJFTY5WH4COY5ZCIUOQWEXWWLUDYYHVHQSAP4UZTC&v=20190218"
    )
    suspend fun getVenueDetails(
        @Path(
            value = "venue_id",
            encoded = true
        ) venueId: String?
    ): Call<ResponseVenue>?


}