package databasequeries;

import static utils.MySqlUtils.sendSqlQuery;

public class SearchPageQueries {
    private static final String INSERT_STR = "INSERT INTO vector (id_photo, num_top, link) VALUES (%s, %s,'%s')";

    public static void addTestInfoInBase(String id_photo, String num_top, String link) {
        String insertQuery = String.format(INSERT_STR, id_photo, num_top, link);
        sendSqlQuery(insertQuery);
    }
}