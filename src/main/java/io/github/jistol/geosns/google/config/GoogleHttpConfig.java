package io.github.jistol.geosns.google.config;

import io.github.jistol.geosns.google.http.GoogleMapHttp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class GoogleHttpConfig {
    @Value("${google.map.url}")
    private String googleMapUrl;

    @Bean
    @Qualifier("googleMapRetrofit")
    public Retrofit googleMapRetrofit() {
        return new Retrofit.Builder()
                        .baseUrl(googleMapUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
    }

    @Bean
    public GoogleMapHttp googleMapHttp(@Qualifier("googleMapRetrofit") Retrofit retrofit) {
        return retrofit.create(GoogleMapHttp.class);
    }
}
