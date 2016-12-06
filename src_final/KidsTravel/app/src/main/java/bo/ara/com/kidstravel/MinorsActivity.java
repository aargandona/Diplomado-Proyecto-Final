package bo.ara.com.kidstravel;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bo.ara.com.kidstravel.adapter.MinorAdapter;
import bo.ara.com.kidstravel.model.MinorItem;
import bo.ara.com.kidstravel.model.Person;
import bo.ara.com.kidstravel.model.Travel;
import bo.ara.com.kidstravel.network.TravelAsyncTask;

public class MinorsActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView minorsLitView;
    private MinorAdapter minorAdapter;

    //Buttons
    private Button buttonA;
    private Button buttonB;

    //Data
    private Person currentPerson;
    ArrayList<MinorItem> minorList;
    private Travel currentTravel;
    private boolean editMode;

    public enum Action {
        CREATE, VIEW, AUTHORIZE, APPROVE
    }

    private Action travelAction;
    private Travel travel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minors);

        buttonA = (Button) findViewById(R.id.card_btnA);
        buttonA.setOnClickListener(this);

        buttonB = (Button) findViewById(R.id.card_btnB);
        buttonB.setOnClickListener(this);

        minorsLitView = (ListView) findViewById(R.id.minors_listView);

        minorAdapter = new MinorAdapter(this);

        editMode = false;
        //For View Travel
        SharedPreferences sp = this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE);
        String travelJson = sp.getString("Travel", "");
        if (travelJson != null && travelJson != "") {
            Gson gson = new Gson();
            currentTravel = gson.fromJson(travelJson, Travel.class);
            editMode = true;
        }
        //>>

        loadCurrentPerson();

        if (editMode) {
            loadTravelData();
            establishTravelAction();
        }
        else {
            loadPersonData();
            travelAction = Action.CREATE;

            minorsLitView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView adapter, View view, int position, long arg) {
                    minorAdapter.setCheckBox(position);
                }
            });
        }

        establishButtons();
    }

    @Override
    public void onClick(View view) {
        if(view == buttonA) {
            //Button for followings actions: AUTHORIZE, APPROVE

            Travel travelToUpdate = new Travel();
            travelToUpdate.set_id(currentTravel.get_id());

            if(travelAction == Action.AUTHORIZE)
                travelToUpdate.setStatus("Authorized");
            else if(travelAction == Action.APPROVE)
                travelToUpdate.setStatus("Approved");

            //Call Update Travel Async
            TravelAsyncTask travelAsyncTask = new TravelAsyncTask(this, false);
            travelAsyncTask.execute(travelToUpdate);
        }
        else if (view == buttonB) {
            //Button for followings actions: CREATE, REJECT
            if(travelAction == Action.CREATE){
                if(validateActivity()) {
                    //Prepare Travel Data
                    prepareTravelData();

                    //Call Save Travel Async
                    TravelAsyncTask travelAsyncTask = new TravelAsyncTask(this, true);
                    travelAsyncTask.execute(travel);
                }
                else
                    Toast.makeText(this, R.string.error_atLessOneMinor_required, Toast.LENGTH_LONG).show();
            }
            else if(travelAction == Action.AUTHORIZE || travelAction == Action.APPROVE){
                Travel travelToUpdate = new Travel();
                travelToUpdate.set_id(currentTravel.get_id());
                travelToUpdate.setStatus("Rejected");

                //Call Update Travel Async
                TravelAsyncTask travelAsyncTask = new TravelAsyncTask(this, false);
                travelAsyncTask.execute(travelToUpdate);
            }
        }
    }

    private void loadCurrentPerson() {
        if(currentPerson == null) {
            SharedPreferences sp = this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE);

            String userPersonJson = sp.getString("userPerson", "");
            Gson gson = new Gson();
            currentPerson = gson.fromJson(userPersonJson, Person.class);
        }
    }

    private void loadPersonData() {
        minorList = new ArrayList<MinorItem>();

        for(Person person : currentPerson.getMinors()){
            MinorItem minorItem = new MinorItem(person.get_id(), person.getFullName(), false, true);
            minorList.add(minorItem);
        }

        minorsLitView.setAdapter(minorAdapter);

        minorAdapter.clear();
        minorAdapter.addAll(minorList);
    }


    private void loadTravelData() {

        minorList = new ArrayList<MinorItem>();

        for(Person person : currentTravel.getMinors()){
            MinorItem minorItem = new MinorItem(person.get_id(), person.getFullName(), true, false);
            minorList.add(minorItem);
        }

        minorsLitView.setAdapter(minorAdapter);

        minorAdapter.clear();
        minorAdapter.addAll(minorList);
    }

    private void prepareTravelData(){
        travel = new Travel();

        //Get Entered Data
        SharedPreferences sp = this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE);
        String travelRoute = sp.getString("newTravel_travelRoute", "");
        String startDate = sp.getString("newTravel_startDate", "");
        String endDate = sp.getString("newTravel_endDate", "");

        //Get Status
        String status = "Requested";

        //Get Creation Date
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        String creationDate = dateFormat.format(date);
        String updateDate = creationDate;
        //>>

        //Get Applicant
        Person applicant = new Person();
        applicant.set_id(currentPerson.get_id());

        //Get Authorizer
        Person authorizer = new Person();
        authorizer.set_id(currentPerson.getPartner().get_id());// Check if authorizer exist

        //Get Selected Minors
        List<Person> minors = new ArrayList<Person>();
        for(MinorItem minorItem: minorList){
            if(minorItem.getMinorSelected()){
                Person minorPerson = new Person();
                minorPerson.set_id(minorItem.get_id());

                minors.add(minorPerson);
            }
        }
        //>>

        //Set values
        travel.setTravelRoute(travelRoute);
        travel.setStartDate(startDate);
        travel.setEndDate(endDate);
        travel.setStatus(status);
        travel.setCreationDate(creationDate);
        travel.setUpdateDate(updateDate);
        travel.setApplicant(applicant);
        travel.setAuthorizer(authorizer);
        travel.setMinors(minors);
    }

    //Call only for editMode=TRUE
    private void establishTravelAction(){

        //String travelUserPersonId = currentTravel.getUser().getPerson().get_id();
        String travelAuthorizerPersonId = currentTravel.getAuthorizer().get_id();
        String personId = currentPerson.get_id();

        travelAction = Action.VIEW;
        if(currentTravel.getStatus().toLowerCase().equals("requested")){

            travelAction = Action.VIEW;
            if(travelAuthorizerPersonId.equals(personId))
                travelAction = Action.AUTHORIZE;

        }
        else if(currentTravel.getStatus().toLowerCase().equals("authorized")) {
            if(!travelAuthorizerPersonId.equals(personId) && (currentPerson.getUser().getUserLevel() == 1 || currentPerson.getUser().getUserLevel() == 3))
                travelAction = Action.APPROVE;
            else
                travelAction = Action.VIEW;
        }
        else if(currentTravel.getStatus().toLowerCase().equals("rejected")) {
            travelAction = Action.VIEW;
        }
        else if(currentTravel.getStatus().toLowerCase().equals("approved")) {
            if( currentPerson.getUser().getUserLevel() == 1 || currentPerson.getUser().getUserLevel() == 3)
                travelAction = Action.VIEW;// Pending - Option to rePrint the certificate
            else
                travelAction = Action.VIEW;
        }

    }

    private void establishButtons(){

        if(travelAction == Action.VIEW){
            buttonA.setVisibility(View.INVISIBLE);
            buttonB.setVisibility(View.INVISIBLE);
        }
        else if(travelAction == Action.CREATE){
            buttonA.setVisibility(View.INVISIBLE);
            buttonB.setVisibility(View.VISIBLE);

            buttonB.setText(getString(R.string.btn_Save));
        }
        else if(travelAction == Action.AUTHORIZE){
            buttonA.setVisibility(View.VISIBLE);
            buttonB.setVisibility(View.VISIBLE);

            buttonA.setText(getString(R.string.btn_Authorize));
            buttonB.setText(getString(R.string.btn_Reject));
        }
        else if(travelAction == Action.APPROVE){
            buttonA.setVisibility(View.VISIBLE);
            buttonB.setVisibility(View.VISIBLE);

            buttonA.setText(getString(R.string.btn_Approve));
            buttonB.setText(getString(R.string.btn_Reject));
        }
    }

    private boolean validateActivity(){
        boolean isSelectedAtLessOneMinor = false;
        for(MinorItem minorItem: minorList){
            if(minorItem.getMinorSelected()){
                isSelectedAtLessOneMinor = true;
                break;
            }
        }

        return isSelectedAtLessOneMinor;
    }
}
