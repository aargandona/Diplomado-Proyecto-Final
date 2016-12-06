package bo.ara.com.kidstravel;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.messaging.RemoteMessage;
import com.pusher.android.PusherAndroid;
import com.pusher.android.notifications.ManifestValidator;
import com.pusher.android.notifications.PushNotificationRegistration;
import com.pusher.android.notifications.fcm.FCMPushNotificationReceivedListener;
import com.pusher.android.notifications.interests.InterestSubscriptionChangeListener;
import com.pusher.android.notifications.tokens.PushNotificationRegistrationListener;

import bo.ara.com.kidstravel.model.User;
import bo.ara.com.kidstravel.network.LoginAsyncTask;

public class LoginActivity extends AppCompatActivity {
    private EditText input_username;
    private EditText input_password;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Injections
        input_username = (EditText) findViewById(R.id.input_username);
        input_password = (EditText) findViewById(R.id.input_password);

        hideActionBar();

        //Store preferences
        storeInitPreferences();
    }

    private void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void loginClick(View view) {
        Log.d("LoginActivity", "Login to App...");

        String username = input_username.getText().toString();
        String password = input_password.getText().toString();

        Log.d("LoginActivity", "username: " + username);
        Log.d("LoginActivity", "password: " + password);

        //HTTP Request
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        LoginAsyncTask taskAsync = new LoginAsyncTask(this);
        taskAsync.execute(user);
    }

    private void storeInitPreferences()
    {
        SharedPreferences sharedPreferences = this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //settings
        //editor.putString("restfulApi_URL", "http://192.168.136.1:8080");
        editor.putString("restfulApi_URL", "https://kidstravel.herokuapp.com");

        editor.commit();
    }

    //Register to receive push-notification
//    private boolean playServicesAvailable() {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (apiAvailability.isUserResolvableError(resultCode)) {
//                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
//                        .show();
//            } else {
//                Log.i("LoginActivity", "This device is not supported. Cannot receive push notifications");
//                finish();
//            }
//            return false;
//        }
//        return true;
//    }

    public void registerToReceivePushNotifications(String userName){

        //if (playServicesAvailable()) {
        if (true) {
            Log.d("LoginActivity", "Starting register for push notification, interest = " + userName);

            String pusherApiKey = "361fd7e62d908ba93451";//KidsTravel project
            String interest = userName;

            PusherAndroid pusher = new PusherAndroid(pusherApiKey);
            PushNotificationRegistration nativePusher = pusher.nativePusher();

            //Push notification listener
            PushNotificationRegistrationListener listener = new PushNotificationRegistrationListener() {
                @Override
                public void onSuccessfulRegistration() {
                    Log.d("LoginActivity", "Pusher registration SUCCESSFULL.");
                    //Toast.makeText(LoginActivity.this, "Pusher registration SUCCESSFULL.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailedRegistration(int statusCode, String response) {
                    Log.d("LoginActivity", "Pusher registration FAILED. Code = " + statusCode + " response = " + response);
                    Toast.makeText(LoginActivity.this, "Pusher registration failed. Code = " + statusCode + " response = " + response, Toast.LENGTH_LONG).show();
                }
            };
            //>>

            try {
                nativePusher.registerFCM(this, listener);

                //Subscribe with username as interest
                nativePusher.subscribe(interest, new InterestSubscriptionChangeListener() {
                    @Override
                    public void onSubscriptionChangeSucceeded() {
                        System.out.println("Success! I love kittens!");
                        Log.d("LoginActivity", "Subscribe SUCCESSFULL.");
                        //Toast.makeText(LoginActivity.this, "Subscribe SUCCESSFULL", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSubscriptionChangeFailed(int statusCode, String response) {
                        Log.d("LoginActivity", "Subscribe FAILED. Code = " + statusCode + " response = " + response);
                        Toast.makeText(LoginActivity.this, "Subscribe failed. Code = " + statusCode + " response = " + response, Toast.LENGTH_LONG).show();
                    }
                });

                nativePusher.setFCMListener(new FCMPushNotificationReceivedListener() {
                    @Override
                    public void onMessageReceived(RemoteMessage remoteMessage) {
                        Log.d("LoginActivity", "FCM message received. From = " + remoteMessage.getFrom());
                        //Toast.makeText(LoginActivity.this, "FCM message received. From = " + remoteMessage.getFrom(), Toast.LENGTH_LONG).show();

                        showNotification(remoteMessage);
                    }
                });
            }
            catch (ManifestValidator.InvalidManifestException e) {
                Log.e("LoginActivity", "Register for push notification error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
            Log.d("LoginActivity", "This device is not supported. Cannot receive push notifications");
    }

    public void showNotification(RemoteMessage remoteMessage) {
        Intent resultIntent = new Intent(this, DashboardActivity.class);
        //resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP ); //FLAG_ACTIVITY_CLEAR_TOP

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Get message content
        //int messageId = Integer.parseInt(remoteMessage.getMessageId());
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String content = remoteMessage.getNotification().getBody();

            NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.ticket)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE);

            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());
            //manager.notify(messageId, builder.build());
        }
    }
    //>>
}
