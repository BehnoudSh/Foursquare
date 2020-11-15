
package ir.behnoudsh.aroundme.data.model.Venues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("venue")
    @Expose
    private Venue venue;
    @SerializedName("referralId")
    @Expose
    private String referralId;


    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public String getReferralId() {
        return referralId;
    }

    public void setReferralId(String referralId) {
        this.referralId = referralId;
    }

}
