package groups.kma.sharelocation.model;

public class Users {
    private String photoUrl;
    private String userName;
    private String email;
    private String password;
    private String status;
    private String image;
    private String key;
    private String token;

    public Users() {

    }



    public Users(String userName, String status, String thumb_image) {
        this.userName = userName;
        this.status = status;
        this.photoUrl = thumb_image;
    }

    public Users(String userName, String email, String password, String status, String image, String thumbimage,String token){
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.status = status;
        this.image=image;
        this.photoUrl=thumbimage;
        this.token = token;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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


}
