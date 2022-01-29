import static org.junit.Assert.*;
import org.junit.Test;

public class CompoundInterestTest {

    @Test
    public void testNumYears() {
        /** Sample assert statement for comparing integers.

        assertEquals(0, 0); */
        assertEquals(CompoundInterest.numYears(2025), 3);
        assertEquals(CompoundInterest.numYears(2022), 0);
        assertEquals(CompoundInterest.numYears(3022), 1000);
    }

    @Test
    public void testFutureValue() {
        // When working with decimals, we often want to specify a certain
        // range of "wiggle room", or tolerance. For example, if the answer
        // is 5.04, but anything between 5.02 and 5.06 would be okay too,
        // then we can do assertEquals(5.04, answer, .02).

        // The variable below can be used when you write your tests.
        double tolerance = 0.01;
        assertEquals(12.544, CompoundInterest.futureValue(10, 12, 2024), tolerance);
        assertEquals(90, CompoundInterest.futureValue(90, 0, 3022), tolerance);
        assertEquals(78.78, CompoundInterest.futureValue(125, -5, 2031), tolerance);
    }

    @Test
    public void testFutureValueReal() {
        double tolerance = 0.01;
        assertEquals(295712.29, CompoundInterest.futureValueReal(1000000, 0, 2062, 3), tolerance);
        assertEquals(11.80, CompoundInterest.futureValueReal(10, 12, 2024, 3), tolerance);
        assertEquals(76.7274, CompoundInterest.futureValueReal(50, 5, 2026, -6), tolerance);
    }


    @Test
    public void testTotalSavings() {
        double tolerance = 0.01;
        assertEquals(16550, CompoundInterest.totalSavings(5000, 2024, 10), tolerance);
        assertEquals(28525, CompoundInterest.totalSavings(10000, 2024, -5), tolerance);
        assertEquals(5005000, CompoundInterest.totalSavings(5000, 3022, 0), tolerance);

    }

    @Test
    public void testTotalSavingsReal() {
        double tolerance = 0.01;
        assertEquals(15571.8949, CompoundInterest.totalSavingsReal(5000, 2024, 10, 3), tolerance);
        assertEquals(30262.1725, CompoundInterest.totalSavingsReal(10000, 2024, -5, -3), tolerance);
        assertEquals(5005000, CompoundInterest.totalSavingsReal(5000, 3022, 0, 0), tolerance);
    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(CompoundInterestTest.class));
    }
}
