package io.github.jistol.geosns.google.controller;

import io.github.jistol.geosns.google.http.GoogleMapHttp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import retrofit2.Response;

import java.io.IOException;

@Controller
@RequestMapping("/google")
public class GoogleMapController {
    @Autowired private GoogleMapHttp googleMapHttp;
    @Value("${google.map.key}") private String key;

    @ResponseBody
    @RequestMapping("/map/js")
    public String googleMapJs(@RequestParam("callback") String callback) throws IOException {
        Response<okhttp3.ResponseBody> res = googleMapHttp.callMapJs(key, callback).execute();
        return res.body().string();
    }
}
