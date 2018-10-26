package groups.kma.sharelocation.NguoiThan;

public class MemberType {
    private String inviteKey;
    private String type;

    public MemberType() {
    }

    public MemberType(String inviteKey, String type) {
        this.inviteKey = inviteKey;
        this.type = type;
    }

    public String getInviteKey() {
        return inviteKey;
    }

    public void setInviteKey(String inviteKey) {
        this.inviteKey = inviteKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
