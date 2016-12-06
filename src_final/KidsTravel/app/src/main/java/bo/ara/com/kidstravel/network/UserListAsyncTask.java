package bo.ara.com.kidstravel.network;

import android.app.ProgressDialog;
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
    ProgressDialog loadingDialog;

    public UserListAsyncTask(UserFragment userFragment) {
        this.userFragment = userFragment;

        loadingDialog = new ProgressDialog(this.userFragment.getContext());
    }

    @Override
    protected void onPreExecute() {
        //Config loading dialog
        loadingDialog.setMessage(userFragment.getContext().getString(R.string.dialog_loading));
        loadingDialog.show();

        super.onPreExecute();
    }

    @Override
    protected List<User> doInBackground(Void... params) {

        ConnectivityManager cm = (ConnectivityManager)userFragment.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = false;
        if(activeNetwork != null)
            isConnected = activeNetwork.isConnectedOrConnecting();

        if(!isConnected) {
            Log.d("UserFragment", "Connectivity FAIL");
            Log.d("UserFragment", "Retrieving Users Data from DB");

            List<User> list = User.listAll(User.class);
            for (User user: list) {
                user.fillRelationshipData();
            }

            return list;
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

            TravelService travelService = ServiceGenerator.createService(TravelService.class, token);

            Call<List<User>> call = travelService.getUsers();

            try {
                Response<List<User>> response = call.execute();
                Log.d("UserFragment", "Response body: " + response.body());
                List<User> users = response.body();

                //Store users into DB
                storeUsersIntoDB(users);

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
        //close LoadingDialog
        loadingDialog.dismiss();

        if (users == null) {
            // Fail
            Toast.makeText(userFragment.getContext(), "Getting user list failed", Toast.LENGTH_LONG).show();
        }
        else {
            userFragment.getUserAdapter().clear();
            userFragment.getUserAdapter().addAll(users);
            userFragment.getUserAdapter().setUserList(users);
        }
    }

    private void storeUsersIntoDB(List<User> list){
        if(list != null) {
            for (User user : list) {
                try {
                    user.saveUser();
                } catch (Exception ex) {
                    Log.d("Store DB procedure", "Error to save user = " + ex.getMessage());
                }
            }
        }
    }
}
