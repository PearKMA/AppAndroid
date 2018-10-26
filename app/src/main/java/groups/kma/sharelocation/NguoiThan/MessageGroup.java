package groups.kma.sharelocation.NguoiThan;

public class MessageGroup {
    private String date,message,name,time,fromId;

    public MessageGroup() {
    }

    public MessageGroup(String date, String message, String name, String time, String fromId) {
        this.date = date;
        this.message = message;
        this.name = name;
        this.time = time;
        this.fromId = fromId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
