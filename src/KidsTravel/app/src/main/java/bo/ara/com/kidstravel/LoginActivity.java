package bo.ara.com.kidstravel;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import bo.ara.com.kidstravel.model.User;
import bo.ara.com.kidstravel.network.LoginAsyncTask;

public class LoginActivity extends AppCompatActivity {
    private EditText input_username;
    private EditText input_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Injections
        input_username = (EditText) findViewById(R.id.input_username);
        input_password = (EditText) findViewById(R.id.input_password);

        hideActionBar();

        //Store preferences
        storeInitPreferences();
    }

    private void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void loginClick(View view) {
        Log.d("LoginActivity", "Login to App...");

        String username = input_username.getText().toString();
        String password = input_password.getText().toString();

        Log.d("LoginActivity", "username: " + username);
        Log.d("LoginActivity", "password: " + password);

        //HTTP Request
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        LoginAsyncTask taskAsync = new LoginAsyncTask(this);
        taskAsync.execute(user);
    }

    private void storeInitPreferences()
    {
        SharedPreferences sharedPreferences = this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //settings
        //editor.putString("restfulApi_URL", "http://192.168.136.1:8080");
        editor.putString("restfulApi_URL", "https://kidstravel.herokuapp.com");

        editor.commit();
    }
}
