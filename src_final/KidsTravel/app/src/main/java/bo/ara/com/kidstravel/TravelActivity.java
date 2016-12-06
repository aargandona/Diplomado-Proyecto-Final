package bo.ara.com.kidstravel;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import bo.ara.com.kidstravel.model.Person;
import bo.ara.com.kidstravel.model.Travel;

public class TravelActivity extends AppCompatActivity implements OnClickListener {

    //
    private static final String[] Departments = new String[] {"Beni", "Chuquisaca", "Cochabamba", "La Paz", "Oruro", "Pando", "Potosí",
                                                            "Santa Cruz", "Tarija"};
    //Travel Route
    private AutoCompleteTextView cityOrigin;
    private AutoCompleteTextView cityDestiny;
    private AutoCompleteTextView cityInt;

    //Dates
    private EditText startDateEditText;
    private EditText endDateEditText;

    private DatePickerDialog startDatePickerDialog;
    private DatePickerDialog endDatePickerDialog;

    private SimpleDateFormat dateFormat;

    //Applicant
    private TextView applicant;
    private TextView authorizer;

    //Buttons
    private Button continueBtn;

    //Data
    private Person currentPerson;
    private Travel currentTravel;
    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        //getIntent().getStringExtra()

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

        initControls();
        initDatePickers();

