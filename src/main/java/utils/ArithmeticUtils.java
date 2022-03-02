package utils;

import aquality.selenium.core.logging.Logger;
import org.testng.SkipException;

public class ArithmeticUtils {
    public static void isMaxValue(int value, int maxValue) {
        String SCAN_STOP = String.format("Scanned %s images. Scanning stopped.", maxValue);
        if (value >= maxValue) {
            Logger.getInstance().info(SCAN_STOP);
            throw new SkipException(SCAN_STOP);
        }
    }
}