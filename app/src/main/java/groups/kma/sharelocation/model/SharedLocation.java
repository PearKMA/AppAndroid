package groups.kma.sharelocation.model;

public class SharedLocation {
    public String name;
    public double latitude;
    public double  longitude;

    public SharedLocation() {
    }

    public SharedLocation(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
