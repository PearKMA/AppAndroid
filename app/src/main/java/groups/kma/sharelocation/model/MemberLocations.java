package groups.kma.sharelocation.model;

public class MemberLocations {
    private String type;
    private String inviteKey;

    public MemberLocations() {
    }

    public MemberLocations(String type, String inviteKey) {
        this.type = type;
        this.inviteKey = inviteKey;
    }

    public MemberLocations(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInviteKey() {
        return inviteKey;
    }

    public void setInviteKey(String inviteKey) {
        this.inviteKey = inviteKey;
    }
}
