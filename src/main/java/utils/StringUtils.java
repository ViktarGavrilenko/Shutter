package utils;

public class StringUtils {
    public static String getIdImageFromUrl(String url) {
        return url.substring(url.lastIndexOf("-") + 1);
    }

    public static String deleteStartNameOfLink(String link) {
        String label = "image-";
        if (link.contains(label)) {
            return link.substring(link.indexOf(label));
        } else {
            return link;
        }
    }

    public static String changeLinkForTopImage(String link, String idPhoto) {
        return "https://image.shutterstock.com/" +
                link.substring(0, link.lastIndexOf("-")).substring(0, link.lastIndexOf("-"))
                + "-600w-" + idPhoto + ".jpg";
    }
}