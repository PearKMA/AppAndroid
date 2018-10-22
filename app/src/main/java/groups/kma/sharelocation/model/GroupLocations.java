package groups.kma.sharelocation.model;

public class GroupLocations {
    private String inviteKey;
    private String NameGroup;
    private String GroupId;

    public GroupLocations() {
    }

    public GroupLocations(String groupId) {
        GroupId = groupId;
    }

    public GroupLocations(String inviteKey, String nameGroup) {
        this.inviteKey = inviteKey;
        NameGroup = nameGroup;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getInviteKey() {
        return inviteKey;
    }

    public void setInviteKey(String inviteKey) {
        this.inviteKey = inviteKey;
    }

    public String getNameGroup() {
        return NameGroup;
    }

    public void setNameGroup(String nameGroup) {
        NameGroup = nameGroup;
    }
}
