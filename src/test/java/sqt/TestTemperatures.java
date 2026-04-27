package sqt;

import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertEquals;
import static sqt.Temperatures.analyzeTemperatures;

public class TestTemperatures {
    @Test
    void testInvalidTemperature() {
        int[] temps = {10, 25, 100};
        String result = analyzeTemperatures(temps);
        assertEquals("Invalid temperatures detected.", result);
    }

    @Test
    void testAllWarmDays() {
        int[] temps = {20, 25, 30};
        String result = analyzeTemperatures(temps);
        assertEquals("All days were warm.", result);
    }

    @Test
    void testNoWarmDays() {
        int[] temps = {0, 10, 15};
        String result = analyzeTemperatures(temps);
        assertEquals("No warm days.", result);
    }

    @Test
    void testSomeWarmDays() {
        int[] temps = {10, 20, 15};
        String result = analyzeTemperatures(temps);
        assertEquals("Some days were warm.", result);
    }

    @Test
    void testEmptyArray() {
        int[] temps = {};
        String result = analyzeTemperatures(temps);
        assertEquals("No warm days.", result);
    }

    @Test
    void testBoundaryValues() {
        int[] temps = {-50, 20, 60};
        String result = analyzeTemperatures(temps);
        assertEquals("Some days were warm.", result);
    }
}
