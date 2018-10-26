package groups.kma.sharelocation.NguoiThan;

public class QuanLyNhomModel {
    private String invitekey;
    private String type;

    public QuanLyNhomModel(String invitekey, String type) {
        this.invitekey = invitekey;
        this.type = type;
    }

    public QuanLyNhomModel() {

    }

    public String getInvitekey() {
        return invitekey;
    }

    public void setInvitekey(String invitekey) {
        this.invitekey = invitekey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
