package io.github.jistol.geosns.google.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface GoogleMapHttp {
    @GET("maps/api/js")
    Call<ResponseBody> callMapJs(@Query("key") String key, @Query("callback") String callback);

}
