package pageobject;

import aquality.selenium.elements.interfaces.ILink;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static aquality.selenium.elements.ElementType.LINK;
import static utils.StringUtils.getIdImageFromUrl;

public class SearchPage extends Form {
    private final List<ILink> images = getElementFactory().findElements(
            By.cssSelector("div[data-automation^='AssetGrids'] a[class^='jss']"), "Links Image", LINK);

    public SearchPage() {
        super(By.cssSelector("div[class^='jss'] h2"), "Page Search");
    }

    public Map<String, String> getMapImages() {
        Map<String, String> listImages = new LinkedHashMap<>();
        for (ILink image : images) {
            String link = image.getHref();
            String idImage = getIdImageFromUrl(link);
            listImages.put(idImage, link);
        }
        return listImages;
    }
}
