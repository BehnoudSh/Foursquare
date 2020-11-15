
package ir.behnoudsh.aroundme.data.model.Venue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VenueResponse {

    @SerializedName("venue")
    @Expose
    private VenueDetail venue;

    public VenueDetail getVenue() {
        return venue;
    }

    public void setVenue(VenueDetail venue) {
        this.venue = venue;
    }

}
