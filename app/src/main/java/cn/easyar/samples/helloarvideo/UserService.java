package cn.easyar.samples.helloarvideo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by liteng on 2018/4/24.
 */

public interface UserService {
    @GET("users/{id}/images.json")
    Call<List<ImageInfo>> getImages(@Header("Authorization") String token, @Path("id") int id);
}
