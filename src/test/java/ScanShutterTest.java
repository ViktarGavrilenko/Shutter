import aquality.selenium.core.logging.Logger;
import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobject.SearchPage;

import java.util.Map;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static databasequeries.SearchPageQueries.addTestInfoInBase;
import static utils.ArithmeticUtils.isMaxValue;
import static utils.FileUtils.createFolder;
import static utils.MySqlUtils.closeConnection;

public class ScanShutterTest {
    private static final ISettingsFile CONFIG_FILE = new JsonSettingsFile("configData.json");
    private static final ISettingsFile TEST_FILE = new JsonSettingsFile("testData.json");
    private static final int MAX_COUNT_IMAGES = (int) TEST_FILE.getValue("/maxCountImages");
    private static final String TYPE_SCAN = TEST_FILE.getValue("/typeScan").toString();
    private static final String DEFAULT_URL = String.format(CONFIG_FILE.getValue("/mainPage").toString(), TYPE_SCAN);
    private static final String PATH_SCREEN = System.getProperty("user.dir") + TEST_FILE.getValue("/screen").toString();
    private static final int TIME_SLEEP = (int) TEST_FILE.getValue("/timeSleep");
    private static final int NUMBER_RE_SURVEYS = (int) TEST_FILE.getValue("/numberReSurveys");
    private int id = 0;

    @BeforeMethod
    protected void beforeMethod() {
        getBrowser().maximize();
        createFolder(PATH_SCREEN.substring(0, PATH_SCREEN.lastIndexOf("\\")));
    }

    @Test(description = "Working with test data",
            dataProvider = "ScannedPageNumbers", dataProviderClass = DataProviderForTests.class)
    public void testWorkWithTestData(int page) {
        isMaxValue(id, MAX_COUNT_IMAGES);
        SearchPage searchPage;
        for (int i = 0; i < NUMBER_RE_SURVEYS; i++) {
            getBrowser().goTo(DEFAULT_URL + page);
            searchPage = new SearchPage();
            Map<String, String> data = searchPage.getMapImages(String.format(PATH_SCREEN, page));
            Logger.getInstance().info("Data size is " + data.size());
            if (data.size() > 0) {
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    isMaxValue(id, MAX_COUNT_IMAGES);
                    id++;
                    addTestInfoInBase(entry.getKey(), String.valueOf(id), entry.getValue());
                    Logger.getInstance().info(entry.getKey() + " " + (id) + " " + entry.getValue());
                }
                Logger.getInstance().info("Request number is " + i);
                break;
            } else {
                try {
                    Logger.getInstance().info("Thread sleep " + TIME_SLEEP + " ms");
                    Thread.sleep(TIME_SLEEP);
                } catch (InterruptedException e) {
                    Logger.getInstance().info("Error InterruptedException " + e);
                }
            }
        }
    }

    @AfterMethod
    public void afterMethod() {
        getBrowser().quit();
    }

    @AfterClass
    public void afterClass() {
        closeConnection();
    }
}