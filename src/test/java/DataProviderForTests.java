import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;
import org.testng.annotations.DataProvider;

public class DataProviderForTests {
    private static final ISettingsFile TEST_FILE = new JsonSettingsFile("testData.json");
    private static final int START_PAGE = (int) TEST_FILE.getValue("/startPage");
    private static final int END_PAGE = (int) TEST_FILE.getValue("/endPage");

    @DataProvider(name = "ScannedPageNumbers")
    public static Object[][] scannedPageNumbers() {
        int countPages = END_PAGE - START_PAGE + 1;
        Object[][] pages = new Object[countPages][1];

        for (int i = 0; i < countPages; i++) {
            pages[i][0] = START_PAGE + i;
        }
        return pages;
    }
}