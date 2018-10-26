package groups.kma.sharelocation.VungAnToan;

public class SharedLocation {
    private String date;
    private Double latitude;
    private Double longtitude;
    private String name;
    private String time;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public SharedLocation() {
    }

    public SharedLocation(String date, Double latitude, Double longtitude, String name, String time) {
        this.date = date;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.name = name;
        this.time = time;
    }
}
