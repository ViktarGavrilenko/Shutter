package reports;

import modelsdatabase.ImageTable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static databasequeries.SearchPageQueries.*;
import static utils.FileUtils.writeInFile;
import static utils.StringUtils.changeLinkForTopImage;

public class Reports {
    public void writeTopImages(List<ImageTable> images, Path pathReport) {
        for (int i = 0; i < images.size(); i++) {
            String table = "<html><head><meta http-equiv='Content-Type' content=text/html; charset=utf-8></head>" +
                    "<body><table width='100%' cellspacing='0' cellpadding='5'><tr><td width='50%' valign='top'>";
            writeInFile(pathReport, table);
            String imgSrc = "<img src=" + changeLinkForTopImage(images.get(i).link, images.get(i).idPhoto) +
                    "> <a href=https://www.shutterstock.com/" + images.get(i).link +
                    " target='_blank'><br>" + (i + 1) + ") " + images.get(i).idPhoto + "</a><br>";
            writeInFile(pathReport, imgSrc);
            writeInFile(pathReport, "<br></td><td valign='top'>");
            List<ImageTable> listPositions = getAllPositionsOfImage(images.get(i).idPhoto);
            writePositionImage(listPositions, pathReport);
            writeInFile(pathReport, "<br></td></table></body></html>");
        }
    }

    public void writePositionImage(List<ImageTable> listPositions, Path pathReport) {
        int numberTopOld = 0;
        for (int i = 0; i < listPositions.size(); i++) {
            ImageTable position = listPositions.get(i);
            if (i > 0) {
                int difference = numberTopOld - Integer.parseInt(position.numTop);
                if (difference > 0) {
                    String strRed = "<font style='background-color: #fc9692'>" + numberTopOld +
                            " </font> <font color='red'>  &nbsp &nbsp -" + difference + "</font><br>";
                    writeInFile(pathReport, strRed);
                } else {
                    String strGreen = "<font style='background-color: #a7f2bb'>" + numberTopOld +
                            " </font> <font color='green'>  &nbsp &nbsp +" + Math.abs(difference) + "</font><br>";
                    writeInFile(pathReport, strGreen);
                }
            }
            numberTopOld = Integer.parseInt(position.numTop);
            String placeInTop = (i + 1) + ") " + position.dataWriteInBase + ", Place in the top: ";
            writeInFile(pathReport, placeInTop);
            if (i == listPositions.size() - 1) {
                String strGreen = "<font style='background-color: #a7f2bb'>" + numberTopOld + " </font><br>";
                writeInFile(pathReport, strGreen);
            }
        }
    }

    public List<String> getImagesOnlyInLastScan(Path pathReport, int numberRecentScan) {
        List<String> dates = getAllDatesOfScan();
        if (dates.size() < numberRecentScan) {
            numberRecentScan = dates.size();
        }
        List<String> imagesLastScan = new ArrayList<>();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < numberRecentScan; i++) {
            String dateScan = dates.get(i);
            List<String> images = getImagesScanOnSpecificDate(dateScan);
            str.append(dateScan).append(" date of scan. Number images in the array: ");
            str.append(images.size()).append("<br>");
            if (i == 0) {
                imagesLastScan = images;
            } else {
                imagesLastScan.removeAll(images);
            }
        }
        imagesLastScan.sort(Collections.reverseOrder());
        str.append("Number unique in last array: ").append(imagesLastScan.size()).append("<br>");
        writeInFile(pathReport, str.toString());
        return imagesLastScan;
    }

    public void writeReportOfLastThreeScans(Path pathReport, int numberRecentScan) {
        String head = "<html><head><meta http-equiv='Content-Type' content=text/html; charset=utf-8></head><body>";
        writeInFile(pathReport, head);
        List<String> images = getImagesOnlyInLastScan(pathReport, numberRecentScan);
        String lastScanDate = getAllDatesOfScan().get(0);
        for (int i = 0; i < images.size(); i++) {
            ImageTable imageTable = getImageDataForSpecificDate(images.get(i), lastScanDate);

            String linkPhoto = changeLinkForTopImage(imageTable.link, imageTable.idPhoto);
            String table = "<img src=" + linkPhoto + "> <a href=https://www.shutterstock.com/" + imageTable.link +
                    " target='_blank'><br>" + (i + 1) + ") " + imageTable.idPhoto + "</a> Place in the top:"
                    + imageTable.numTop + " <br>";
            writeInFile(pathReport, table);
        }
        writeInFile(pathReport, "</body></html>");
    }
}