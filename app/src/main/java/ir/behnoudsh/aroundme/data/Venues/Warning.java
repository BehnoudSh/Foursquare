
package ir.behnoudsh.aroundme.data.Venues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Warning {

    @SerializedName("text")
    @Expose
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
