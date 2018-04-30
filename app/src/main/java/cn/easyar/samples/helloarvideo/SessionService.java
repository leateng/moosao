package cn.easyar.samples.helloarvideo;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by liteng on 2018/4/23.
 */


public interface SessionService {
    @POST("sessions.json")
    Call<User> getSession(@Query("user[email]") String email, @Query("user[password]") String password);
}
