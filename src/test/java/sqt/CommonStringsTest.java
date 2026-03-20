package sqt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;

public class CommonStringsTest {
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {Arrays.asList("Banana", "Apple", "Cherry"), Arrays.asList("APPLE", "cherry"), Arrays.asList("Apple", "Cherry")},
                {Collections.emptyList(), Arrays.asList("APPLE", "cherry"), Collections.emptyList()},
                {Arrays.asList("Banana", "Apple", "Cherry"), Collections.emptyList(), Collections.emptyList()},
                {Arrays.asList("apple"), Arrays.asList("cherry", "Apple"), Arrays.asList("apple")},
                {Arrays.asList("apple", "orange"), Arrays.asList("Orange"), Arrays.asList("orange")},
                {Arrays.asList("apple", "banana"), Arrays.asList("cherry", "orange"), Collections.emptyList()}

        });
    }

    @ParameterizedTest
    @MethodSource("data")
    public void Testing(List<String> list1, List<String> list2, List<String> output) {
        List<String> result = CommonStrings.findCommonIgnoreCase(list1, list2);
        Assertions.assertEquals(output, result);
    }
}