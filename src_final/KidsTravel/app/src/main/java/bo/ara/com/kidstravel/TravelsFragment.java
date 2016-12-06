package bo.ara.com.kidstravel;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import bo.ara.com.kidstravel.adapter.TravelAdapter;
import bo.ara.com.kidstravel.model.Travel;
import bo.ara.com.kidstravel.network.TravelListAsyncTask;

public class TravelsFragment extends Fragment {

    private TravelAdapter travelAdapter;
    private ListView listView;

    public static TravelsFragment newInstance(boolean adminAccount, String travelStatus) {
        TravelsFragment travelsFragment = new TravelsFragment();
        Bundle args = new Bundle();
        args.putBoolean("adminAccount", adminAccount);
        args.putString("travelStatus", travelStatus);
        travelsFragment.setArguments(args);
        return travelsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travels, container, false);

        //Create Adapter
        travelAdapter = new TravelAdapter(getActivity());//OJO
        listView = (ListView)view.findViewById(R.id.travel_list_view);
        listView.setAdapter(travelAdapter);

        boolean adminAccount = getArguments().getBoolean("adminAccount");
        String travelStatus = getArguments().getString("travelStatus");

        TravelListAsyncTask travelAsyncTask = new TravelListAsyncTask(this, adminAccount);
        travelAsyncTask.setTravelStatus(travelStatus);
        travelAsyncTask.execute();

        //Add listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapter, View view, int position, long arg) {

                Travel travel = (Travel)adapter.getItemAtPosition(position);
                Log.d("TravelsFragment", "selected travel = " + travel.getTravelRoute());

                Gson gson = new Gson();
                String travelJson = gson.toJson(travel);

                SharedPreferences sharedPreferences = TravelsFragment.this.getActivity().getSharedPreferences(TravelsFragment.this.getString(R.string.app_name), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Travel", travelJson);
                editor.commit();

                Intent intent = new Intent(getActivity(), TravelActivity.class);
                //intent.putExtra ("Travel", travelJson);
                startActivity(intent);
            }
        });

        return view;
    }

    public TravelAdapter getTravelAdapter() {
        return travelAdapter;
    }
}
