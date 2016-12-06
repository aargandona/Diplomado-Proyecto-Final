package bo.ara.com.kidstravel.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import bo.ara.com.kidstravel.DashboardActivity;
import bo.ara.com.kidstravel.LoginActivity;
import bo.ara.com.kidstravel.R;
import bo.ara.com.kidstravel.model.Person;
import bo.ara.com.kidstravel.model.User;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by LENOVO on 7/10/2016.
 */
public class LoginAsyncTask extends AsyncTask<User, Void, Person> {

    LoginActivity loginActivity;

    public LoginAsyncTask(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    @Override
    protected Person doInBackground(User... params) {
        ConnectivityManager cm = (ConnectivityManager)loginActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = false;
        if(activeNetwork != null)
            isConnected = activeNetwork.isConnectedOrConnecting();

        if(!isConnected) {
            Log.d("LoginActivity", "Connectivity FAIL");
            Log.d("LoginActivity", "Retrieving Person Data from SharedPreferences");

            SharedPreferences sp = loginActivity.getSharedPreferences(loginActivity.getString(R.string.app_name), Context.MODE_PRIVATE);
            String userPersonJson = sp.getString("userPerson", "");

            Gson gson = new Gson();
            Person person = gson.fromJson(userPersonJson, Person.class);

            return person;
        }
        else {

            //Get settings
            SharedPreferences sp = loginActivity.getSharedPreferences(loginActivity.getString(R.string.app_name), Context.MODE_PRIVATE);
            String restApiUrl = sp.getString("restfulApi_URL", "");
            Log.d("Login", "restfuk api = " + restApiUrl);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(restApiUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            TravelService service = retrofit.create(TravelService.class);
            Call<Person> call = service.authenticate(params[0]);

            try {
                Response<Person> response = call.execute();
                Log.d("LoginActivity", "Response body: " + response.body());
                Person person = response.body();

                return person;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    @Override
    protected void onPostExecute(Person person) {
        if (person == null) {
            // Login Fail
            Toast.makeText(loginActivity, "Login failed", Toast.LENGTH_LONG).show();
        }
        else {
            if(person.get_id() != null) {

                SharedPreferences sharedPreferences = loginActivity.getSharedPreferences(loginActivity.getString(R.string.app_name), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Gson gson = new Gson();
                String userPersonJson = gson.toJson(person);
                String token = person.getUser().getToken();

                editor.putString("userPerson", userPersonJson);
                editor.putString("token", token);

                editor.commit();

                Log.d("LoginActivity", "Token asigned: " + token);

                Toast.makeText(loginActivity, "Login successful, username: " + person.getUser().getUsername(), Toast.LENGTH_LONG).show();

                //Save Person into DB
                Log.d("LoginActivity", "Save Person into DB");
                person.savePerson();

                //Register to receive Push Notification
                String username = person.getUser().getUsername();
                loginActivity.registerToReceivePushNotifications(username);
                //>>

                //Call to Dashboard Activity
                Intent intent = new Intent(loginActivity, DashboardActivity.class);
                loginActivity.startActivity(intent);
            }
            else{
                Toast.makeText(loginActivity, "Login failed, connectivity is unavailable. Please wait and tray again", Toast.LENGTH_LONG).show();
            }
        }
    }

}
