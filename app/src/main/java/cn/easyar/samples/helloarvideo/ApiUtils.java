package cn.easyar.samples.helloarvideo;

/**
 * Created by liteng on 2018/4/23.
 */

public class ApiUtils {

    public static final String BASE_URL = "http://192.168.0.115:3000/api/v1/";

    public static SessionService getSessionService() {
        return RetrofitClient.getClient(BASE_URL).create(SessionService.class);
    }

    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }
}