package groups.kma.sharelocation.NguoiThan;

public class SpinnerGroup {
    private String key,name;

    public SpinnerGroup() {
    }

    public SpinnerGroup(String name, String key) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
