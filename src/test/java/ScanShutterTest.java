import aquality.selenium.core.logging.Logger;
import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobject.SearchPage;

import java.util.Map;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static databasequeries.SearchPageQueries.addTestInfoInBase;
import static utils.MySqlUtils.closeConnection;

public class ScanShutterTest {
    private static final ISettingsFile CONFIG_FILE = new JsonSettingsFile("configData.json");
    private static final ISettingsFile TEST_FILE = new JsonSettingsFile("testData.json");
    private static final String DEFAULT_URL = CONFIG_FILE.getValue("/mainPage").toString();
    private static final int START_PAGE = (int) TEST_FILE.getValue("/startPage");
    private static final int END_PAGE = (int) TEST_FILE.getValue("/endPage");
    private int id = 0;

    @BeforeMethod
    protected void beforeMethod() {
        getBrowser().maximize();
    }

    @Test(description = "Working with test data")
    public void testWorkWithTestData() {
        for (int i = START_PAGE; i <= END_PAGE; i++) {
            getBrowser().goTo(DEFAULT_URL + i);
            Logger.getInstance().info("Check if the page is loaded " + DEFAULT_URL);
            SearchPage searchPage = new SearchPage();
            Map<String, String> data = searchPage.getMapImages();
            for (Map.Entry<String, String> entry : data.entrySet()) {
                if (id >= 10000) {
                    return;
                }
                addTestInfoInBase(entry.getKey(), String.valueOf(++id), entry.getValue());
//                System.out.println(entry.getKey() + " " + (++id) + " " + entry.getValue());
            }
        }
    }

    @AfterMethod
    public void afterTest(ITestResult result) {
        closeConnection();
        getBrowser().quit();
    }
}
