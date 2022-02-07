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
    protected static final ISettingsFile CONFIG_FILE = new JsonSettingsFile("configData.json");
    protected static final String DEFAULT_URL = CONFIG_FILE.getValue("/mainPage").toString();
    private int id = 0;

    @BeforeMethod
    protected void beforeMethod() {
        getBrowser().goTo(DEFAULT_URL);
        getBrowser().maximize();
        Logger.getInstance().info("Check if the page is loaded " + DEFAULT_URL);
    }

    @Test(description = "Working with test data")
    public void testWorkWithTestData() {
        SearchPage searchPage = new SearchPage();
        Map<String, String> data = searchPage.getMapImages();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            addTestInfoInBase(entry.getKey(), String.valueOf(++id), entry.getValue());
        }
    }

    @AfterMethod
    public void afterTest(ITestResult result) {
        closeConnection();
        getBrowser().quit();
    }
}
