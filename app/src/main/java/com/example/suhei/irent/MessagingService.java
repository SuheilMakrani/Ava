package com.example.suhei.irent;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String dataFrom = remoteMessage.getData().get("from_user_id");
        String pid = remoteMessage.getData().get("prop");
        String msg = remoteMessage.getData().get("from_message");

        String messageTitle = remoteMessage.getNotification().getTitle();
        String messageBody = remoteMessage.getNotification().getBody();
        String click_Action = remoteMessage.getNotification().getClickAction();

        String message1 = getResources().getString(R.string.notification1);
        String message2 = getResources().getString(R.string.notification2);
        String message3 = getResources().getString(R.string.notification3);
        String message4 = getResources().getString(R.string.notification4);
        String message5 = getResources().getString(R.string.notification5);
        String message6 = getResources().getString(R.string.notification6);
        String message7 = getResources().getString(R.string.notification7);
        String message8 = getResources().getString(R.string.notification8);
        String message9 = getResources().getString(R.string.notification9);
        String message10 = getResources().getString(R.string.notification10);
        String message11 = getResources().getString(R.string.notification11);
        String message12 = getResources().getString(R.string.notification12);
        String message13 = getResources().getString(R.string.notification13);
        String message14 = getString(R.string.notification14);
        String message15 = getString(R.string.notification15);

        createNotificationChannel();

        if (msg.equals(message1)) {
            click_Action = "com.example.suhei.irent.TARGETNOTIFICATION";
            messageTitle = "Rent Request";

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Intent intent = new Intent(click_Action);
            intent.putExtra("from_user_id", dataFrom);
            intent.putExtra("property_id", pid);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);

            int mNotificationID = (int) System.currentTimeMillis();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(mNotificationID, mBuilder.build());

        } else if (msg.equals(message2)) {
            click_Action = "com.example.suhei.irent.PAYMENT";
            messageTitle = "Response";

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Intent intent = new Intent(click_Action);
            intent.putExtra("from_user_id", dataFrom);
            intent.putExtra("property_id", pid);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);

            int mNotificationID = (int) System.currentTimeMillis();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(mNotificationID, mBuilder.build());

        } else if (msg.equals(message3)) {
            click_Action = "com.example.suhei.irent.DISPLAYPROPERTYTEN";
            messageTitle = "Declined";

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Intent intent = new Intent(click_Action);
            intent.putExtra("from_user_id", dataFrom);
            intent.putExtra("property_id", pid);
            intent.putExtra("declined", "declined");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);

            int mNotificationID = (int) System.currentTimeMillis();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(mNotificationID, mBuilder.build());

        } else if (msg.equals(message4)) {
             click_Action = "com.example.suhei.irent.TEN";
            messageTitle = "New Tenant";

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Intent intent = new Intent(click_Action);
            intent.putExtra("from_user_id", dataFrom);
            intent.putExtra("property_id", pid);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);

            int mNotificationID = (int) System.currentTimeMillis();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(mNotificationID, mBuilder.build());

        } else if (msg.equals(message5)) {
            click_Action = "com.example.suhei.irent.TEN";
            messageTitle = "Rent is due";

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Intent intent = new Intent(click_Action);
            intent.putExtra("from_user_id", dataFrom);
            intent.putExtra("property_id", pid);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);

            int mNotificationID = (int) System.currentTimeMillis();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(mNotificationID, mBuilder.build());

        } else if (msg.equals(message6)) {
            click_Action = "com.example.suhei.irent.PAYMENT";
            messageTitle = "Time to pay rent";

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Intent intent = new Intent(click_Action);
            intent.putExtra("from_user_id", dataFrom);
            intent.putExtra("property_id", pid);
            intent.putExtra("rent", "");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);

            int mNotificationID = (int) System.currentTimeMillis();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(mNotificationID, mBuilder.build());

        } else if (msg.equals(message7)) {
            click_Action = "com.example.suhei.irent.TEN";
            messageTitle = "Payment Received";

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Intent intent = new Intent(click_Action);
            intent.putExtra("from_user_id", dataFrom);
            intent.putExtra("property_id", pid);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);

            int mNotificationID = (int) System.currentTimeMillis();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(mNotificationID, mBuilder.build());


        }
        else if (msg.equals(message8)) {
            click_Action = "com.example.suhei.irent.DISPLAYPROPERTY";
            messageTitle = "Contract Expiry";

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Intent intent = new Intent(click_Action);
            intent.putExtra("from_user_id", dataFrom);
            intent.putExtra("property_id", pid);
            intent.putExtra("rent", "");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);

            int mNotificationID = (int) System.currentTimeMillis();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(mNotificationID, mBuilder.build());
        }
        else if (msg.equals(message9)) {
            click_Action = "com.example.suhei.irent.DISPLAYPROPERTY";
            messageTitle = "End of Contract";

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Intent intent = new Intent(click_Action);
            intent.putExtra("from_user_id", dataFrom);
            intent.putExtra("property_id", pid);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);

            int mNotificationID = (int) System.currentTimeMillis();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(mNotificationID, mBuilder.build());

        }else if (msg.equals(message10)) {
            click_Action = "com.example.suhei.irent.DISPLAYPROPERTYTEN";
            messageTitle = "Would you like to continue?";

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Intent intent = new Intent(click_Action);
            intent.putExtra("from_user_id", dataFrom);
            intent.putExtra("property_id", pid);
            intent.putExtra("contract","");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);


            int mNotificationID = (int) System.currentTimeMillis();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(mNotificationID, mBuilder.build());

        }else if (msg.equals(message11)) {
            click_Action = "com.example.suhei.irent.DISPLAYPROPERTYTEN";
            messageTitle = "Time is up";

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Intent intent = new Intent(click_Action);
            intent.putExtra("from_user_id", dataFrom);
            intent.putExtra("property_id", pid);
            intent.putExtra("contract1","");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);


            int mNotificationID = (int) System.currentTimeMillis();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(mNotificationID, mBuilder.build());

        }else if (msg.equals(message12)) {
            click_Action = "com.example.suhei.irent.TEN";
            messageTitle = "Accepted";

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Intent intent = new Intent(click_Action);
            intent.putExtra("from_user_id", dataFrom);
            intent.putExtra("property_id", pid);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);


            int mNotificationID = (int) System.currentTimeMillis();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(mNotificationID, mBuilder.build());
        }
        else if (msg.equals(message13)) {
            click_Action = "com.example.suhei.irent.TEN";
            messageTitle = "Declined";

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Intent intent = new Intent(click_Action);
            intent.putExtra("from_user_id", dataFrom);
            intent.putExtra("property_id", pid);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);


            int mNotificationID = (int) System.currentTimeMillis();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(mNotificationID, mBuilder.build());
        }
        else if (msg.equals(message14)) {
            click_Action = "com.example.suhei.irent.CHAT";
            messageTitle = "New Message";

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Intent intent = new Intent(click_Action);
            intent.putExtra("user_id", dataFrom);
            intent.putExtra("property_id", pid);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);


            int mNotificationID = (int) System.currentTimeMillis();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(mNotificationID, mBuilder.build());
        }else if (msg.equals(message9)) {
            click_Action = "com.example.suhei.irent.HOMETENANT";
            messageTitle = "End of Contract";

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Intent intent = new Intent(click_Action);
            intent.putExtra("from_user_id", dataFrom);
            intent.putExtra("property_id", pid);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);

            int mNotificationID = (int) System.currentTimeMillis();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(mNotificationID, mBuilder.build());
        }
        }
        private void createNotificationChannel () {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = getString(R.string.channel_name);
                String description = getString(R.string.channel_description);
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }