
package ir.behnoudsh.aroundme.data.model.Venue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseVenue {


    @SerializedName("response")
    @Expose
    private VenueResponse response;


    public VenueResponse getResponse() {
        return response;
    }

    public void setResponse(VenueResponse response) {
        this.response = response;
    }

}
