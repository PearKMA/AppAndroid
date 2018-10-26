package groups.kma.sharelocation.model;

public class ListGiamSat {
    private String name;
    private String key;
    private Double lat,lon;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public ListGiamSat(String name, String key, Double lat, Double lon) {
        this.name = name;
        this.key = key;
        this.lat = lat;
        this.lon = lon;
    }

    public ListGiamSat() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
