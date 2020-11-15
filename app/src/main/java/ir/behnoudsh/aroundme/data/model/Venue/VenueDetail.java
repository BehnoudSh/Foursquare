package ir.behnoudsh.aroundme.data.model.Venue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VenueDetail {


    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("canonicalUrl")
    @Expose
    private String canonicalUrl;
    @SerializedName("categories")
    @Expose
    private List<Category> categories = null;

    @SerializedName("shortUrl")
    @Expose
    private String shortUrl;
    @SerializedName("timeZone")
    @Expose
    private String timeZone;

    public VenueDetail(String id, String name, Location location, String canonicalUrl, List<Category> categories, String shortUrl, String timeZone) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.canonicalUrl = canonicalUrl;
        this.categories = categories;
        this.shortUrl = shortUrl;
        this.timeZone = timeZone;
    }

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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    public void setCanonicalUrl(String canonicalUrl) {
        this.canonicalUrl = canonicalUrl;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
