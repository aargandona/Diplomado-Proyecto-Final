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
import java.util.ArrayList;
import java.util.List;

import bo.ara.com.kidstravel.DashboardActivity;
import bo.ara.com.kidstravel.R;
import bo.ara.com.kidstravel.TravelsFragment;
import bo.ara.com.kidstravel.model.Person;
import bo.ara.com.kidstravel.model.Travel;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by LENOVO on 8/6/2016.
 */
public class TravelListAsyncTask extends AsyncTask<Void, Void, List<Travel>>{

    private TravelsFragment travelsFragment;
    private String travelStatus;
    private boolean adminAccount;

    public TravelListAsyncTask(TravelsFragment travelsFragment, boolean adminAccount) {
        this.travelsFragment = travelsFragment;
        this.adminAccount = adminAccount;
    }

    @Override
    protected List<Travel> doInBackground(Void... params) {

        ConnectivityManager cm = (ConnectivityManager)travelsFragment.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork.isConnectedOrConnecting();

        if(!isConnected) {
            Log.d("LoginActivity", "Connectivity FAIL");

            List<Travel> travels = new ArrayList<Travel>();
            return travels;
        }
        else {
            //Get settings
            SharedPreferences sp = travelsFragment.getContext().getSharedPreferences(travelsFragment.getString(R.string.app_name), Context.MODE_PRIVATE);
            String restApiUrl = sp.getString("restfulApi_URL", "");

            String userPersonJson = sp.getString("userPerson", "");
            Gson gson = new Gson();
            Person userPerson = gson.fromJson(userPersonJson, Person.class);
            String userId = userPerson.getUser().get_id();

            String token = sp.getString("token", "");

            Log.d("DashboardActivity", "restfuk api = " + restApiUrl);
            Log.d("DashboardActivity", "userId = " + userId);
            Log.d("DashboardActivity", "token = " + token);

/*
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(restApiUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            TravelService service = retrofit.create(TravelService.class);
*/
            TravelService travelService = ServiceGenerator.createService(TravelService.class, token);

            //Call<List<Travel>> call = travelService.getTravelsByUserId(userId);
            Call<List<Travel>> call;

            if(adminAccount ){
                if(travelStatus == null || travelStatus.equals(""))
                    call = travelService.getAllTravels();
                else
                    call = travelService.getTravelsByStatus(travelStatus);
            }
            else {
                if (travelStatus == null || travelStatus.equals(""))
                    call = travelService.getTravelsByUserId(userId);
                else
                    call = travelService.getTravelsByStatus(travelStatus);
            }

            try {
                Response<List<Travel>> response = call.execute();
                Log.d("LoginActivity", "Response body: " + response.body());
                List<Travel> travels = response.body();

                return travels;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Travel> travels) {

        if (travels == null) {
            // Fail
            Toast.makeText(travelsFragment.getContext(), "Getting travels fail", Toast.LENGTH_LONG).show();
        }
        else {
            travelsFragment.getTravelAdapter().clear();
            travelsFragment.getTravelAdapter().addAll(travels);
            travelsFragment.getTravelAdapter().setTravelList(travels);
        }
    }

    public String getTravelStatus() {
        return travelStatus;
    }

    public void setTravelStatus(String travelStatus) {
        this.travelStatus = travelStatus;
    }
}
