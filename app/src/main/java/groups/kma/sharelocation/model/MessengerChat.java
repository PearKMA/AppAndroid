package groups.kma.sharelocation.model;

import android.provider.SyncStateContract;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MessengerChat {
    private String mText;
    private String mSender;
    private Date mDate;
    private String mDateString;

    public MessengerChat() {

    }

    public MessengerChat(String mText, String mSender) {
        this.mText = mText;
        this.mSender = mSender;
    }

    public MessengerChat(String mText, String mSender, Date mDate) {
        this.mText = mText;
        this.mSender = mSender;
        this.mDate = mDate;
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public String getSender() {
        return mSender;
    }

    public void setSender(String mSender) {
        this.mSender = mSender;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public String getDateString() {
        Calendar cal = Calendar.getInstance();
        return String.valueOf(cal);
    }

    public void setDateString(String mDateString) {
        this.mDateString = mDateString;
    }
}
