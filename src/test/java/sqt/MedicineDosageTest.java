package sqt;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sqt.MedicineDosage.calculateDosage;

class MedicineDosageTest {
    @ParameterizedTest(name = "Row {0}")
    @MethodSource("gaccTestCases")
    void testCalculateDosage_GACC(int row, int age, double weight, boolean isHighRisk, boolean hasAllergy, double expected) {
        assertEquals(expected, calculateDosage(age, weight, isHighRisk, hasAllergy), 0.001);
    }

    @ParameterizedTest(name = "Row {0}")
    @MethodSource("raccTestCases")
    void testCalculateDosage_RACC(int row, int age, double weight, boolean isHighRisk, boolean hasAllergy, double expected) {
        assertEquals(expected, calculateDosage(age, weight, isHighRisk, hasAllergy), 0.001);
    }

    static Stream<Arguments> gaccTestCases() {
        return Stream.of(
                // GACC Set 1: rows 6, 14, 10, 4, 5
                // Row 6:  a=T,b=F,c=T,d=F → TRUE  → weight*0.8
                Arguments.of(6,  70, 40.0, false, false, 32.0),
                // Row 14: a=F,b=F,c=T,d=F → FALSE → weight*1.2 + age*0.1
                Arguments.of(14, 30, 40.0, false, false, 51.0),
                // Row 10: a=F,b=T,c=T,d=F → TRUE  → weight*0.8
                Arguments.of(10, 30, 40.0, true,  false, 32.0),
                // Row 4:  a=T,b=T,c=F,d=F → FALSE → weight*1.2 + age*0.1
                Arguments.of(4,  70, 60.0, true,  false, 79.0),
                // Row 5:  a=T,b=F,c=T,d=T → FALSE → weight*1.2 + age*0.1
                Arguments.of(5,  70, 40.0, false, true,  55.0),

                // GACC Set 2: rows 6, 14, 10, 8, 9
                // (rows 6, 14, 10 already covered above)
                // Row 8:  a=T,b=F,c=F,d=F → FALSE → weight*1.2 + age*0.1
                Arguments.of(8,  70, 60.0, false, false, 79.0),
                // Row 9:  a=F,b=T,c=T,d=T → FALSE → weight*1.2 + age*0.1
                Arguments.of(9,  30, 40.0, true,  true,  51.0)
        );
    }

    static Stream<Arguments> raccTestCases() {
        return Stream.of(
                // RACC Set 1: rows 6, 14, 10, 12, 9
                // Row 6:  a=T,b=F,c=T,d=F → TRUE  → weight*0.8
                Arguments.of(6,  70, 40.0, false, false, 32.0),
                // Row 14: a=F,b=F,c=T,d=F → FALSE → weight*1.2 + age*0.1
                Arguments.of(14, 30, 40.0, false, false, 51.0),
                // Row 10: a=F,b=T,c=T,d=F → TRUE  → weight*0.8
                Arguments.of(10, 30, 40.0, true,  false, 32.0),
                // Row 12: a=F,b=T,c=F,d=F → FALSE → weight*1.2 + age*0.1
                Arguments.of(12, 30, 60.0, true,  false, 75.0),
                // Row 9:  a=F,b=T,c=T,d=T → FALSE → weight*1.2 + age*0.1
                Arguments.of(9,  30, 40.0, true,  true,  51.0),

                // RACC Set 2: rows 6, 14, 10, 8, 5
                // (rows 6, 14, 10 already covered)
                // Row 8:  a=T,b=F,c=F,d=F → FALSE → weight*1.2 + age*0.1
                Arguments.of(8,  70, 60.0, false, false, 79.0),
                // Row 5:  a=T,b=F,c=T,d=T → FALSE → weight*1.2 + age*0.1
                Arguments.of(5,  70, 40.0, false, true,  55.0)
        );
    }
}