package databasequeries;

import aquality.selenium.core.logging.Logger;
import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;
import modelsdatabase.ColumnName;
import modelsdatabase.ImageTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static utils.MySqlUtils.*;
import static utils.StringUtils.deleteStartNameOfLink;

public class SearchPageQueries {
    private static final String INSERT_STR = "INSERT INTO %s (id_photo, num_top, link) VALUES (%s, %s,'%s')";
    private static final String SELECT_DATE_WRITE_IN_BASE = "SELECT datawriteinbase FROM %s " +
            "GROUP BY DATE_FORMAT(datawriteinbase, '%%Y%%m%%d') ORDER BY datawriteinbase DESC";
    private static final String SELECT_TOP_IMAGES = "SELECT * FROM (SELECT * FROM %s where " +
            "DATE_FORMAT(datawriteinbase, '%%Y%%m%%d')=DATE_FORMAT( '%s', '%%Y%%m%%d') and num_top < 10000 " +
            "ORDER BY id_photo DESC LIMIT 0, %s) as qwe order by num_top asc;";
    private static final String SELECT_ALL_POSITIONS_OF_IMAGE =
            "SELECT num_top, DATE_FORMAT(datawriteinbase, '%%Y-%%m-%%d') as datawriteinbase FROM %s " +
                    "where id_photo = %s ORDER BY datawriteinbase DESC";
    private static final String SELECT_ALL_DATES_OF_SCAN = "SELECT count(*), datawriteinbase FROM %s GROUP BY " +
            "DATE_FORMAT(datawriteinbase, '%%Y%%m%%d') ORDER BY datawriteinbase DESC";
    private static final String SELECT_IMAGES_SCANNED_ON_SPECIFIC_DATE = "SELECT id_photo FROM %s " +
            "where DATE_FORMAT(datawriteinbase, '%%Y%%m%%d')=DATE_FORMAT('%s', '%%Y%%m%%d') ORDER BY id_photo DESC";
    private static final String SELECT_IMAGE_DATA_FOR_SPECIFIC_DATE = "SELECT * FROM %s where id_photo= %s and " +
            "DATE_FORMAT(datawriteinbase, '%%Y%%m%%d')=DATE_FORMAT('%s', '%%Y%%m%%d')";

    private static final ISettingsFile TEST_FILE = new JsonSettingsFile("testData.json");
    private static final String TYPE_SCAN = TEST_FILE.getValue("/typeScan").toString();

    public static void addTestInfoInBase(String id_photo, String num_top, String link) {
        String insertQuery = String.format(INSERT_STR, TYPE_SCAN, id_photo, num_top, deleteStartNameOfLink(link));
        sendSqlQuery(insertQuery);
    }

    public static String getDateLastScanBase() {
        String query = String.format(SELECT_DATE_WRITE_IN_BASE, TYPE_SCAN);
        ResultSet resultSet = sendSelectQuery(query);
        try {
            resultSet.next();
            return resultSet.getString(ColumnName.datawriteinbase.toString());
        } catch (SQLException e) {
            Logger.getInstance().error(SQL_QUERY_FAILED + e);
            throw new IllegalArgumentException(SQL_QUERY_FAILED, e);
        }
    }

    public static List<ImageTable> getTopImages(String lastDate, String numberOfImage) {
        String query = String.format(SELECT_TOP_IMAGES, TYPE_SCAN, lastDate, numberOfImage);
        ResultSet resultSet = sendSelectQuery(query);
        ArrayList<ImageTable> listImages = new ArrayList<>();
        try {
            while (resultSet.next()) {
                ImageTable image = new ImageTable();
                image.idPhoto = resultSet.getString(ColumnName.id_photo.toString());
                image.numTop = resultSet.getString(ColumnName.num_top.toString());
                image.link = resultSet.getString(ColumnName.link.toString());
                listImages.add(image);
            }
        } catch (SQLException e) {
            Logger.getInstance().error(SQL_QUERY_FAILED + e);
            throw new IllegalArgumentException(SQL_QUERY_FAILED, e);
        }
        return listImages;
    }

    public static List<ImageTable> getAllPositionsOfImage(String idPhoto) {
        String query = String.format(SELECT_ALL_POSITIONS_OF_IMAGE, TYPE_SCAN, idPhoto);
        ResultSet resultSet = sendSelectQuery(query);
        ArrayList<ImageTable> listPositions = new ArrayList<>();
        try {
            while (resultSet.next()) {
                ImageTable image = new ImageTable();
                image.numTop = resultSet.getString(ColumnName.num_top.toString());
                image.dataWriteInBase = resultSet.getString(ColumnName.datawriteinbase.toString());
                listPositions.add(image);
            }
        } catch (SQLException e) {
            Logger.getInstance().error(SQL_QUERY_FAILED + e);
            throw new IllegalArgumentException(SQL_QUERY_FAILED, e);
        }
        return listPositions;
    }

    public static List<String> getAllDatesOfScan() {
        String query = String.format(SELECT_ALL_DATES_OF_SCAN, TYPE_SCAN);
        ResultSet resultSet = sendSelectQuery(query);
        ArrayList<String> listDates = new ArrayList<>();
        try {
            while (resultSet.next()) {
                listDates.add(resultSet.getString(ColumnName.datawriteinbase.toString()));
            }
        } catch (SQLException e) {
            Logger.getInstance().error(SQL_QUERY_FAILED + e);
            throw new IllegalArgumentException(SQL_QUERY_FAILED, e);
        }
        return listDates;
    }

    public static List<String> getImagesScanOnSpecificDate(String date) {
        String query = String.format(SELECT_IMAGES_SCANNED_ON_SPECIFIC_DATE, TYPE_SCAN, date);
        ResultSet resultSet = sendSelectQuery(query);
        ArrayList<String> listImages = new ArrayList<>();
        try {
            while (resultSet.next()) {
                listImages.add(resultSet.getString(ColumnName.id_photo.toString()));
            }
        } catch (SQLException e) {
            Logger.getInstance().error(SQL_QUERY_FAILED + e);
            throw new IllegalArgumentException(SQL_QUERY_FAILED, e);
        }
        return listImages;
    }

    public static ImageTable getImageDataForSpecificDate(String idPhoto, String lastDate) {
        String query = String.format(SELECT_IMAGE_DATA_FOR_SPECIFIC_DATE, TYPE_SCAN, idPhoto, lastDate);
        ResultSet resultSet = sendSelectQuery(query);
        ImageTable image = new ImageTable();
        try {
            resultSet.next();
            image.idPhoto = resultSet.getString(ColumnName.id_photo.toString());
            image.numTop = resultSet.getString(ColumnName.num_top.toString());
            image.link = resultSet.getString(ColumnName.link.toString());

        } catch (SQLException e) {
            Logger.getInstance().error(SQL_QUERY_FAILED + e);
            throw new IllegalArgumentException(SQL_QUERY_FAILED, e);
        }
        return image;
    }
}