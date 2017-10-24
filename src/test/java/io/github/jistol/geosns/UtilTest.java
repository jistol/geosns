package io.github.jistol.geosns;

import io.github.jistol.geosns.util.Util;
import org.junit.Test;
import org.springframework.util.NumberUtils;

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
}
