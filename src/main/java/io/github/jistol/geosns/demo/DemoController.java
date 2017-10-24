package io.github.jistol.geosns.demo;

import io.github.jistol.geosns.google.http.GoogleMapHttp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/demo")
public class DemoController {
    @Autowired private GoogleMapHttp googleMapHttp;
    @Value("${google.map.key}") private String key;

    @RequestMapping("/view/{name}")
    public String view(@PathVariable("name") String name) {
        return "view/demo/" + name;
    }

}
