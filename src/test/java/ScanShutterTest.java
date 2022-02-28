import aquality.selenium.core.logging.Logger;
import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pageobject.SearchPage;

import java.util.Map;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static databasequeries.SearchPageQueries.addTestInfoInBase;
import static utils.ArithmeticUtils.isMaxValue;
import static utils.MySqlUtils.closeConnection;

public class ScanShutterTest {
    private static final ISettingsFile CONFIG_FILE = new JsonSettingsFile("configData.json");
    private static final ISettingsFile TEST_FILE = new JsonSettingsFile("testData.json");
    private static final int MAX_COUNT_IMAGES = (int) TEST_FILE.getValue("/maxCountImages");
    private static final String TYPE_SCAN = TEST_FILE.getValue("/typeScan").toString();
    private static final String DEFAULT_URL = String.format(CONFIG_FILE.getValue("/mainPage").toString(), TYPE_SCAN);
    private int id = 0;

    @BeforeClass
    protected void beforeMethod() {
        getBrowser().maximize();
    }

    @Test(description = "Working with test data",
            dataProvider = "ScannedPageNumbers", dataProviderClass = DataProviderForTests.class)
    public void testWorkWithTestData(int page) {
        isMaxValue(id, MAX_COUNT_IMAGES);
        getBrowser().goTo(DEFAULT_URL + page);
        SearchPage searchPage = new SearchPage();
        Logger.getInstance().info("Answer isDisplayed():  " + searchPage.state().isDisplayed());
        Logger.getInstance().info("Answer isEnabled():  " + searchPage.state().isEnabled());
        Logger.getInstance().info("Answer isClickable():  " + searchPage.state().isClickable());
        Logger.getInstance().info("Answer waitForDisplayed():  " + searchPage.state().waitForDisplayed());
        Logger.getInstance().info("Answer waitForEnabled():  " + searchPage.state().waitForEnabled());
        if (searchPage.state().waitForDisplayed()) {
            Map<String, String> data = searchPage.getMapImages();
            for (Map.Entry<String, String> entry : data.entrySet()) {
                isMaxValue(id, MAX_COUNT_IMAGES);
                id++;
                addTestInfoInBase(entry.getKey(), String.valueOf(id), entry.getValue());
                Logger.getInstance().info(entry.getKey() + " " + (id) + " " + entry.getValue());
            }
        } else {
            Logger.getInstance().warn("Page number " + (page) + " is not displayed");
        }
    }

    @AfterClass
    public void afterTest() {
        closeConnection();
        getBrowser().quit();
    }
}