        if (editMode) {
            loadTravelData();
            putControlsReadOnly();
        }
        else
            loadPersonData();
    }

    private void initControls() {
        //Departments
        cityOrigin = (AutoCompleteTextView) findViewById(R.id.tb_cityOrigin);
        cityDestiny = (AutoCompleteTextView) findViewById(R.id.tb_cityDestiny);
        cityInt = (AutoCompleteTextView) findViewById(R.id.tb_cityInt);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, Departments);

        cityOrigin.setAdapter(adapter);
        cityDestiny.setAdapter(adapter);
        cityInt.setAdapter(adapter);

        //Dates
        startDateEditText = (EditText) findViewById(R.id.tb_startDate);
        startDateEditText.setInputType(InputType.TYPE_NULL);
        startDateEditText.setOnClickListener(this);

        //startDatePickerDialog.se
        startDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && !editMode) {
                    startDatePickerDialog.show();
                }
            }
        });

        endDateEditText = (EditText) findViewById(R.id.tb_endDate);
        endDateEditText.setInputType(InputType.TYPE_NULL);
        endDateEditText.setOnClickListener(this);

        endDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && !editMode) {
                    endDatePickerDialog.show();
                }
            }
        });

        //Applicant - Authorizer
        applicant = (TextView) findViewById(R.id.tb_applicant);
        authorizer = (TextView) findViewById(R.id.tb_authorizer);

        //Buttons
        continueBtn = (Button) findViewById(R.id.btn_continue);
        continueBtn.setOnClickListener(this);
    }

    private void initDatePickers(){
        Calendar newCalendar = Calendar.getInstance();
        startDatePickerDialog = new DatePickerDialog(this, R.style.DialogTheme ,new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                startDateEditText.setText(dateFormat.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        endDatePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                endDateEditText.setText(dateFormat.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view) {
        if(view == startDateEditText) {
            if(!editMode)
                startDatePickerDialog.show();
        }
        else if(view == endDateEditText) {
            if(!editMode)
                endDatePickerDialog.show();
        }
        else if(view == continueBtn) {

            boolean result = validateActivity();
            if(result) {
                //load data
                if (!editMode)
                    prepareTravelData();

                //Call to Minors Activity
                Intent intent = new Intent(this, MinorsActivity.class);
                this.startActivity(intent);
            }
        }
    }

    private boolean validateActivity(){
        boolean result = true;

        //Validate city origin
        String cityOrigin = this.cityOrigin.getText().toString().trim();
        if( cityOrigin == "") {
            result = false;
            this.cityOrigin.setError(getString(R.string.error_data_required));
        }

        if(!Arrays.asList(Departments).contains(cityOrigin)) {
            result = false;
            this.cityOrigin.setError(getString(R.string.error_data_noValid));
        }
        //>>

        //Validate city destiny
        String cityDestiny = this.cityDestiny.getText().toString().trim();
        if( cityDestiny == "") {
            result = false;
            this.cityDestiny.setError(getString(R.string.error_data_required));
        }

        if(!Arrays.asList(Departments).contains(cityDestiny)) {
            result = false;
            this.cityDestiny.setError(getString(R.string.error_data_noValid));
        }
        //>>

        //Validate city int
//        String cityInt = this.cityInt.getText().toString().trim();
//        if( cityInt != "") {
//            if(!Arrays.asList(Departments).contains(cityInt)) {
//                result = false;
//                this.cityInt.setError(getString(R.string.error_data_noValid));
//            }
//        }
        //>>

        //Validate start date
        String startDate = this.startDateEditText.getText().toString().trim();
        if( startDate == "") {
            result = false;
            this.startDateEditText.setError(getString(R.string.error_data_required));
        }
        //>>

        //Validate end date
        String endDate = this.endDateEditText.getText().toString().trim();
        if( endDate == "") {
            result = false;
            this.endDateEditText.setError(getString(R.string.error_data_required));
        }
        //>>

        return result;
    }

    private void loadPersonData() {
        if(currentPerson == null) {
            SharedPreferences sp = this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE);

            String userPersonJson = sp.getString("userPerson", "");
            Gson gson = new Gson();
            currentPerson = gson.fromJson(userPersonJson, Person.class);
        }

        applicant.setText(currentPerson.getFullName());

        if(currentPerson.getPartner() != null)
            authorizer.setText(currentPerson.getPartner().getFullName());
    }

    private void prepareTravelData(){

        String travelRoute = String.format("%s - %s", cityOrigin.getText().toString(), cityDestiny.getText().toString());
        if(!cityInt.getText().toString().equals(""))
            travelRoute = String.format("%s - %s - %s", cityOrigin.getText().toString(), cityInt.getText().toString(), cityDestiny.getText().toString());

        String startDate = startDateEditText.getText().toString();
        String endDate = endDateEditText.getText().toString();

        SharedPreferences sharedPreferences = this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("newTravel_travelRoute", travelRoute);
        editor.putString("newTravel_startDate", startDate);
        editor.putString("newTravel_endDate", endDate);
        editor.commit();
    }

    private void putControlsReadOnly(){
        cityOrigin.setKeyListener(null);
        cityOrigin.dismissDropDown();
        cityOrigin.setFocusable(false);
        cityOrigin.setCursorVisible(false);
        cityOrigin.setPressed(false);
        cityOrigin.setClickable(false);

        cityDestiny.setKeyListener(null);
        cityDestiny.dismissDropDown();
        cityDestiny.setFocusable(false);
        cityDestiny.setCursorVisible(false);
        cityDestiny.setPressed(false);
        cityDestiny.setClickable(false);

        cityInt.setKeyListener(null);
        cityInt.dismissDropDown();
        cityInt.setFocusable(false);
        cityInt.setCursorVisible(false);
        cityInt.setPressed(false);
        cityInt.setClickable(false);

        startDateEditText.setKeyListener(null);
        startDateEditText.setFocusable(false);
        startDateEditText.setCursorVisible(false);
        startDateEditText.setPressed(false);
        startDateEditText.setClickable(false);

        endDateEditText.setKeyListener(null);
        endDateEditText.setFocusable(false);
        endDateEditText.setCursorVisible(false);
        endDateEditText.setPressed(false);
        endDateEditText.setClickable(false);
    }

    private void loadTravelData(){
        //Fill Cities
        String travelRoute = currentTravel.getTravelRoute();
        String[] cities = travelRoute.split("-");

        int index = 0;
        for(String city : cities){
            city = city.trim();

            if(cities.length == 2) {
                if (index == 0)
                    cityOrigin.setText(city);
                else if (index == 1)
                    cityDestiny.setText(city);
            }
            else if(cities.length == 3){
                if (index == 0)
                    cityOrigin.setText(city);
                else if (index == 1)
                    cityInt.setText(city);
                else if (index == 2)
                    cityDestiny.setText(city);
            }

            index++;
        }//>>

        //Fill Dates
        startDateEditText.setText(currentTravel.getStartDate());
        endDateEditText.setText(currentTravel.getEndDate());

        //Fill Applicant
        applicant.setText(currentTravel.getApplicant().getFullName());

        //Fill Authorizer
        authorizer.setText(currentTravel.getAuthorizer().getFullName());
    }
}