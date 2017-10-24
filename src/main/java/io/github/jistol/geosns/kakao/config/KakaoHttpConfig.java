package io.github.jistol.geosns.kakao.config;

import io.github.jistol.geosns.kakao.http.KakaoApiHttp;
import io.github.jistol.geosns.kakao.http.KakaoOAuthHttp;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class KakaoHttpConfig {
    @Value("${kakao.auth.oauth-url}") private String oauthUrl;
    @Value("${kakao.auth.api-url}") private String apiUrl;

    @Bean
    @Qualifier("kakaoOAuthRetrofit")
    public Retrofit kakaoOAuthRetrofit() {
        return new Retrofit.Builder()
                        .baseUrl(oauthUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
    }

    @Bean
    @Qualifier("kakaoApiRetrofit")
    public Retrofit kakaoApiRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Bean
    public KakaoOAuthHttp kakaoOAuthHttp(@Qualifier("kakaoOAuthRetrofit") Retrofit retrofit) {
        return retrofit.create(KakaoOAuthHttp.class);
    }

    @Bean
    public KakaoApiHttp kakaoApiHttp(@Qualifier("kakaoApiRetrofit") Retrofit retrofit) {
        return retrofit.create(KakaoApiHttp.class);
    }
}
