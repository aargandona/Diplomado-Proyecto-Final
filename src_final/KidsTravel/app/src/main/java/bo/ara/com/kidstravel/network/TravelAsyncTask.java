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

import bo.ara.com.kidstravel.CardActivity;
import bo.ara.com.kidstravel.DashboardActivity;
import bo.ara.com.kidstravel.MinorsActivity;
import bo.ara.com.kidstravel.R;
import bo.ara.com.kidstravel.model.Person;
import bo.ara.com.kidstravel.model.Travel;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LENOVO on 8/22/2016.
 */
public class TravelAsyncTask extends AsyncTask<Travel, Void, Travel> {
    private MinorsActivity minorsActivity;
    private boolean createMode;
    ProgressDialog loadingDialog;

    public TravelAsyncTask(MinorsActivity minorsActivity, boolean createMode) {
        this.minorsActivity = minorsActivity;
        this.createMode = createMode;

        loadingDialog = new ProgressDialog(this.minorsActivity);
    }

    @Override
    protected void onPreExecute() {
        //Config loading dialog
        loadingDialog.setMessage(minorsActivity.getString(R.string.dialog_loading));
        loadingDialog.show();

        super.onPreExecute();
    }

    @Override
    protected Travel doInBackground(Travel... params) {

        ConnectivityManager cm = (ConnectivityManager) minorsActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = false;
        if(activeNetwork != null)
            isConnected = activeNetwork.isConnectedOrConnecting();

        if(!isConnected) {
            Log.d("TravelActivity", "Connectivity FAIL");

            Travel travel = new Travel();
            return travel;
        }
        else {
            //Get settings
            SharedPreferences sp = minorsActivity.getSharedPreferences(minorsActivity.getString(R.string.app_name), Context.MODE_PRIVATE);
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

            Call<Travel> call;
            if(createMode)
                call = travelService.createTravel(userId, params[0]);
            else
                call = travelService.updateTravel(params[0].get_id(), params[0]);

            try {
                Response<Travel> response = call.execute();
                Log.d("TravelActivity", "Response body: " + response.body());
                Travel travel = response.body();

                return travel;
            }
            //catch (IOException e) {
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onPostExecute(Travel travel) {
        //close LoadingDialog
        loadingDialog.dismiss();

        if (travel == null) {
            // Fail
            Toast.makeText(minorsActivity, "Saving travel failed", Toast.LENGTH_LONG).show();
        }
        else {
            if(travel.get_id().equals(""))
                Toast.makeText(minorsActivity, "Saving travel failed", Toast.LENGTH_LONG).show();
            else{
                //Toast.makeText(minorsActivity, "Create travel was success, travelId: " + travel.get_id(), Toast.LENGTH_LONG).show();
                Toast.makeText(minorsActivity, "Saving travel was success", Toast.LENGTH_LONG).show();
                Log.d("TravelActivity", "Saving travel was success, travelId: " + travel.get_id());

                //Store travel into SharedPreferences
                SharedPreferences sharedPreferences = minorsActivity.getSharedPreferences(minorsActivity.getString(R.string.app_name), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Gson gson = new Gson();
                String travelJson = gson.toJson(travel);

                editor.putString("lastTravel", travelJson);

                editor.commit();
                //>>

                //minorsActivity.finish();

                if(travel.getStatus().toLowerCase().equals("approved")) {
                    Intent intent = new Intent(minorsActivity, CardActivity.class);
                    minorsActivity.startActivity(intent);
                }
                else {
                    Intent intent = new Intent(minorsActivity, DashboardActivity.class);
                    minorsActivity.startActivity(intent);
                }
            }
        }
    }
}