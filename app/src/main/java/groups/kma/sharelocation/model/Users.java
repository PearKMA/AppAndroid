package groups.kma.sharelocation.model;

public class Users {
    private String photoUrl;
    private String userName;
    private String email;
    private String password;
    private String key;
    private boolean online;



    public Users() {

    }

    public Users(String userName, String email, String password, String key) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.key = key;
    }

    public Users(String photoUrl, String userName, String email, String password, String key, boolean online) {
        this.photoUrl = photoUrl;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.key = key;
        this.online = online;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Users(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
