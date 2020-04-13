package model.webUser;

import dbUtils.DbConn;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DbMods {

    public static StringDataList findById(DbConn dbc, String id) {

        StringDataList sdl = new StringDataList();
        try {
            String sql = "SELECT web_user_id, user_email, user_password, membership_fee, birthday, image, "
                    + "web_user.user_role_id, user_role_type "
                    + "FROM web_user, user_role WHERE web_user.user_role_id = user_role.user_role_id "
                    + "AND web_user_id = ?";

            PreparedStatement stmt = dbc.getConn().prepareStatement(sql);

            // Encode the id (that the user typed in) into the select statement, into the first 
            // (and only) ? 
            stmt.setString(1, id);

            ResultSet results = stmt.executeQuery();
            if (results.next()) { // id is unique, one or zero records expected in result set
                sdl.add(results);
            }
            results.close();
            stmt.close();
        } catch (Exception e) {
            StringData sd = new StringData();
            sd.errorMsg = "Exception thrown in WebUserView.getUserById(): " + e.getMessage();
            sdl.add(sd);
        }
        return sdl;

    } // getUserById

    // method delete returns "" (empty string) if the delete worked fine. Otherwise, 
    // it returns an error message.
    public static String delete(String userId, DbConn dbc) {

        if (userId == null) {
            return "Error in modelwebUser.DbMods.delete: cannot delete web_user record because 'userId' is null";
        }

        // This method assumes that the calling Web API (JSP page) has already confirmed 
        // that the database connection is OK. BUT if not, some reasonable exception should 
        // be thrown by the DB and passed back anyway... 
        String result = ""; // empty string result means the delete worked fine.
        try {

            String sql = "DELETE FROM web_user WHERE web_user_id = ?";

            // This line compiles the SQL statement (checking for syntax errors against your DB).
            PreparedStatement pStatement = dbc.getConn().prepareStatement(sql);

            // Encode user data into the prepared statement.
            pStatement.setString(1, userId);

            int numRowsDeleted = pStatement.executeUpdate();

            if (numRowsDeleted == 0) {
                result = "Record not deleted - there was no record with web_user_id " + userId;
            } else if (numRowsDeleted > 1) {
                result = "Programmer Error: > 1 record deleted. Did you forget the WHERE clause?";
            }

        } catch (Exception e) {
            result = "Exception thrown in model.webUser.DbMods.delete(): " + e.getMessage();
        }

        return result;
    }

} // class
