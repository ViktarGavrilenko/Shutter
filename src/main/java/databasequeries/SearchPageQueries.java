package databasequeries;

import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;

import static utils.MySqlUtils.sendSqlQuery;

public class SearchPageQueries {
    private static final String INSERT_STR = "INSERT INTO %s (id_photo, num_top, link) VALUES (%s, %s,'%s')";
    private static final ISettingsFile TEST_FILE = new JsonSettingsFile("testData.json");
    private static final String TYPE_SCAN = TEST_FILE.getValue("/typeScan").toString();

    public static void addTestInfoInBase(String id_photo, String num_top, String link) {
        String insertQuery = String.format(INSERT_STR, TYPE_SCAN, id_photo, num_top, link);
        sendSqlQuery(insertQuery);
    }
}