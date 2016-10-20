package bo.ara.com.kidstravel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import bo.ara.com.kidstravel.model.Person;
import bo.ara.com.kidstravel.model.User;
import bo.ara.com.kidstravel.network.CheckUserAsync;
import bo.ara.com.kidstravel.network.ServiceGenerator;
import bo.ara.com.kidstravel.network.TravelService;
import bo.ara.com.kidstravel.network.UserAsyncTask;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private String mCurrentPhotoPath;

    private ImageView imageView;
    private EditText userCI;
    private EditText userPassword;
    private EditText userName;
    private Button saveButton;

    //Data
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        imageView = (ImageView) findViewById(R.id.user_image_camera);
        imageView.setOnClickListener(this);

        saveButton = (Button) findViewById(R.id.user_btn_save);
        saveButton.setOnClickListener(this);

        userName = (EditText) findViewById(R.id.user_username);
        userCI = (EditText) findViewById(R.id.user_userCI);
        userPassword = (EditText) findViewById(R.id.user_password);

        mCurrentPhotoPath = "";
        //Test
//        mCurrentPhotoPath = "file:/storage/emulated/0/Android/data/bo.ara.com.kidstravel/files/Pictures/JPEG_20160827_131824_-1358791322.jpg";
//
//        Uri uri = Uri.parse(mCurrentPhotoPath);
//        File imgFile = new  File(uri.getPath());
//        if(imgFile.exists()) {
//            Log.d("UserActivity", "user image exist");
//            Bitmap bmImg = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            imageView.setImageBitmap(bmImg);
//        }
        //>>
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Load Image
            Log.d("UserActivity", "user image path = " + mCurrentPhotoPath);

            Uri uri = Uri.parse(mCurrentPhotoPath);
            Log.d("UserActivity", "user URI path = " + uri.getPath());
            File imgFile = new File(uri.getPath());
            if(imgFile.exists()) {
                Log.d("UserActivity", "user image exist");
                Bitmap bmImg = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(bmImg);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view == imageView) {
            dispatchTakePictureIntent();
        }
        else if(view == saveButton) {
            //uploadFile();
            //validateActivity();
            //prepareUserData();
            //createUser();
            checkUserData();
        }
    }

    //Camera methods
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    //>>

    //Upload
    private void uploadFile() {
        //Get settings
        SharedPreferences sp = this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE);
        String restApiUrl = sp.getString("restfulApi_URL", "");

        String userPersonJson = sp.getString("userPerson", "");
        Gson gson = new Gson();
        Person userPerson = gson.fromJson(userPersonJson, Person.class);
        String userId = userPerson.getUser().get_id();

        String token = sp.getString("token", "");

        // create upload service client
        TravelService service = ServiceGenerator.createService(TravelService.class, token);

        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        //File file = FileUtils.getFile(this, fileUri);

        mCurrentPhotoPath = "file:/storage/emulated/0/Android/data/bo.ara.com.kidstravel/files/Pictures/JPEG_20160827_131824_-1358791322.jpg";
        Uri uri = Uri.parse(mCurrentPhotoPath);
        Log.d("UserActivity", "user URI path = " + uri.getPath());
        File file = new File(uri.getPath());

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

        // add another part within the multipart request
        String descriptionString = "hello, this is description speaking";
        RequestBody description =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), descriptionString);

        // finally, execute the request
        Call<ResponseBody> call = service.uploadUser(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                Response<ResponseBody> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    private boolean validateActivity(){
        boolean result = true;

        String userCI = this.userCI.getText().toString().trim();
        String userName = this.userName.getText().toString().trim();
        String userPassword = this.userPassword.getText().toString().trim();

        if(userCI.equals("")) {
            result = false;
            this.userCI.setError("CI es requerido");
        }
        else if(userName.equals("")) {
            result = false;
            this.userName.setError("Nombre usuario es requerido");
        }
        else if(userPassword.equals("")) {
            result = false;
            this.userPassword.setError("Contraseña es requerido");
        }

        return result;
    }

    private boolean validateUserImage(){
        boolean result = true;

        if(!mCurrentPhotoPath.equals("")) {
            Uri uri = Uri.parse(mCurrentPhotoPath);
            File imgFile = new File(uri.getPath());
            result = imgFile.exists();
        }
        else
            result = false;

        return result;
    }

    private void prepareUserData(){
        user = new User();

        user.setUsername(userName.getText().toString());
        user.setPassword(userPassword.getText().toString());

        Person userPerson = new Person();
        userPerson.setCI(Integer.parseInt(userCI.getText().toString()));
        user.setPerson(userPerson);
    }

    private void createUser() {
        //Call Save Travel Async
        UserAsyncTask userAsyncTask = new UserAsyncTask(this, mCurrentPhotoPath, userCI.getText().toString(),
                                                    userName.getText().toString(), userPassword.getText().toString());
        userAsyncTask.execute();
    }
    //>>

    private void checkUserData(){

        boolean result = validateActivity();
        if(result) {
            prepareUserData();

            CheckUserAsync checkUserAync = new CheckUserAsync(this);
            checkUserAync.execute(user);
        }
    }

    public void dataFailed(String message){

        if (message.toLowerCase().equals("username already exist"))
            userName.setError(getString(R.string.error_username_alreadyExist));
        else if (message.toLowerCase().equals("user ci already exist"))
            userCI.setError(getString(R.string.error_userCI_alreadyExist));
        else
            userCI.setError(getString(R.string.error_CI_NotFound));
    }

    public void dataVerified(){
        boolean result = validateUserImage();
        if(result) {
            createUser();
        }
        else{
            userCI.setError(getString(R.string.error_image_notFound));
        }
    }
}
