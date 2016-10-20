package bo.ara.com.kidstravel.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bo.ara.com.kidstravel.R;
import bo.ara.com.kidstravel.UserFragment;
import bo.ara.com.kidstravel.model.Person;
import bo.ara.com.kidstravel.model.User;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LENOVO on 8/20/2016.
 */
public class UserListAsyncTask extends AsyncTask<Void, Void, List<User>> {

    private UserFragment userFragment;

    public UserListAsyncTask(UserFragment userFragment) {
        this.userFragment = userFragment;
    }

    @Override
    protected List<User> doInBackground(Void... params) {

        ConnectivityManager cm = (ConnectivityManager)userFragment.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork.isConnectedOrConnecting();

        if(!isConnected) {
            Log.d("LoginActivity", "Connectivity FAIL");

            List<User> users = new ArrayList<User>();
            return users;
        }
        else {
            //Get settings
            SharedPreferences sp = userFragment.getContext().getSharedPreferences(userFragment.getString(R.string.app_name), Context.MODE_PRIVATE);
            String restApiUrl = sp.getString("restfulApi_URL", "");

            String userPersonJson = sp.getString("userPerson", "");
            Gson gson = new Gson();
            Person userPerson = gson.fromJson(userPersonJson, Person.class);
            String userId = userPerson.getUser().get_id();

            String token = sp.getString("token", "");

            Log.d("UserFragment", "restfuk api = " + restApiUrl);
            Log.d("UserFragment", "userId = " + userId);
            Log.d("UserFragment", "token = " + token);

/*
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(restApiUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            TravelService service = retrofit.create(TravelService.class);
*/
            TravelService travelService = ServiceGenerator.createService(TravelService.class, token);

            Call<List<User>> call = travelService.getUsers();

            try {
                Response<List<User>> response = call.execute();
                Log.d("UserFragment", "Response body: " + response.body());
                List<User> users = response.body();

                return users;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<User> users) {

        if (users == null) {
            // Fail
            Toast.makeText(userFragment.getContext(), "Getting users fail", Toast.LENGTH_LONG).show();
        }
        else {
            userFragment.getUserAdapter().clear();
            userFragment.getUserAdapter().addAll(users);
            userFragment.getUserAdapter().setUserList(users);
        }
    }
}
