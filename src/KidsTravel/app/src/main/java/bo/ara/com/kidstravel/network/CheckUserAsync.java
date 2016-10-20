package bo.ara.com.kidstravel.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import bo.ara.com.kidstravel.R;
import bo.ara.com.kidstravel.UserActivity;
import bo.ara.com.kidstravel.model.Person;
import bo.ara.com.kidstravel.model.User;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LENOVO on 9/6/2016.
 */
public class CheckUserAsync extends AsyncTask<User, Void, String> {
    private UserActivity userActivity;

    public CheckUserAsync(UserActivity userActivity) {
        this.userActivity = userActivity;
    }

    @Override
    protected String doInBackground(User... params) {
        ConnectivityManager cm = (ConnectivityManager) userActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork.isConnectedOrConnecting();

        if(!isConnected) {
            Log.d("UserActivity", "Connectivity FAIL");

            return "Connectivity FAIL";
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

            Log.d("TravelActivity", "restfuk api = " + restApiUrl);
            Log.d("TravelActivity", "userId = " + userId);
            Log.d("TravelActivity", "token = " + token);

            TravelService travelService = ServiceGenerator.createService(TravelService.class, token);

            Call<String> call = travelService.userCheck(params[0]);

            try {
                Response<String> response = call.execute();
                Log.d("UserActivity", "Response body: " + response.body());
                String result = response.body();

                return result;
            }
            //catch (IOException e) {
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    protected void onPostExecute(String result) {

        if (result == null) {
            // Fail
            Toast.makeText(userActivity, "Save travel fail", Toast.LENGTH_LONG).show();
        }
        else {
            if(result.toLowerCase().equals("success")) {
                userActivity.dataVerified();
            }
            else{
                userActivity.dataFailed(result);
            }
        }
    }

}
