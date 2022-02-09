package pageobject;

import aquality.selenium.browser.AqualityServices;
import aquality.selenium.core.logging.Logger;
import aquality.selenium.elements.interfaces.ILink;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

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
        try {
            AqualityServices.getConditionalWait().waitForTrue(() -> (images.size() > 0));
        } catch (TimeoutException e) {
            Logger.getInstance().error("Images on page not found");
            throw new IllegalArgumentException("Images on page not found", e);
        }
        for (ILink image : images) {
            String link = image.getHref();
            String idImage = getIdImageFromUrl(link);
            listImages.put(idImage, link);
        }
        return listImages;
    }
}
