package groups.kma.sharelocation.Chat;

public class Requests {
    private String user_name,user_status,user_thumbimage;

    public Requests() {
    }

    public Requests(String user_name, String user_status, String user_thumbimage) {
        this.user_name = user_name;
        this.user_status = user_status;
        this.user_thumbimage = user_thumbimage;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getUser_thumbimage() {
        return user_thumbimage;
    }

    public void setUser_thumbimage(String user_thumbimage) {
        this.user_thumbimage = user_thumbimage;
    }
}
