
package ir.behnoudsh.aroundme.data.model.Venues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseVenues {

    @SerializedName("response")
    @Expose
    private Response response;


    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

}
