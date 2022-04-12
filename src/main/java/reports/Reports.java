package reports;

import aquality.selenium.core.logging.Logger;
import modelsdatabase.ImageTable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

import static databasequeries.SearchPageQueries.*;
import static utils.StringUtils.changeLinkForTopImage;

public class Reports {
    public void writeTopImages(List<ImageTable> images, Path pathReport) {
        try {
            for (int i = 0; i < images.size(); i++) {
                String table = "<table width='100%' cellspacing='0' cellpadding='5'><tr><td width='50%' valign='top'>";
                Files.write(pathReport, table.getBytes(), StandardOpenOption.APPEND);
                String imgSrc = "<img src=" + changeLinkForTopImage(images.get(i).link, images.get(i).idPhoto) +
                        "> <a href=https://www.shutterstock.com/" + images.get(i).link +
                        " target='_blank'><br>" + (i + 1) + ") " + images.get(i).idPhoto + "</a><br>";
                Files.write(pathReport, imgSrc.getBytes(), StandardOpenOption.APPEND);
                Files.write(pathReport, "<br></td><td valign='top'>".getBytes(), StandardOpenOption.APPEND);
                List<ImageTable> listPositions = getAllPositionsOfImage(images.get(i).idPhoto);
                writePositionImage(listPositions, pathReport);
                Files.write(pathReport, "<br></td></table>".getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            Logger.getInstance().error("Error IOException: " + e);
        }
    }

    public void writePositionImage(List<ImageTable> listPositions, Path pathReport) {
        int numberTopOld = 0;
        for (int i = 0; i < listPositions.size(); i++) {
            ImageTable position = listPositions.get(i);
            try {
                if (i > 0) {
                    int difference = numberTopOld - Integer.parseInt(position.numTop);
                    if (difference > 0) {
                        String strRed = "<font style='background-color: #fc9692'>" + numberTopOld +
                                " </font> <font color='red'>  &nbsp &nbsp -" + difference + "</font><br>";
                        Files.write(pathReport, strRed.getBytes(), StandardOpenOption.APPEND);
                    } else {
                        String strGreen = "<font style='background-color: #a7f2bb'>" + numberTopOld +
                                " </font> <font color='green'>  &nbsp &nbsp +" + Math.abs(difference) + "</font><br>";
                        Files.write(pathReport, strGreen.getBytes(), StandardOpenOption.APPEND);
                    }
                }
                numberTopOld = Integer.parseInt(position.numTop);
                String placeInTop = (i + 1) + ") " + position.dataWriteInBase + ", Place in the top: ";
                Files.write(pathReport, placeInTop.getBytes(), StandardOpenOption.APPEND);

                if (i == listPositions.size() - 1) {
                    String strGreen = "<font style='background-color: #a7f2bb'>" + numberTopOld + " </font><br>";
                    Files.write(pathReport, strGreen.getBytes(), StandardOpenOption.APPEND);
                }

            } catch (IOException e) {
                Logger.getInstance().error("Error IOException: " + e);
            }
        }
    }

    public List<String> getImagesOnlyInLastScan(Path pathReport) {
        List<String> dates = getAllDatesOfScan();
        String firstDateFromEnd = dates.get(0);
        String secondDateFromEnd = dates.get(1);
        String thirdDateFromEnd = dates.get(2);
        List<String> fistFromEnd = getImagesScanOnSpecificDate(firstDateFromEnd);
        List<String> secondFromEnd = getImagesScanOnSpecificDate(secondDateFromEnd);
        List<String> thirdFromEnd = getImagesScanOnSpecificDate(thirdDateFromEnd);
        StringBuilder str = new StringBuilder();
        str.append(firstDateFromEnd).append(" first date from the end.<br>");
        str.append(secondDateFromEnd).append(" second date from the end.<br>");
        str.append(thirdDateFromEnd).append(" third date from the end.<br>");
        str.append("Number in the first array from the end: ").append(fistFromEnd.size()).append("<br>");
        str.append("Number in the second array from the end: ").append(secondFromEnd.size()).append("<br>");
        str.append("Number in the third array from the end: ").append(thirdFromEnd.size()).append("<br>");

        fistFromEnd.removeAll(secondFromEnd);
        fistFromEnd.removeAll(thirdFromEnd);
        fistFromEnd.sort(Collections.reverseOrder());
        str.append("Number unique in last array: ").append(fistFromEnd.size()).append("<br>");

        try {
            Files.write(pathReport, str.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            Logger.getInstance().error("Error IOException: " + e);
        }
        return fistFromEnd;
    }

    public void writeReportOfLastThreeScans(Path pathReport) {
        List<String> images = getImagesOnlyInLastScan(pathReport);
        String lastScanDate = getAllDatesOfScan().get(0);
        for (int i = 0; i < images.size(); i++) {
            ImageTable imageTable = getImageDataForSpecificDate(images.get(i), lastScanDate);

            String linkPhoto = changeLinkForTopImage(imageTable.link, imageTable.idPhoto);
            String table = "<img src=" + linkPhoto + "> <a href=https://www.shutterstock.com/" + imageTable.link +
                    " target='_blank'><br>" + i + ") " + imageTable.idPhoto + "</a> Place in the top:" + imageTable.numTop + " <br>";

            try {
                Files.write(pathReport, table.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                Logger.getInstance().error("Error IOException: " + e);
            }
        }
    }
}