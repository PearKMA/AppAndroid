package groups.kma.sharelocation.VungAnToan;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import groups.kma.sharelocation.NguoiThan.NhomNguoiThanMapActivity;
import groups.kma.sharelocation.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class RunInner extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager=
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo()!=null)
        {
            xuLyNotification(context);
        }else {

        }
    }
    private void xuLyNotification(Context context) {
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context, "Alert_Area")
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Phát hiện vượt khoảng cách an toàn")
                .setContentText("User: .Nhấn để xem chi tiết!")
                .setAutoCancel(true);

        Uri uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        nBuilder.setSound(uri);
        Intent resultIntent = new Intent(context, NhomNguoiThanMapActivity.class);
        PendingIntent resultPending = PendingIntent.getActivity(context,
                0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(resultPending);

        /*Uri uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        nBuilder.setSound(uri);*/
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService
                (Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(113, nBuilder.build());
    }
}
