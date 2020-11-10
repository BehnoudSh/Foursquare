package ir.behnoudsh.aroundme.data.api

import ir.behnoudsh.aroundme.data.model.Venues.ResponseVenues

class ApiHelper(private val apiService: ApiService) {
    suspend fun getvenues(lat_lng: String, offset: Int)
            : ResponseVenues? {

        return apiService.getVenues(lat_lng, offset)

    }
}