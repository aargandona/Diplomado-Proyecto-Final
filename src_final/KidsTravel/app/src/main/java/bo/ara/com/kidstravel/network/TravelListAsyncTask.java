package bo.ara.com.kidstravel.network;

import android.app.ProgressDialog;
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

    ProgressDialog loadingDialog;

    public TravelListAsyncTask(TravelsFragment travelsFragment, boolean adminAccount) {
        this.travelsFragment = travelsFragment;
        this.adminAccount = adminAccount;

        loadingDialog = new ProgressDialog(this.travelsFragment.getContext());
    }

    @Override
    protected void onPreExecute() {
        //Config loading dialog
        loadingDialog.setMessage(travelsFragment.getContext().getString(R.string.dialog_loading));
        loadingDialog.show();

        super.onPreExecute();
    }

    @Override
    protected List<Travel> doInBackground(Void... params) {

        ConnectivityManager cm = (ConnectivityManager)travelsFragment.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = false;
        if(activeNetwork != null)
            isConnected = activeNetwork.isConnectedOrConnecting();

        if(!isConnected) {
            Log.d("TravelFragment", "Connectivity FAIL");
            Log.d("TravelFragment", "Retrieving Travels Data from DB");

            List<Travel> list;
            if(travelStatus == null || travelStatus.equals(""))
                list = Travel.listAll(Travel.class);
            else
                list = Travel.find(Travel.class, "status = ?", travelStatus);

            for (Travel travel: list) {
                travel.fillRelationshipData();
            }

            return list;
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

            Log.d("TravelFragment", "restfuk api = " + restApiUrl);
            Log.d("TravelFragment", "userId = " + userId);
            Log.d("TravelFragment", "token = " + token);

            TravelService travelService = ServiceGenerator.createService(TravelService.class, token);

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
                Log.d("TravelFragment", "Response body: " + response.body());
                List<Travel> travels = response.body();

                //Store users into DB
                storeTravelsIntoDB(travels);

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
        //close LoadingDialog
        loadingDialog.dismiss();

        if (travels == null) {
            // Fail
            Toast.makeText(travelsFragment.getContext(), "Getting travel list failed", Toast.LENGTH_LONG).show();
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

    private void storeTravelsIntoDB(List<Travel> list){
        if(list != null) {
            for (Travel travel : list) {
                try {
                    travel.saveTravel();
                } catch (Exception ex) {
                    Log.d("Store DB procedure", "Error to save travel = " + ex.getMessage());
                }
            }
        }
    }
}
