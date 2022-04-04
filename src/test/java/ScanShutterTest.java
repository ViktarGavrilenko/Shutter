import aquality.selenium.core.logging.Logger;
import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;
import modelsdatabase.ImageTable;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static databasequeries.SearchPageQueries.*;
import static utils.FileUtils.createFolder;
import static utils.MySqlUtils.closeConnection;
import static utils.StringUtils.changeLinkForTopImage;

public class ScanShutterTest {
    private static final ISettingsFile CONFIG_FILE = new JsonSettingsFile("configData.json");
    private static final ISettingsFile TEST_FILE = new JsonSettingsFile("testData.json");
    private static final int MAX_COUNT_IMAGES = (int) TEST_FILE.getValue("/maxCountImages");
    private static final String TYPE_SCAN = TEST_FILE.getValue("/typeScan").toString();
    private static final String DEFAULT_URL = String.format(CONFIG_FILE.getValue("/mainPage").toString(), TYPE_SCAN);
    private static final String PATH_SCREEN = System.getProperty("user.dir") + TEST_FILE.getValue("/screen").toString();
    private static final String PATH_REPORT = System.getProperty("user.dir") + TEST_FILE.getValue("/report").toString();
    private static final int TIME_SLEEP = (int) TEST_FILE.getValue("/timeSleep");
    private static final int NUMBER_RE_SURVEYS = (int) TEST_FILE.getValue("/numberReSurveys");
    private int id = 0;

    @BeforeMethod
    protected void beforeMethod() {
        //getBrowser().maximize();
        createFolder(PATH_SCREEN.substring(0, PATH_SCREEN.lastIndexOf("\\")));
        createFolder(PATH_REPORT.substring(0, PATH_REPORT.lastIndexOf("\\")));
    }

/*    @Test(description = "Working with test data",
            dataProvider = "ScannedPageNumbers", dataProviderClass = DataProviderForTests.class)
    public void testWorkWithTestData(int page) {
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
    }*/

    @Test(description = "Get top images")
    public void testGetTopImages() {
        Path pathReport = Paths.get(String.format(PATH_REPORT, TYPE_SCAN));
        try {
            if (Files.exists(pathReport)) {
                Files.delete(pathReport);
            }
            Files.createFile(pathReport);

            ArrayList<ImageTable> images = getTopImages(getDateLastScanBase());
            for (int i = 0; i < images.size(); i++) {
                Files.write(pathReport, "<table width='100%' cellspacing='0' cellpadding='5'><tr><td width='50%' valign='top'>".getBytes(), StandardOpenOption.APPEND);
                String imgSrc = "<img src=" + changeLinkForTopImage(images.get(i).link, images.get(i).idPhoto) +
                        "> <a href=https://www.shutterstock.com/" + images.get(i).link +
                        " target=\"_blank\"><br>" + (i + 1) + ") " + images.get(i).idPhoto + "</a><br>";
                Files.write(pathReport, imgSrc.getBytes(), StandardOpenOption.APPEND);
                Files.write(pathReport, "<br></td><td valign=\"top\">".getBytes(), StandardOpenOption.APPEND);
                ArrayList<String> positions = getAllPositionsOfImage(images.get(i).idPhoto);
                int countScan = 1;
                int numberTopOld = 0;
                for (int j = 0; j < positions.size(); j++) {
                    if (countScan > 1) {
                        int difference = numberTopOld - Integer.parseInt(positions.get(j));
                        if (difference > 0) {
                            String strRed = "<font style=\"background-color: #fc9692 \">" + numberTopOld + " </font> <font color=\"red\">  &nbsp &nbsp +" + difference + "</font><br>";
                            Files.write(pathReport, strRed.getBytes(), StandardOpenOption.APPEND);
                        } else {
                            String strGreen = "<font style=\"background-color: #a7f2bb \">" + numberTopOld + " </font> <font color=\"green\">  &nbsp &nbsp +" + difference + "</font><br>";
                            Files.write(pathReport, strGreen.getBytes(), StandardOpenOption.APPEND);
                        }
                    }
                    numberTopOld = Integer.parseInt(positions.get(j));
                    String placeInTop = countScan + ") , место в топе";
                    Files.write(pathReport, placeInTop.getBytes(), StandardOpenOption.APPEND);
                    countScan++;
                }

                Files.write(pathReport, "<br></td></table>".getBytes(), StandardOpenOption.APPEND);
            }

        } catch (IOException e) {
            Logger.getInstance().error("Error IOException: " + e);
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