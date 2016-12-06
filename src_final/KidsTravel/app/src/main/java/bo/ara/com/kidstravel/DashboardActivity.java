package bo.ara.com.kidstravel;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import bo.ara.com.kidstravel.adapter.ViewPagerAdapter;
import bo.ara.com.kidstravel.model.Person;
import bo.ara.com.kidstravel.network.TravelListAsyncTask;
import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity implements SearchView.OnQueryTextListener  {

    private NavigationView navigationView;

    private Person userPerson;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;

    //Search
    private SearchView searchView;
    private MenuItem searchMenuItem;


    private TabLayout tabLayout;
    private ViewPager viewPager;

    boolean adminAccount;
    private String travelStatus;

    public enum AccessLevel {
        ADMIN, OPERATOR, PARENT
    }
    private AccessLevel accessLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        adminAccount = false;
        travelStatus = "";

        loadPersonData();

        setupToolbar();
        setupNavigationView();
        setupNavDrawerLayout();
        setupTabLayout();
    }

    private void loadPersonData(){
        SharedPreferences sp = this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE);
        String userPersonJson = sp.getString("userPerson", "");

        Gson gson = new Gson();
        userPerson = gson.fromJson(userPersonJson, Person.class);
        Log.d("DashboardActivity", "Person Id = " + userPerson.get_id());

        if(userPerson.getUser().getUserLevel() == 1)
            accessLevel = AccessLevel.ADMIN;
        else if(userPerson.getUser().getUserLevel() == 2)
            accessLevel = AccessLevel.OPERATOR;
        if(userPerson.getUser().getUserLevel() == 3)
            accessLevel = AccessLevel.PARENT;
    }

    private void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.menu);
        ab.setDisplayHomeAsUpEnabled(true);

        appBarLayout = (AppBarLayout) findViewById(R.id.appBar);
        //if(adminAccount)
        if(accessLevel == AccessLevel.ADMIN)
            appBarLayout.setTargetElevation((float) 0);
        else {
            appBarLayout.setExpanded(false);
            appBarLayout.setVisibility(View.GONE);
        }
    }

    private void setupNavigationView(){
        navigationView = (NavigationView)findViewById(R.id.navigation);
        navigationView.setItemIconTintList(null);

        //Security Access - Create User
        if(accessLevel != AccessLevel.ADMIN)
            navigationView.getMenu().findItem(R.id.navMenu_user_add).setVisible(false);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Marcar item presionado

                        switch(menuItem.getItemId()){
                            case R.id.navMenu_requested_travels:
                                Log.d("Menu Nav", "Viajes solicitados");

                                travelStatus = "Requested";
                                closeDrawerLayout();
                                refreshTraveList(travelStatus);
                                return true;
                            case R.id.navMenu_authorized_travels:
                                Log.d("Menu Nav", "Viajes autorizados");

                                travelStatus = "Authorized";
                                closeDrawerLayout();
                                refreshTraveList(travelStatus);
                                return true;
                            case R.id.navMenu_approved_travels:
                                Log.d("Menu Nav", "Viajes aprobados");

                                travelStatus = "Approved";
                                closeDrawerLayout();
                                refreshTraveList(travelStatus);
                                return true;
                            case R.id.navMenu_rejected_travels:
                                Log.d("Menu Nav", "Viajes rechazados");

                                travelStatus = "Rejected";
                                closeDrawerLayout();
                                refreshTraveList(travelStatus);
                                return true;
                            case R.id.navMenu_logout:
                                Log.d("Menu Nav", "Salir");
                                logout();
                                return true;
                            case R.id.navMenu_user_add:
                                Intent intent = new Intent(navigationView.getContext(), UserActivity.class);
                                navigationView.getContext().startActivity(intent);

                                return true;
                        }

                        return true;
                    }
                }
        );
    }

    private void setupNavDrawerLayout(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        View header = navigationView.getHeaderView(0);
        CircleImageView userImage = (CircleImageView)header.findViewById(R.id.navHeader_userImage);
        TextView userFullName = (TextView)header.findViewById(R.id.navHeader_userFullName);
        TextView username = (TextView)header.findViewById(R.id.navHeader_username);

        Glide.with(this).load(userPerson.getUser().getImageUrl()).into(userImage);
        userFullName.setText(userPerson.getFullName());
        username.setText(userPerson.getUser().getUsername());
    }

    private void setupTabLayout(){

        SharedPreferences sp = this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE);
        travelStatus = sp.getString("travelStatus", "");

        tabLayout = (TabLayout)findViewById(R.id.tabs);

        //if(adminAccount) {
        if(accessLevel == AccessLevel.ADMIN) {
            tabLayout.addTab(tabLayout.newTab().setText("Travels"));
            tabLayout.addTab(tabLayout.newTab().setText("Users"));
        }
        else
            tabLayout.addTab(tabLayout.newTab().setText("Travels"));

        //If No Admin - Hide Tabs
        //if(!adminAccount)
        if(accessLevel != AccessLevel.ADMIN)
            tabLayout.setVisibility(View.INVISIBLE);
        //>>

        viewPager = (ViewPager)findViewById(R.id.viewpager);

        //ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), adminAccount);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), accessLevel == AccessLevel.ADMIN, travelStatus);
        viewPager.setAdapter(viewPagerAdapter);

        //Evento cambiar de pagina
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //Evento cambiar de tab (tab seleccionado)
        //if(adminAccount) {
        if(accessLevel == AccessLevel.ADMIN){
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
        else
            viewPager.setCurrentItem(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard, menu);

        //Search
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
               getSearchableInfo(getComponentName()));
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(
        //        new ComponentName(getApplicationContext(), DashboardActivity.class)));
        //searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        //Style
        setupSearchView();

        //return true;
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_newTravel:

                //Remove Travel object from Sharedpreferences
                SharedPreferences sharedPreferences = this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("Travel");
                editor.commit();

                //Call to Travel Activity
                Intent intent = new Intent(this, TravelActivity.class);
                this.startActivity(intent);

                return true;
            case R.id.menu_travelList:
                refreshTraveList();
                return true;
            case android.R.id.home:
                if(drawerLayout != null) {
                    if(drawerLayout.isDrawerOpen(GravityCompat.START))
                        drawerLayout.closeDrawer(GravityCompat.START);
                    else
                        drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    public TravelAdapter getTravelAdapter() {
//        return travelAdapter;
//    }

    //Search
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("DashboarActivity", "onQueryTextChange = " + newText);
        //travelAdapter.getFilter().filter(newText);

        int currenViewItem = viewPager.getCurrentItem();
        ViewPagerAdapter viewPagerAdapter = (ViewPagerAdapter)viewPager.getAdapter();

        if(currenViewItem == 0)
            viewPagerAdapter.getTravelsFragment().getTravelAdapter().getFilter().filter(newText);
        else if(currenViewItem == 1)
            viewPagerAdapter.getUserFragment().getUserAdapter().getFilter().filter(newText);

        return true;
    }

    private void setupSearchView(){
        //SearchView full width
        searchView.setMaxWidth(Integer.MAX_VALUE);

        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        if (searchPlate!=null) {
            searchPlate.setBackgroundColor(Color.WHITE);

            //Text
            int searchTextId = searchPlate.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView searchText = (TextView) searchPlate.findViewById(searchTextId);
            if (searchText!=null) {
                searchText.setTextColor(Color.GRAY);
                searchText.setHintTextColor(Color.GRAY);
                //searchText.setWidth(50);
            }
            //Close Button
            int closeBtnId = searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
            ImageView closeBtn = (ImageView) searchView.findViewById(closeBtnId);
            closeBtn.setColorFilter(Color.GRAY);
            //v.setImageResource(R.drawable.new_travel);

            //Search Hint Image
            //int searchBtnId = searchView.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
            //ImageView searchBtn = (ImageView) searchView.findViewById(searchBtnId);
            //searchBtn.setColorFilter(Color.GRAY);
        }

        int searchImgId = searchView.getContext().getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.search);
    }

    private void refreshTraveList() {
        refreshTraveList("");
    }

    private void refreshTraveList(String travelStatus){

        SharedPreferences sharedPreferences = this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("travelStatus", travelStatus);
        editor.commit();

        ViewPagerAdapter vpAdapter = (ViewPagerAdapter) viewPager.getAdapter();
        TravelsFragment travelsFragment = vpAdapter.getTravelsFragment();

        TravelListAsyncTask travelAsyncTask = new TravelListAsyncTask(travelsFragment, accessLevel == AccessLevel.ADMIN);
        travelAsyncTask.setTravelStatus(travelStatus);
        travelAsyncTask.execute();
    }

    private void closeDrawerLayout(){
        if(drawerLayout != null) {
            if(drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void logout() {
        Log.d("DashboardActivity", "You clicked logout!");

        //
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Está seguro que desea salir?")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        redirectToLogin();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        //>>
    }

    private void redirectToLogin(){
        Log.d("DashboardActivity", "return to login");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
