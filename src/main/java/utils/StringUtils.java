package utils;

public class StringUtils {
    public static String getIdImageFromUrl(String url) {
        return url.substring(url.lastIndexOf("-") + 1);
    }
}
