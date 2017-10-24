package io.github.jistol.geosns.kakao.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface KakaoApiHttp {

    /**
     * 사용자 토큰 유효성 검사 및 정보 얻기
     * @param token
     * @return
     */
    @GET("/v1/user/access_token_info")
    @Headers({"Content-Type: application/x-www-form-urlencoded;charset=utf-8"})
    Call<ResponseBody> callAccessTokenInfo(@Header("Authorization") String token);

    @POST("v1/user/signup")
    @Headers({"Content-Type: application/x-www-form-urlencoded;charset=utf-8"})
    Call<ResponseBody> callSignup(@Header("Authorization") String token);

    @POST("/v1/user/update_profile")
    @Headers({"Content-Type: application/x-www-form-urlencoded;charset=utf-8"})
    Call<ResponseBody> callUpdateProfile(@Header("Authorization") String token);

    @POST("/v1/user/me")
    @Headers({"Content-Type: application/x-www-form-urlencoded;charset=utf-8"})
    Call<ResponseBody> callUserId(@Header("Authorization") String token);

    @POST("/v1/user/unlink")
    @Headers({"Content-Type: application/x-www-form-urlencoded;charset=utf-8"})
    Call<ResponseBody> callUnlinkByAdmin(@Header("Authorization") String adminKey,
                                         @Query("target_id_type") String targetIdType,
                                         @Query("target_id") String targetId);

    default Call<ResponseBody> callUnlinkByAdmin(@Header("Authorization") String adminKey, @Query("target_id") String targetId) {
        return callUnlinkByAdmin(adminKey, "user_id", targetId);
    }

}
