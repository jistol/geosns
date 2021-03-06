package io.github.jistol.geosns;

import io.github.jistol.geosns.model.Meta;
import io.github.jistol.geosns.util.Util;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.NumberUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Stream;

public class UtilTest {
    @Test
    public void snakeToCamelTest() {
        String[] snakes = {
                "kim_ji_hwan", "kim", "kim_ji_", "_kim", "_kim_ji_hwan_"
        };

        Stream.of(snakes).forEach(snake -> {
            System.out.println(snake + " -> " + Util.snakeToCamel(snake));
        });
    }

    @Test
    public void numberTest() {
        String[] str = {
                "10", "10.5", "abc"
        };

        Stream.of(str).forEach(s -> {
            Double d = NumberUtils.parseNumber(s, Double.class);
            System.out.println(NumberUtils.convertNumberToTargetClass(d, Integer.class));
        });
    }

    @Test
    public void dateSubtractTest() throws InterruptedException {
        Date now = new Date();
        Thread.sleep(1000);

        System.out.println(Util.calFromNow(now.getTime()));
    }

    @Test
    public void fileSizeTest() throws IOException {
        File f = Paths.get("/Users/jistol/IdeaProjects/github/geo-sns/src/main/resources/application.yml").toFile();
        FileInputStream fis = new FileInputStream(f);
        System.out.println("length:" + f.length() + ", channel:" + fis.getChannel().size());
    }

    @Test
    public void lambdaTest() {
        System.out.println(lambdaBean());
        System.out.println(anoClassBean());

        System.out.println(lambdaBean() instanceof Converter);
        System.out.println(anoClassBean() instanceof Converter);

        System.out.println(lambdaBean().getClass().getName());
        System.out.println(anoClassBean().getClass().getName());
    }

    public Converter<String, Meta> lambdaBean() {
        return source -> Util.jsonToBean(source, Meta.class);
    }

    public Converter<String, Meta> anoClassBean() {
        return new Converter<String, Meta>() {
            @Override
            public Meta convert(String source) {
                return Util.jsonToBean(source, Meta.class);
            }
        };
    }
}
