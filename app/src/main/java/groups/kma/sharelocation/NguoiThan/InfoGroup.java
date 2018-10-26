package groups.kma.sharelocation.NguoiThan;

public class InfoGroup {
    String NameGroup,PhotoGroup,StatusGroup;

    public InfoGroup() {
    }

    public InfoGroup(String nameGroup, String photoGroup, String statusGroup) {
        NameGroup = nameGroup;
        PhotoGroup = photoGroup;
        StatusGroup = statusGroup;
    }

    public String getNameGroup() {
        return NameGroup;
    }

    public void setNameGroup(String nameGroup) {
        NameGroup = nameGroup;
    }

    public String getPhotoGroup() {
        return PhotoGroup;
    }

    public void setPhotoGroup(String photoGroup) {
        PhotoGroup = photoGroup;
    }

    public String getStatusGroup() {
        return StatusGroup;
    }

    public void setStatusGroup(String statusGroup) {
        StatusGroup = statusGroup;
    }
}
