package groups.kma.sharelocation.NguoiThan;

public class MembersGroup {
    public String mLat;
    public String mLong;
    public String type;

    public MembersGroup() {
    }

    public MembersGroup(String mLat, String mLong, String type) {
        this.mLat = mLat;
        this.mLong = mLong;
        this.type = type;
    }

    public String getmLat() {
        return mLat;
    }

    public void setmLat(String mLat) {
        this.mLat = mLat;
    }

    public String getmLong() {
        return mLong;
    }

    public void setmLong(String mLong) {
        this.mLong = mLong;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
