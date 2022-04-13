import aquality.selenium.core.logging.Logger;
import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;
import modelsdatabase.ImageTable;
import org.openqa.selenium.TimeoutException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pageobject.SearchPage;
import reports.Reports;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static databasequeries.SearchPageQueries.*;
import static utils.ArithmeticUtils.isMaxValue;
import static utils.BrowserUtils.createScreenshot;
import static utils.FileUtils.createFolder;
import static utils.FileUtils.createNewFile;
import static utils.MySqlUtils.closeConnection;

public class ScanShutterTest {
    private static final ISettingsFile CONFIG_FILE = new JsonSettingsFile("configData.json");
    private static final ISettingsFile TEST_FILE = new JsonSettingsFile("testData.json");
    private static final int MAX_COUNT_IMAGES = (int) TEST_FILE.getValue("/maxCountImages");
    private static final int NUMBER_RECENT_SCAN = (int) TEST_FILE.getValue("/numberRecentScan");

    private static final String MAX_COUNT_IMAGES_FOR_REPORTS = TEST_FILE.getValue("/maxCountImagesForReports").toString();
    private static final String TYPE_SCAN = TEST_FILE.getValue("/typeScan").toString();
    private static final String DEFAULT_URL = String.format(CONFIG_FILE.getValue("/mainPage").toString(), TYPE_SCAN);
    private static final String PATH_SCREEN = System.getProperty("user.dir") + TEST_FILE.getValue("/screen").toString();
    private static final String PATH_REPORT = System.getProperty("user.dir") + TEST_FILE.getValue("/report").toString();
    private static final String LAST_THREE_SCANS_REPORT =
            System.getProperty("user.dir") + TEST_FILE.getValue("/lastThreeScans").toString();
    private static final int TIME_SLEEP = (int) TEST_FILE.getValue("/timeSleep");
    private static final int NUMBER_RE_SURVEYS = (int) TEST_FILE.getValue("/numberReSurveys");
    private int id = 0;

    @BeforeClass
    protected void beforeClass() {
        createFolder(PATH_SCREEN.substring(0, PATH_SCREEN.lastIndexOf("\\")));
        createFolder(PATH_REPORT.substring(0, PATH_REPORT.lastIndexOf("\\")));
    }

    @Test(priority = 1, description = "Working with test data",
            dataProvider = "ScannedPageNumbers", dataProviderClass = DataProviderForTests.class)
    public void testWorkWithTestData(int page) {
        getBrowser().maximize();
        isMaxValue(id, MAX_COUNT_IMAGES);
        SearchPage searchPage;
        for (int i = 0; i < NUMBER_RE_SURVEYS; i++) {
            try {
                getBrowser().goTo(DEFAULT_URL + page);
                searchPage = new SearchPage();
                Map<String, String> data = searchPage.getMapImages(String.format(PATH_SCREEN, page));
                Logger.getInstance().info("Data size is " + data.size());
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    isMaxValue(id, MAX_COUNT_IMAGES);
                    id++;
                    addTestInfoInBase(entry.getKey(), String.valueOf(id), entry.getValue());
                    Logger.getInstance().info(entry.getKey() + " " + (id) + " " + entry.getValue());
                }
                Logger.getInstance().info("Request number is " + i);
                break;
            } catch (TimeoutException e) {
                createScreenshot(String.format(PATH_SCREEN, page));
                Logger.getInstance().info("TimeoutException on the page of number " + page);
                try {
                    Logger.getInstance().info("Thread sleep " + TIME_SLEEP + " ms");
                    Thread.sleep(TIME_SLEEP);
                } catch (InterruptedException eSleep) {
                    Logger.getInstance().info("Error InterruptedException " + eSleep);
                }
            }
        }
        getBrowser().quit();
    }

    @Test(priority = 2, description = "Get top images")
    public void testGetTopImages() {
        Logger.getInstance().info("Report 'Top images' is being generated, please wait...");
        Path pathReport = Paths.get(String.format(PATH_REPORT, TYPE_SCAN));
        createNewFile(pathReport);
        List<ImageTable> images = getTopImages(getDateLastScanBase(), MAX_COUNT_IMAGES_FOR_REPORTS);
        Reports reports = new Reports();
        reports.writeTopImages(images, pathReport);
    }

    @Test(priority = 3, description = "Get report of last three scans")
    public void testGetReportOfLastThreeScans() {
        Logger.getInstance().info("Report with last scans is being generated, please wait...");
        Path pathReport = Paths.get(String.format(LAST_THREE_SCANS_REPORT, TYPE_SCAN));
        createNewFile(pathReport);
        Reports reports = new Reports();
        reports.writeReportOfLastThreeScans(pathReport, NUMBER_RECENT_SCAN);
    }

    @AfterClass
    public void afterClass() {
        closeConnection();
    }
}