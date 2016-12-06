package bo.ara.com.kidstravel.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;

import bo.ara.com.kidstravel.DashboardActivity;
import bo.ara.com.kidstravel.MinorsActivity;
import bo.ara.com.kidstravel.R;
import bo.ara.com.kidstravel.UserActivity;
import bo.ara.com.kidstravel.model.Person;
import bo.ara.com.kidstravel.model.Travel;
import bo.ara.com.kidstravel.model.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LENOVO on 8/28/2016.
 */
public class UserAsyncTask extends AsyncTask<Void, Void, User> {
    private UserActivity userActivity;
    private String mCurrentPhotoPath;

    private String personCI;
    private String userName;
    private String userPassword;

    ProgressDialog loadingDialog;

    public UserAsyncTask(UserActivity userActivity, String mCurrentPhotoPath, String personCI, String userName, String userPassword) {
        this.userActivity = userActivity;
        this.mCurrentPhotoPath = mCurrentPhotoPath;
        this.personCI = personCI;
        this.userName = userName;
        this.userPassword = userPassword;

        loadingDialog = new ProgressDialog(this.userActivity);
    }

    @Override
    protected User doInBackground(Void... params) {

        ConnectivityManager cm = (ConnectivityManager) userActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = false;
        if(activeNetwork != null)
            isConnected = activeNetwork.isConnectedOrConnecting();

        if(!isConnected) {
            Log.d("UserActivity", "Connectivity FAIL");

            User user = new User();
            return user;
        }
        else {
            //Get settings
            SharedPreferences sp = userActivity.getSharedPreferences(userActivity.getString(R.string.app_name), Context.MODE_PRIVATE);
            String restApiUrl = sp.getString("restfulApi_URL", "");

            String userPersonJson = sp.getString("userPerson", "");
            Gson gson = new Gson();
            Person userPerson = gson.fromJson(userPersonJson, Person.class);
            String userId = userPerson.getUser().get_id();

            String token = sp.getString("token", "");

            Log.d("UserActivity", "restfuk api = " + restApiUrl);
            Log.d("UserActivity", "userId = " + userId);
            Log.d("UserActivity", "token = " + token);

            TravelService travelService = ServiceGenerator.createService(TravelService.class, token);

            Uri uri = Uri.parse(mCurrentPhotoPath);
            Log.d("UserActivity", "user URI path = " + uri.getPath());
            File file = new File(uri.getPath());

            // create RequestBody instance from file
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

            //Form fields
            RequestBody rbPersonCI = RequestBody.create(MediaType.parse("multipart/form-data"), personCI);
            RequestBody rbUserName = RequestBody.create(MediaType.parse("multipart/form-data"), userName);
            RequestBody rbUserPassword = RequestBody.create(MediaType.parse("multipart/form-data"), userPassword);


            Call<User> call = travelService.createUser(body, rbPersonCI, rbUserName, rbUserPassword);

            try {
                Response<User> response = call.execute();
                Log.d("UserActivity", "Response body: " + response.body());
                User user = response.body();

                return user;
                //return new User();
            }
            //catch (IOException e) {
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onPostExecute(User user) {
        //close LoadingDialog
        loadingDialog.dismiss();

        if (user == null) {
            // Fail
            Toast.makeText(userActivity, "Create user failed", Toast.LENGTH_LONG).show();
        }
        else {
            //if(user.get_id().equals(""))
            if(user.getUsername().equals(""))
                Toast.makeText(userActivity, "Create user failed", Toast.LENGTH_LONG).show();
            else{
                Toast.makeText(userActivity, "Create user was successful, userId: " + user.get_id(), Toast.LENGTH_LONG).show();

                //userActivity.finish();
                Intent intent = new Intent(userActivity, DashboardActivity.class);
                userActivity.startActivity(intent);
            }
        }
    }
}
