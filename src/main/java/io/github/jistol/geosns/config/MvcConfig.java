package io.github.jistol.geosns.config;

import io.github.jistol.geosns.model.Meta;
import io.github.jistol.geosns.util.Util;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {
    public Converter<String, Meta> metaConverter() {
        return new Converter<String, Meta>() {
            @Override
            public Meta convert(String source) {
                return Util.jsonToBean(source, Meta.class);
            }
        };
    }

    @Override
    public void addFormatters(FormatterRegistry formatterRegistry) {
        formatterRegistry.addConverter(metaConverter());
    }
}
