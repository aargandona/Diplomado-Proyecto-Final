package bo.ara.com.kidstravel.network;

import java.util.List;

import bo.ara.com.kidstravel.model.Person;
import bo.ara.com.kidstravel.model.Travel;
import bo.ara.com.kidstravel.model.User;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by LENOVO on 7/10/2016.
 */
public interface TravelService {
    @POST("/api/authenticate")
    Call<Person> authenticate(@Body User user);

    @GET("/api/travels/{userId}")
    //Call<List<Travel>> getTravelsByUserId(@Path("userId") String userId, @Body("token") String token);
    Call<List<Travel>> getTravelsByUserId(@Path("userId") String userId);

    @GET("/api/users")
    Call<List<User>> getUsers();

    @POST("/api/travels/{userId}")
    Call<Travel> createTravel(@Path("userId") String userId, @Body Travel travel);

    @Multipart
    @POST("/api/users")
    Call<ResponseBody> uploadUser(@Part("description") RequestBody description,
                                  @Part MultipartBody.Part file);

    @Multipart
    @POST("/api/users")
    Call<User> createUser(@Part MultipartBody.Part file, @Part("personCI") RequestBody personCI,
                          @Part("userName") RequestBody userName,  @Part("userPassword") RequestBody userPassword);

    @POST("/api/users/check")
    Call<String> userCheck(@Body User user);

    @PUT("/api/travels/{travelId}")
    Call<Travel> updateTravel(@Path("travelId") String travelId, @Body Travel travel);

    @GET("/api/travels")
    Call<List<Travel>> getTravelsByStatus(@Query("status") String status);

    @GET("/api/travels")
    Call<List<Travel>> getAllTravels();
}
