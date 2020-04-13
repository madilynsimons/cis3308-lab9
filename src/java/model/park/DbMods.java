    package model.park;
    import dbUtils.DbConn;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    public class DbMods {

            public static StringData findById(DbConn dbc, String id) {

                StringData sd = new StringData();
                try {
                    String sql = "SELECT park_id, name, description, park.image, "
                            + "rating, cost, park.web_user_id, web_user.web_user_id, "
                            + "web_user.image, web_user.user_email "
                            + "FROM park, web_user WHERE park.web_user_id = web_user.web_user_id "
                            + "AND park_id = ?";

                    PreparedStatement stmt = dbc.getConn().prepareStatement(sql);

                    // Encode the id (that the user typed in) into the select statement, into the first
                    // (and only) ?
                    stmt.setString(1, id);

                    ResultSet results = stmt.executeQuery();
                    if (results.next()) { // id is unique, one or zero records expected in result set
                        sd = new StringData(results);
                    } else {
                        sd.errorMsg = "The database has no Park with id " + id;
                    }
                    results.close();
                    stmt.close();
                } catch (Exception e) {
                    sd.errorMsg = "Exception thrown in model.park.DbMods.findById(): " + e.getMessage();
                }
                return sd;

            } // findById


            // method delete returns "" (empty string) if the delete worked fine. Otherwise,
            // it returns an error message.
            public static String delete(String parkId, DbConn dbc) {

                if (parkId == null) {
                    return "Error in model.park.DbMods.delete: cannot delete park record because 'parkId' is null";
                }

                // This method assumes that the calling Web API (JSP page) has already confirmed
                // that the database connection is OK. BUT if not, some reasonable exception should
                // be thrown by the DB and passed back anyway...
                String result = ""; // empty string result means the delete worked fine.
                try {

                    String sql = "DELETE FROM park WHERE park_id = ?";

                    // This line compiles the SQL statement (checking for syntax errors against your DB).
                    PreparedStatement pStatement = dbc.getConn().prepareStatement(sql);

                    // Encode user data into the prepared statement.
                    pStatement.setString(1, parkId);

                    int numRowsDeleted = pStatement.executeUpdate();

                    if (numRowsDeleted == 0) {
                        result = "Record not deleted - there was no record with park_id " + parkId;
                    } else if (numRowsDeleted > 1) {
                        result = "Programmer Error: > 1 record deleted. Did you forget the WHERE clause?";
                    }

                } catch (Exception e) {
                    result = "Exception thrown in model.park.DbMods.delete(): " + e.getMessage();
                }

                return result;
            }

} // class
