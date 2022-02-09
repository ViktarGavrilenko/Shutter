import org.testng.annotations.DataProvider;

public class DataProviderForTests {
    @DataProvider(name = "ScannedPageNumbers")
    public static Object[][] scannedPageNumbers() {
        return new Object[][]{
                {1},
                {2},
                {3},
                {4},
                {5},
                {6},
                {7},
                {8},
                {9},
                {10},
                {11},
                {12}
        };
    }
}
