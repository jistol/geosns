package io.github.jistol.geosns.kakao.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface KakaoOAuthHttp {
    @POST("oauth/token?grant_type=authorization_code")
    Call<ResponseBody> callToken(@Query("client_id") String clientId,
                                 @Query("redirect_uri") String redirectUri,
                                 @Query("code") String code,
                                 @Query("client_secret") String clientSecret);

    @POST("oauth/token?grant_type=refresh_token")
    Call<ResponseBody> callTokenRefresh(@Query("client_id") String clientId,
                                        @Query("refresh_token") String refreshToken,
                                        @Query("client_secret") String clientSecret);
}
