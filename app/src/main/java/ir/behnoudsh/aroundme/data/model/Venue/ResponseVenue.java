
package ir.behnoudsh.aroundme.data.model.Venue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseVenue {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("response")
    @Expose
    private VenueResponse response;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public VenueResponse getResponse() {
        return response;
    }

    public void setResponse(VenueResponse response) {
        this.response = response;
    }

}
