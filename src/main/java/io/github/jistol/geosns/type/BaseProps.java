package io.github.jistol.geosns.type;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "base")
public class BaseProps {
    private String path;
    private String attachPath;
    private String url;
    private Map<String, String> serviceUrl = new HashMap<>();
}
