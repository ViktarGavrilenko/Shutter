import aquality.selenium.core.logging.Logger;
import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static utils.MySqlUtils.closeConnection;

public class ScanShutterTest {
    private static final ISettingsFile CONFIG_FILE = new JsonSettingsFile("configData.json");
    private static final ISettingsFile TEST_FILE = new JsonSettingsFile("testData.json");
    private static final String TYPE_SCAN = TEST_FILE.getValue("/typeScan").toString();
    private static final String DEFAULT_URL = String.format(CONFIG_FILE.getValue("/mainPage").toString(), TYPE_SCAN);
    private static final String PATH_SCREEN = System.getProperty("user.dir") + TEST_FILE.getValue("/screen").toString();

    @BeforeClass
    protected void beforeMethod() {
        getBrowser().maximize();
        new File(PATH_SCREEN.substring(0, PATH_SCREEN.lastIndexOf("\\"))).mkdir();
    }

    @Test(description = "Working with test data")
    public void testWorkWithTestData() {
        getBrowser().goTo(DEFAULT_URL + "1");
        byte[] bytearray = getBrowser().getScreenshot();
        BufferedImage screen;
        try {
            screen = ImageIO.read(new ByteArrayInputStream(bytearray));
            ImageIO.write(screen, "png", new File(PATH_SCREEN));
        } catch (IOException ioException) {
            Logger.getInstance().error("File read or write exception" + ioException);
        }
    }

    @AfterClass
    public void afterTest() {
        closeConnection();
        getBrowser().quit();
    }
}