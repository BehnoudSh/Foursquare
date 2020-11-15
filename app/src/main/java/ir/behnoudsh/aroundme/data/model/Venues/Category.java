
package ir.behnoudsh.aroundme.data.model.Venues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("pluralName")
    @Expose
    private String pluralName;
    @SerializedName("shortName")
    @Expose
    private String shortName;

    @SerializedName("primary")
    @Expose
    private Boolean primary;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPluralName() {
        return pluralName;
    }

    public void setPluralName(String pluralName) {
        this.pluralName = pluralName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }


    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

}
