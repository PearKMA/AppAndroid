package groups.kma.sharelocation.model;

public class LocationUsers {
    private String date;
    private double latitude;
    private double longtitude;
    private String name;
    private String time;

    public LocationUsers() {
    }

    public LocationUsers(String date, double latitude, double longtitude, String name, String time) {
        this.date = date;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.name = name;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
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
}
