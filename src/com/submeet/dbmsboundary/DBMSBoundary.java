// TODO: hash della password
// TODO: migliorare loggin, quindi togliere printStackTrace

package com.submeet.dbmsboundary;

import com.submeet.client.entity.*;
import com.submeet.client.utility.AppSession;
import com.submeet.client.utility.EncryptionUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.sql.Date;
import java.util.*;

@SuppressWarnings("CallToPrintStackTrace")
public class DBMSBoundary {
    private static Connection connection;

    private static final String URL = "jdbc:mariadb://localhost:3306/submeetdb?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "password";


    // Start connection with DB
    public static void startConnection(){
        try{
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to DB");
        }catch(SQLException e){
            e.printStackTrace();
            System.err.println("Unable to connect to DB");
        }
    }

    //Function used to check if a user already exists
    public static boolean checkCredentials(String email, String password) {
        String sql = """
            SELECT userId, email, password
            FROM EntityUser
            WHERE email = ? AND password = ?
        """;

        String encryptedPassword = EncryptionUtil.encrypt(password);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, encryptedPassword);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // Set global user id
                AppSession.getInstance().setUserId(resultSet.getInt("userId"));
                EntityUser user = DBMSBoundary.getUserInfo(AppSession.getInstance().getUserId());
                AppSession.getInstance().setMail(user.getEmail());
                AppSession.getInstance().setUsername(user.getName());
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    //Function used to insert a new user into the DB
    public static String insertNewUser(String name, String surname, String email, String nationality, String password, String[] specialization){
        // Password regex
        String regexPassword = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{7,}$";
        String regexMail = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";

        // Check if email exists
        if (checkEmailExists(email)) {

        }
        String sqlCheckUser = "SELECT email, userId FROM EntityUser WHERE email=?";
        try (PreparedStatement checkStmt = connection.prepareStatement(sqlCheckUser)) {
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // User already exists
                return "email_exists";
            }

            // Check password regex
            if (!password.matches(regexPassword)) {
                return "password_regex";
            }

            if(!email.matches(regexMail)){
                return "email_regex";
            }

            // Encrypt password
            String encryptedPassword = EncryptionUtil.encrypt(password);

            // Insert new user
            String insertUser = "INSERT INTO EntityUser(name, surname, email, nationality, password) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, name);
                insertStmt.setString(2, surname);
                insertStmt.setString(3, email);
                insertStmt.setString(4, nationality);
                insertStmt.setString(5, encryptedPassword);
                int rows = insertStmt.executeUpdate();

                if (rows == 0) {
                    return "insert_error";
                }

                // Get generated userId
                ResultSet keys = insertStmt.getGeneratedKeys();
                int userId = -1;
                if (keys.next()) {
                    userId = keys.getInt(1);
                } else {
                    return "insert_error";
                }

                // Insert specialization
                if (specialization != null && specialization.length > 0 && !specialization[0].isBlank()) {
                    String insertSpec = "INSERT INTO UserSpecialization(userId, specializationId) VALUES (?, ?)";
                    for (String spec : specialization) {
                        try (PreparedStatement specStmt = connection.prepareStatement(insertSpec)) {
                            specStmt.setInt(1, userId);
                            specStmt.setInt(2, Integer.parseInt(spec));
                            specStmt.executeUpdate();
                        }
                    }
                }
            }

            return "success";
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }

        return "insert_error";
    }


    // Function used to get user email from userId
    public static String getEmail(int userId) {
        String sql = """
            SELECT email
            FROM EntityUser
            WHERE userId = ?
        """;

        // Check if email exists
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString("email");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Function used to check if email exists and get the user password
    public static String getPassword(String email) {
        String sql = """
            SELECT password
            FROM EntityUser
            WHERE email = ?
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return EncryptionUtil.decrypt(resultSet.getString("password"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks whether the specified email exists in the EntityUser table.
     *
     * @param email The email to check.
     * @return true if the email exists, false otherwise.
     */
    public static boolean checkEmailExists(String email) {
        final String sql = "SELECT COUNT(*) FROM EntityUser WHERE email = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }



    public static List<Map<Integer, String>> getSpecializations() {
        String sql = """
                SELECT specializationId, name
                FROM EntitySpecialization
                ORDER BY name ASC""";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            List<Map<Integer, String>> specializations = new ArrayList<>();
            while (resultSet.next()) {
                HashMap<Integer, String> specialization = new HashMap<>();
                specialization.put(resultSet.getInt("specializationId"), resultSet.getString("name"));
                specializations.add(specialization);
            }

            return specializations;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<Map<Integer, String>> getLanguages() {
        String sql = """
                SELECT languageId, language
                FROM EntityLanguage
                ORDER BY language ASC""";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            List<Map<Integer, String>> languages = new ArrayList<>();
            while (resultSet.next()) {
                HashMap<Integer, String> language = new HashMap<>();
                language.put(resultSet.getInt("languageId"), resultSet.getString("language"));
                languages.add(language);
            }

            return languages;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Get user informations by userId
    public static EntityUser getUserInfo(int userId) {
        // Initialize user entity
        EntityUser user = new EntityUser();

        // Set user data
        String sql = """
            SELECT name, surname, email, nationality
            FROM EntityUser
            WHERE userId=?""";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                user = new EntityUser();
                user.setUserId(userId);
                user.setName(resultSet.getString("name"));
                user.setSurname(resultSet.getString("surname"));
                user.setEmail(resultSet.getString("email"));
                user.setNationality(resultSet.getString("nationality"));
            }

            // Set user specializations
            sql = """
                SELECT e.name
                FROM UserSpecialization as u, EntitySpecialization as e
                WHERE u.userId=? AND u.specializationId=e.specializationId
                """;
            try (PreparedStatement statement2 = connection.prepareStatement(sql)) {
                statement2.setInt(1, userId);
                ResultSet resultSet1 = statement2.executeQuery();

                ArrayList<String> specializations = new ArrayList<>();
                while(resultSet1.next()) {
                    specializations.add(resultSet1.getString("name"));
                }

                user.setSpecializations(specializations);

                // Return user entity
                return user;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    //Function used to update user credentials
    public static boolean updateUserData(int userId, String name, String surname, String email, String nationality, String[] specialization) {
        String sql1 = """
            UPDATE EntityUser
            SET name=?, surname=?, email=?, nationality=?
            WHERE userId=?
        """;

        try (PreparedStatement statement1 = connection.prepareStatement(sql1)) {
            //Updating the user
            statement1.setString(1, name);
            statement1.setString(2, surname);
            statement1.setString(3, email);
            statement1.setString(4, nationality);
            statement1.setInt(5, userId);

            statement1.executeQuery();

            //Updating specialization
            String sql2 = """
                DELETE FROM UserSpecialization
                WHERE userId=?
            """;

            PreparedStatement statement2 = connection.prepareStatement(sql2);
            statement2.setInt(1, userId);

            statement2.executeQuery();

            if (specialization != null && specialization.length > 0 && !specialization[0].isBlank()) {
                String sql3 = """
                    INSERT INTO UserSpecialization(userId, specializationId)
                    VALUES (?, ?)
                """;

                for (String spec : specialization) {
                    PreparedStatement statement3 = connection.prepareStatement(sql3);
                    statement3.setInt(1, userId);
                    statement3.setInt(2, Integer.parseInt(spec));
                    statement3.executeUpdate();
                }
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    // Function used to get the user password
    public static String getUserPassword(int userId) {
        String sql = """
            SELECT password
            FROM  EntityUser
            WHERE userId=?
        """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return EncryptionUtil.decrypt(resultSet.getString("password"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Function used to change user password
    public static void changeUserPassword(int userId, String newPassword) {
        String sql = """
        UPDATE EntityUser
        SET password = ?
        WHERE userId = ?
    """;

        String encryptedPassword = EncryptionUtil.encrypt(newPassword);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, encryptedPassword);
            statement.setInt(2, userId);
            statement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Function used to get a conference list based on the user id
    public static List<Map<String, Object>> getConferenceList(int userId, String role){
        String sql = """
                SELECT c.conferenceId, c.title
                FROM EntityConference c, UserConferenceRole ucr
                WHERE c.conferenceId = ucr.conferenceId AND ucr.userId = ? AND ucr.role = ? AND c.scheduleDate > CURDATE()
            """;

        List<Map<String, Object>> conferenceList = new ArrayList<>();

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, userId);
            statement.setString(2, role);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                HashMap<String, Object> conference = new HashMap<>();

                // Put data in the map
                conference.put("conferenceId", resultSet.getInt("conferenceId"));
                conference.put("title", resultSet.getString("title"));

                conferenceList.add(conference);
            }

            return conferenceList;

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static List<Map<String, Object>> getSubReviewerPaper(int userId){
        String sql = """
            SELECT p.paperId, p.title, r.state
            FROM EntityPaper p, EntityReview r
            WHERE p.paperId= r.paperId AND r.isSubReview=1 AND r.reviewerId = ?
        """;

        List<Map<String, Object>> paperList = new ArrayList<>();

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                HashMap<String, Object> paper = new HashMap<>();

                // Put data in the map
                paper.put("paperId", resultSet.getInt("paperId"));
                paper.put("title", resultSet.getString("title"));
                paper.put("state", resultSet.getString("state"));

                paperList.add(paper);
            }

            return paperList;

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    //Function used to get a notification list based on the user id
    public static List<Map<String, Object>> getUserNotifications(int userId) {
        String sql = """
            SELECT notifId, text, type, accepted, visualized, date
            FROM EntityNotification
            WHERE userId = ?
            ORDER BY date DESC
        """;


        List<Map<String, Object>> notifications = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Map<String, Object> notif = new HashMap<>();

                // Put data in the map
                notif.put("notifId", resultSet.getInt("notifId"));
                notif.put("type", resultSet.getString("type"));
                notif.put("accepted", resultSet.getInt("accepted"));
                notif.put("visualized", resultSet.getBoolean("visualized"));
                notif.put("text", resultSet.getString("text"));
                notif.put("date", resultSet.getDate("date"));

                notifications.add(notif);
            }

            return notifications;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Funtion used to get the number of new notifications
    public static int getUserNotificationsCount(int userId) {
        String sql = """
        SELECT COUNT(*) AS notifCount
        FROM EntityNotification
        WHERE userId = ?
    """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt("notifCount");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }


    //Function that returns a notification selected by the user
    public static EntityNotification getNotificInfo(int notifId){
        //Select all notification infos
        String sql = """
            SELECT userId, type, accepted, visualized, conferenceId, paperId, text, date
            FROM EntityNotification
            WHERE notifId = ?
        """;

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, notifId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Create a new notification entity
                EntityNotification entityNotification = new EntityNotification();

                entityNotification.setNotificId(notifId);
                entityNotification.setUserId(resultSet.getInt("userId"));
                entityNotification.setType(resultSet.getString("type"));
                entityNotification.setAccepted(resultSet.getInt("accepted"));
                entityNotification.setVisualized(resultSet.getBoolean("visualized"));
                entityNotification.setConferenceId(resultSet.getInt("conferenceId"));
                entityNotification.setPaperId(resultSet.getInt("paperId"));
                entityNotification.setText(resultSet.getString("text"));
                entityNotification.setDate(resultSet.getDate("date"));

                return entityNotification;
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Set the notific state to visualized
    public static void updateNotificState(int notifId) {
        String sql = """
        UPDATE EntityNotification
        SET visualized = 1
        WHERE notifId = ?
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, notifId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //Function used to accept a notification
    public static boolean acceptInvite(int notifId){
        // Update notification
        String sql = """
            UPDATE EntityNotification
            SET accepted=1
            WHERE notifId = ?
        """;

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, notifId);
            statement.executeQuery();

            // Get data from notification
            String sql2 = """
                    SELECT type, conferenceId, userId, paperId, sender
                    FROM EntityNotification
                    WHERE notifId = ?""";

            try(PreparedStatement statement1 = connection.prepareStatement(sql2)) {
                statement1.setInt(1, notifId);

                statement1.executeQuery();
                ResultSet resultSet = statement1.getResultSet();
                if(resultSet.next()) {
                    String inviteType = resultSet.getString("type");
                    int conferenceId = resultSet.getInt("conferenceId");
                    int userId = resultSet.getInt("userId");
                    int paperId = resultSet.getInt("paperId");
                    int sender = resultSet.getInt("sender");

                    // Inesert with a role
                    String sql3 = """
                                    INSERT INTO UserConferenceRole(userId, conferenceId, role)
                                    VALUES (?, ?, ?)""";

                    try(PreparedStatement statement2 = connection.prepareStatement(sql3)) {
                        statement2.setInt(1, AppSession.getInstance().getUserId());
                        statement2.setInt(2, conferenceId);
                        switch (inviteType) {
                            case "invitation PC":
                                statement2.setString(3, "Reviewer");
                                break;
                            case "invitation EDITOR":
                                statement2.setString(3, "Editor");
                                break;
                            case "invitation SUBREVIEWER":
                                statement2.setString(3, "SubReviewer");

                                // Assign paper to sub reviewer
                                insertPaperSubReviewer(paperId, userId, sender);

                                break;
                        }

                        statement2.executeUpdate();

                        return true;

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    //Function used to decline a notification
    public static boolean denyInvite(int notifId){
        String sql = """
            UPDATE EntityNotification
            SET accepted=-1
            WHERE notifId = ?
        """;

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, notifId);
            statement.executeQuery();

            return true;

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // Function used to create a new conference
    public static boolean createConference(
            String title,
            int reviewerForPaper,
            Date scheduleDate,
            Date submissionDate,
            Date reviewDeadline,
            Date finalVersionDeadline,
            Date invitationDeadline,
            Date paperAssignmentDeadline,
            String[] specialization,
            String[] language) {

        String sqlConference = """
        INSERT INTO EntityConference (
            title, reviewerForPaper,
            scheduleDate, submissionDeadline, reviewDeadline,
            finalVersionDeadline, invitationDeadline, paperAssignmentDeadline,
            templateFile
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (PreparedStatement statement = connection.prepareStatement(sqlConference, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, title);
            statement.setInt(2, reviewerForPaper);
            statement.setDate(3, scheduleDate);
            statement.setDate(4, submissionDate);
            statement.setDate(5, reviewDeadline);
            statement.setDate(6, finalVersionDeadline);
            statement.setDate(7, invitationDeadline);
            statement.setDate(8, paperAssignmentDeadline);
            statement.setNull(9, Types.BLOB); // templateFile = NULL

            int rows = statement.executeUpdate();

            if (rows == 0) {
                System.err.println("Conference insertion failed");
                return false;
            }

            // Retrieve generated conferenceId
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    System.err.println("Could not retrieve conferenceId");
                    return false;
                }
                int conferenceId = keys.getInt(1);

                // Insert specializations
                if (specialization != null) {
                    String insertSpec = "INSERT INTO ConferenceSpecialization(conferenceId, specializationId) VALUES (?, ?)";
                    try (PreparedStatement specStmt = connection.prepareStatement(insertSpec)) {
                        for (String spec : specialization) {
                            if (spec != null && !spec.isBlank()) {
                                specStmt.setInt(1, conferenceId);
                                specStmt.setInt(2, Integer.parseInt(spec));
                                specStmt.addBatch();
                            }
                        }
                        specStmt.executeBatch();
                    }
                }

                // Insert languages
                if (language != null) {
                    String insertLang = "INSERT INTO ConferenceLanguage(conferenceId, languageId) VALUES (?, ?)";
                    try (PreparedStatement langStmt = connection.prepareStatement(insertLang)) {
                        for (String lang : language) {
                            if (lang != null && !lang.isBlank()) {
                                langStmt.setInt(1, conferenceId);
                                langStmt.setInt(2, Integer.parseInt(lang));
                                langStmt.addBatch();
                            }
                        }
                        langStmt.executeBatch();
                    }
                }

                // Insert user as Chair in UserConferenceRole
                String insertRole = "INSERT INTO UserConferenceRole(userId, conferenceId, role) VALUES (?, ?, ?)";
                try (PreparedStatement roleStmt = connection.prepareStatement(insertRole)) {
                    roleStmt.setInt(1, AppSession.getInstance().getUserId());
                    roleStmt.setInt(2, conferenceId);
                    roleStmt.setString(3, "Chair");
                    roleStmt.executeUpdate();
                }

                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static EntityConference getConference(int conferenceId) {
        EntityConference entityConference = new EntityConference();

        // Conference main data + reviewers per paper
        String confSql = """
        SELECT title, scheduleDate, submissionDeadline, reviewDeadline, 
               finalVersionDeadline, reviewerForPaper, invitationDeadline, paperAssignmentDeadline 
        FROM EntityConference
        WHERE conferenceId = ?
    """;

        // Count of submitted papers
        String countSql = """
        SELECT COUNT(*) AS paperCount
        FROM EntityPaper
        WHERE conferenceId = ?
    """;

        // List of papers with their review status
        String papersSql = """
        SELECT p.paperId, p.title
        FROM EntityPaper p
        WHERE p.conferenceId = ?
    """;

        // Languages
        String langSql = """
        SELECT language
        FROM ConferenceLanguage c, EntityLanguage e
        WHERE conferenceId = ? AND c.languageId = e.languageId
    """;

        // Specializations
        String specSql = """
        SELECT name
        FROM ConferenceSpecialization c, EntitySpecialization e
        WHERE conferenceId = ? AND c.specializationId = e.specializationId
    """;

        try {
            // Fetch basic conference info
            try (PreparedStatement stmt = connection.prepareStatement(confSql)) {
                stmt.setInt(1, conferenceId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    entityConference.setConferenceId(conferenceId);
                    entityConference.setTitle(rs.getString("title"));
                    entityConference.setScheduleDate(rs.getDate("scheduleDate"));
                    entityConference.setSubmissionDeadline(rs.getDate("submissionDeadline"));
                    entityConference.setReviewDeadline(rs.getDate("reviewDeadline"));
                    entityConference.setFinalVersionDeadline(rs.getDate("finalVersionDeadline"));
                    entityConference.setInvitationDeadline(rs.getDate("invitationDeadline"));
                    entityConference.setPaperAssignmentDeadline(rs.getDate("paperAssignmentDeadline"));
                    entityConference.setReviewerForPaper(rs.getInt("reviewerForPaper"));
                } else {
                    System.err.println("Conference not found.");
                    return null;
                }
            }

            // Count submitted papers
            try (PreparedStatement stmt = connection.prepareStatement(countSql)) {
                stmt.setInt(1, conferenceId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    entityConference.setSubmittedPapers(rs.getInt("paperCount"));
                }
            }

            // Papers with at least one review and papers with insufficient reviewers
            HashMap<Integer, String> reviewed = new HashMap<>();
            HashMap<Integer, String> underReview = new HashMap<>();
            HashMap<Integer, String> unassigned = new HashMap<>();

            try (PreparedStatement stmt = connection.prepareStatement(papersSql)) {
                stmt.setInt(1, conferenceId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int paperId = rs.getInt("paperId");
                    String title = rs.getString("title");

                    // Count the number of reviewers for this paper
                    String countReviewersSql = """
                            SELECT COUNT(*) AS reviewerCount, p.isFinalVersion, c.reviewerForPaper
                            FROM EntityReview r, EntityPaper p, EntityConference c
                            WHERE r.paperId = ? AND r.paperId = p.paperId AND p.conferenceId = c.conferenceId""";

                    try (PreparedStatement countReviewersStmt = connection.prepareStatement(countReviewersSql)) {
                        countReviewersStmt.setInt(1, paperId);
                        ResultSet countRs = countReviewersStmt.executeQuery();
                        if (countRs.next()) {
                            int reviewerCount = countRs.getInt("reviewerCount");
                            boolean isFinalVersion = countRs.getBoolean("isFinalVersion");
                            int reviewerForPaper = countRs.getInt("reviewerForPaper");

                            if(reviewerCount == 0)
                            {
                                unassigned.put(paperId, title);
                            }
                            else
                            {
                                if(isFinalVersion)
                                {
                                    reviewed.put(paperId, title);
                                }
                                else
                                {
                                    underReview.put(paperId, title);
                                    if (reviewerCount < reviewerForPaper)
                                    {
                                        unassigned.put(paperId, title);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            entityConference.setReviewed(reviewed);
            entityConference.setUnderReview(underReview);
            entityConference.setUnassigned(unassigned);

            // Languages
            try (PreparedStatement stmt = connection.prepareStatement(langSql)) {
                stmt.setInt(1, conferenceId);
                ResultSet rs = stmt.executeQuery();
                List<String> languages = new ArrayList<>();
                while (rs.next()) {
                    languages.add(rs.getString("language"));
                }
                entityConference.setLanguages(languages);
            }

            // Specializations
            try (PreparedStatement stmt = connection.prepareStatement(specSql)) {
                stmt.setInt(1, conferenceId);
                ResultSet rs = stmt.executeQuery();
                List<String> specializations = new ArrayList<>();
                while (rs.next()) {
                    specializations.add(rs.getString("name"));
                }
                entityConference.setSpecializations(specializations);
            }

            return entityConference;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get all final versions of a conferece
    public static List<Map<String, Object>> getAcceptedPapers(int conferenceId) {
        List<Map<String, Object>> finalVersions = new ArrayList<>();
        String sql = """
            SELECT p.paperId, p.title, u.name, u.surname
            FROM EntityPaper p, EntityUser u
            WHERE conferenceId = ? AND p.authorId = u.userId AND p.accepted = 1
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, conferenceId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Map<String, Object> paperData = new HashMap<>();
                paperData.put("paperId", resultSet.getInt("paperId"));
                paperData.put("title", resultSet.getString("title"));
                paperData.put("authorName", resultSet.getString("name"));
                paperData.put("authorSurname", resultSet.getString("surname"));
                finalVersions.add(paperData);
            }

            return finalVersions;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Returns a complete, formatted dump of all conference-related data.
     * @param conferenceId the ID of the conference
     * @return an InputStream with formatted conference data
     */
    public static InputStream getConferenceLog(int conferenceId) {
        StringBuilder builder = new StringBuilder();
        builder.append("Conference Report\n=================\n\n");

        try {
            // 1. Info base della conferenza
            String sqlConference = """
                SELECT * FROM EntityConference WHERE conferenceId = ?
            """;
            try (PreparedStatement stmt = connection.prepareStatement(sqlConference)) {
                stmt.setInt(1, conferenceId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    builder.append("Title: ").append(rs.getString("title")).append("\n");
                    builder.append("ID: ").append(rs.getInt("conferenceId")).append("\n");
                    builder.append("Reviewers per Paper: ").append(rs.getInt("reviewerForPaper")).append("\n");
                    builder.append("Schedule Date: ").append(rs.getDate("scheduleDate")).append("\n");
                    builder.append("Submission Deadline: ").append(rs.getDate("submissionDeadline")).append("\n");
                    builder.append("Review Deadline: ").append(rs.getDate("reviewDeadline")).append("\n");
                    builder.append("Final Version Deadline: ").append(rs.getDate("finalVersionDeadline")).append("\n");
                    builder.append("Invitation Deadline: ").append(rs.getDate("invitationDeadline")).append("\n");
                    builder.append("Paper Assignment Deadline: ").append(rs.getDate("paperAssignmentDeadline")).append("\n\n");
                }
            }

            // 2. Lingue
            builder.append("Languages: ");
            String sqlLang = """
            SELECT language FROM EntityLanguage 
            JOIN ConferenceLanguage USING(languageId)
            WHERE conferenceId = ?
        """;
            try (PreparedStatement stmt = connection.prepareStatement(sqlLang)) {
                stmt.setInt(1, conferenceId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    builder.append(rs.getString("language")).append(", ");
                }
            }
            builder.append("\n");

            // 3. Specializzazioni
            builder.append("Specializations: ");
            String sqlSpec = """
            SELECT name FROM EntitySpecialization
            JOIN ConferenceSpecialization USING(specializationId)
            WHERE conferenceId = ?
        """;
            try (PreparedStatement stmt = connection.prepareStatement(sqlSpec)) {
                stmt.setInt(1, conferenceId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    builder.append(rs.getString("name")).append(", ");
                }
            }
            builder.append("\n\n");

            // 4. Papers
            builder.append("Papers:\n");
            String sqlPapers = """
            SELECT p.paperId, p.title, u.name, u.surname, p.accepted 
            FROM EntityPaper p 
            JOIN EntityUser u ON p.authorId = u.userId 
            WHERE p.conferenceId = ?
        """;
            try (PreparedStatement stmt = connection.prepareStatement(sqlPapers)) {
                stmt.setInt(1, conferenceId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String status = switch (rs.getInt("accepted")) {
                        case 1 -> "Accepted";
                        case -1 -> "Rejected";
                        default -> "Pending";
                    };
                    builder.append(String.format("  - [%d] %s by %s %s - Status: %s\n",
                            rs.getInt("paperId"),
                            rs.getString("title"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            status));
                }
            }

            // 5. Reviews
            builder.append("\nReviews:\n");
            String sqlReviews = """
            SELECT r.revisionId, r.paperId, r.rating, r.reviewComment, u.name, u.surname 
            FROM EntityReview r 
            JOIN EntityUser u ON r.reviewerId = u.userId 
            JOIN EntityPaper p ON r.paperId = p.paperId 
            WHERE p.conferenceId = ?
        """;
            try (PreparedStatement stmt = connection.prepareStatement(sqlReviews)) {
                stmt.setInt(1, conferenceId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    builder.append(String.format("  - Paper %d, Reviewer: %s %s, Rating: %d, Comment: %s\n",
                            rs.getInt("paperId"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getInt("rating"),
                            rs.getString("reviewComment")));
                }
            }

            // Ritorna lo stream testuale
            return new ByteArrayInputStream(builder.toString().getBytes(StandardCharsets.UTF_8));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Get detailed information about a reviewed paper
    public static Map<String, Object> getReviewedPaper(int paperId) {
        Map<String, Object> paperInfo = new HashMap<>();

        String paperQuery = """
            SELECT p.title, u.name AS authorName, u.surname AS authorSurname
            FROM EntityPaper p
            JOIN EntityUser u ON p.authorId = u.userId
            WHERE p.paperId = ?
            """;

        String reviewQuery = """
            SELECT r.reviewComment, r.rating, r.privateComment, u.name AS reviewerName, u.surname AS reviewerSurname
            FROM EntityReview r
            JOIN EntityUser u ON r.reviewerId = u.userId
            WHERE r.paperId = ?
            """;

        try (
                PreparedStatement paperStmt = connection.prepareStatement(paperQuery);
                PreparedStatement reviewStmt = connection.prepareStatement(reviewQuery)
        ) {
            // Get paper title and author
            paperStmt.setInt(1, paperId);
            try (ResultSet rs = paperStmt.executeQuery()) {
                if (rs.next()) {
                    paperInfo.put("title", rs.getString("title"));
                    paperInfo.put("authorName", rs.getString("authorName"));
                    paperInfo.put("authorSurname", rs.getString("authorSurname"));
                } else {
                    return null; // Paper not found
                }
            }

            // Get associated reviews with reviewer names
            reviewStmt.setInt(1, paperId);
            try (ResultSet rs = reviewStmt.executeQuery()) {
                List<Map<String, Object>> reviewList = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> review = new HashMap<>();
                    review.put("rating", rs.getInt("rating"));
                    review.put("reviewComment", rs.getString("reviewComment"));
                    review.put("privateComment", rs.getString("privateComment"));
                    review.put("reviewerName", rs.getString("reviewerName"));
                    review.put("reviewerSurname", rs.getString("reviewerSurname"));
                    reviewList.add(review);
                }
                paperInfo.put("reviews", reviewList);
            }

            return paperInfo;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Map<String, Object> getPaperInReview(int paperId) {
        Map<String, Object> result = new HashMap<>();

        // Query per ottenere titolo del paper e dati autore
        String paperSql = """
        SELECT p.title, u.name, u.surname
        FROM EntityPaper p
        JOIN EntityUser u ON p.authorId = u.userId
        WHERE p.paperId = ?
    """;

        // Query per ottenere i revisori del paper
        String reviewersSql = """
        SELECT u.name, u.surname
        FROM EntityReview r
        JOIN EntityUser u ON r.reviewerId = u.userId
        WHERE r.paperId = ?
    """;

        try (
                PreparedStatement paperStmt = connection.prepareStatement(paperSql);
                PreparedStatement reviewersStmt = connection.prepareStatement(reviewersSql)
        ) {
            // Paper + autore
            paperStmt.setInt(1, paperId);
            ResultSet paperRs = paperStmt.executeQuery();

            if (paperRs.next()) {
                result.put("title", paperRs.getString("title"));
                result.put("authorName", paperRs.getString("name"));
                result.put("authorSurname", paperRs.getString("surname"));
            } else {
                return null; // Paper non trovato
            }

            // Revisori
            reviewersStmt.setInt(1, paperId);
            ResultSet revRs = reviewersStmt.executeQuery();

            List<String> reviewers = new ArrayList<>();
            while (revRs.next()) {
                String reviewer = revRs.getString("name") + " " + revRs.getString("surname");
                reviewers.add(reviewer);
            }

            result.put("reviewers", reviewers);

            return result;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Map<String, String> getPaperToBeReviewed(int paperId) {
        String sql = """
            SELECT p.title, u.name, u.surname
            FROM EntityPaper p, UserConferenceRole r, EntityUser u
            where p.conferenceId = r.conferenceId AND r.userId = u.userId AND r.role='Author' AND p.paperId = ?
        """;

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, paperId);
            ResultSet resultSet = statement.executeQuery();

            Map<String, String> paperInfo = new HashMap<>();

            while (resultSet.next()) {

                // Put data in the map
                paperInfo.put("title", resultSet.getString("title"));
                paperInfo.put("name", resultSet.getString("name"));
                paperInfo.put("surname", resultSet.getString("surname"));
            }

            return paperInfo;

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }


    public static boolean getConferenceEditor(int conferenceId) {
        String sql = """
                SELECT userId
                FROM UserConferenceRole
                WHERE conferenceId = ? AND role = 'Editor'""";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, conferenceId);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    public static ArrayList<EntityUser> getUserList(int conferenceId, String inviteType) {
        ArrayList<EntityUser> userList = new ArrayList<>();

        // Set user data
        String sql;

        switch (inviteType) {
            case "Reviewer":
                sql = """
                    SELECT u.userId, u.name, u.surname, u.email
                    FROM EntityUser u
                    WHERE u.userId NOT IN
                        (SELECT userId
                         FROM UserConferenceRole
                         WHERE conferenceId = ? AND role != 'Chair')
                """;
                break;
            case "Editor":
                sql = """
                    SELECT u.userId, u.name, u.surname, u.email
                    FROM EntityUser u
                    WHERE u.userId NOT IN
                        (SELECT userId
                         FROM UserConferenceRole
                         WHERE conferenceId = ?)
                """;
                break;
            default:
                return null;
        }


        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, conferenceId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                EntityUser user = new EntityUser();
                user.setUserId(resultSet.getInt("userId"));
                user.setName(resultSet.getString("name"));
                user.setSurname(resultSet.getString("surname"));
                user.setEmail(resultSet.getString("email"));
                userList.add(user);
            }

            return userList;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static boolean createPCMemberInvite(LinkedList<Integer> selectedUsers, int conferenceId, String title){
        String text = "INVITO a partecipare come MEMBRO DEL PC alla conferenza ";
        text = text.concat(title);

        String sql = """
            INSERT INTO EntityNotification (userId, type, conferenceId, text)
            VALUES (?, 'invitation PC', ?, ?)
        """;

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            while(!selectedUsers.isEmpty()){
                statement.setInt(1, selectedUsers.removeFirst());
                statement.setInt(2, conferenceId);
                statement.setString(3, text);
                statement.executeQuery();
            }

            return true;

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean createEditorInvite(int selectedUsers, int conferenceId, String title){
        String text= "INVITO a partecipare come EDITORE alla conferenza ";
        text = text.concat(title);

        String sql = """
            INSERT INTO EntityNotification (userId, type, conferenceId, text)
            VALUES (?, 'invitation EDITOR', ?, ?)
        """;

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, selectedUsers);
            statement.setInt(2, conferenceId);
            statement.setString(3, text);
            statement.executeQuery();

            return true;

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    public static ArrayList<EntityUser> getConferencePCMembers(int conferenceId){
        ArrayList<EntityUser> PCMemberList = new ArrayList<>();

        String sql = """
            SELECT u.userId, u.name, u.surname, u.email
            FROM EntityUser u, UserConferenceRole r
            WHERE u.userId = r.userId AND r.role='Reviewer' AND r.conferenceId = ?
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, conferenceId);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                EntityUser PCMember = new EntityUser();
                PCMember.setUserId(resultSet.getInt("userId"));
                PCMember.setName(resultSet.getString("name"));
                PCMember.setSurname(resultSet.getString("surname"));
                PCMember.setEmail(resultSet.getString("email"));
                PCMemberList.add(PCMember);
            }

            return PCMemberList;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Assign paper to reviewer
    public static boolean insertPaperReviewers(int paperId, LinkedList<Integer> reviewersId) {
        String sql = """
        INSERT INTO EntityReview (reviewerId, paperId, isSubReview, state, reviewComment, rating, privateComment)
        VALUES (?, ?, 0, 'new', '', 0, '')
    """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            while (!reviewersId.isEmpty()) {
                int reviewerId = reviewersId.removeFirst();
                statement.setInt(1, reviewerId);
                statement.setInt(2, paperId);
                statement.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // Insert sub review
    public static boolean insertPaperSubReviewer(int paperId, int reviewerId, int delegatorId) {
        String sql = """
        INSERT INTO EntityReview (reviewerId, paperId, isSubReview, state, reviewComment, rating, privateComment, delegator)
        VALUES (?, ?, 1, 'new', '', 0, '', ?)
    """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reviewerId);
            statement.setInt(2, paperId);
            statement.setInt(3, delegatorId);
            statement.executeUpdate();

            sql = """
                UPDATE EntityReview
                SET state = 'delegated'
                WHERE paperId = ? AND reviewerId = ?
                """;

            // Set the delegator review as sub review
            try (PreparedStatement updateStatement = connection.prepareStatement(sql)) {
                updateStatement.setInt(1, paperId);
                updateStatement.setInt(2, delegatorId);

                updateStatement.executeUpdate();

                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static List<Map<String, Object>> getAvailableConference(int userId) {
        String sql = """
            SELECT conferenceId, title
            FROM EntityConference
            WHERE conferenceId NOT IN (
                SELECT conferenceId
                FROM UserConferenceRole
                WHERE userId = ?
            ) AND scheduleDate > CURDATE()""";

        List<Map<String, Object>> conferenceList = new ArrayList<>();

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Map<String, Object> conference = new HashMap<>();
                // Put data in the map
                conference.put("conferenceId", resultSet.getInt("conferenceId"));
                conference.put("title", resultSet.getString("title"));
                conferenceList.add(conference);
            }

            return conferenceList;

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<Map<String, Object>> getParticipationConference(int userId) {
        String sql = """
            SELECT c.conferenceId, c.title
            FROM EntityConference c, UserConferenceRole r
            WHERE c.conferenceId = r.conferenceId AND r.role= 'Author' AND r.userId = ? AND c.scheduleDate > CURDATE()
        """;

        List<Map<String, Object>> conferenceList = new ArrayList<>();

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            HashMap<String, Object> conference = new HashMap<>();

            while (resultSet.next()) {

                // Put data in the map
                conference.put("conferenceId", resultSet.getInt("conferenceId"));
                conference.put("title", resultSet.getString("title"));

                conferenceList.add(conference);
            }

            return conferenceList;

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Partecipate as an author
    public static boolean insertAuthor(int userId, int conferenceId) {
            String sql = """
            INSERT INTO UserConferenceRole (userId, conferenceId, role)
            VALUES (?, ?, 'Author')
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, conferenceId);
            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }



    public static boolean uploadPaper(int conferenceId, String title, Date submissionDate, File file){
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String sql = """
            INSERT INTO EntityPaper (conferenceId, title, submitionDate, fileData)
            VALUES (?, ?, ?, ?)
        """;

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, conferenceId);
            statement.setString(2, title);
            statement.setDate(3, submissionDate);
            statement.setBlob(4, inputStream);
            statement.executeUpdate();

            return true;

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    public static boolean insertPaper(int userId, int conferenceId, String title, String description, String specializations, byte[] paperData) {
        // First, insert the paper into EntityPaper
        String sql = """
            INSERT INTO EntityPaper (conferenceId, authorId, title, description, fileData)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, conferenceId);
            statement.setInt(2, userId);
            statement.setString(3, title);
            statement.setString(4, description);
            statement.setBytes(5, paperData);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            // Get the generated paper ID
            ResultSet generatedKeys = statement.getGeneratedKeys();
            int paperId;
            if (generatedKeys.next()) {
                paperId = generatedKeys.getInt(1);
            } else {
                return false;
            }

            // Associate the paper with the selected specializations
            if (specializations != null && !specializations.isEmpty()) {
                String[] specializationIds = specializations.split(",");

                sql = """
                    INSERT INTO PaperSpecialization (paperId, specializationId)
                    VALUES (?, ?)
                """;

                try (PreparedStatement specStatement = connection.prepareStatement(sql)) {
                    for (String specId : specializationIds) {
                        specStatement.setInt(1, paperId);
                        specStatement.setInt(2, Integer.parseInt(specId));
                        specStatement.executeUpdate();
                    }
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Map<String, Object>> getUserPapers(int userId, int conferenceId){
        String sql = """
            SELECT p.paperId, p.title, p.isFinalVersion, p.accepted
            FROM EntityPaper p
            WHERE p.conferenceId = ? AND p.authorId = ?
        """;

        List<Map<String, Object>> paperList = new ArrayList<>();

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, conferenceId);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Map<String, Object> paper = new HashMap<>();
                // Put data in the map
                paper.put("paperId", resultSet.getInt("paperId"));
                paper.put("title", resultSet.getString("title"));
                paper.put("isFinalVersion", resultSet.getBoolean("isFinalVersion"));
                paper.put("accepted", resultSet.getInt("accepted"));

                paperList.add(paper);
            }

            return paperList;

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<EntityReview> getPaperInfo(int paperId){
        List<EntityReview> reviewList = new ArrayList<>();

        String sql = """
            SELECT p.title, r.state, r.reviewComment
            FROM EntityPaper p, EntityReview r
            WHERE r.paperId = ? AND r.paperId = p.paperId
        """;

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, paperId);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                //Create a new notification entity
                EntityReview entityReview = new EntityReview();

                entityReview.setPaperId(paperId);
                entityReview.setTitle(resultSet.getString("title"));
                entityReview.setState(resultSet.getString("state"));
                entityReview.setReviewComment(resultSet.getString("reviewComment"));

                reviewList.add(entityReview);
            }

            return reviewList;

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int getSubReviewId(String paperTitle, int delegatorId) {
        String sql = """
            SELECT r.revisionId
            FROM EntityReview r, EntityPaper p
            WHERE r.paperId = p.paperId AND p.title = ? AND r.delegator = ? AND r.isSubReview = 1
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, paperTitle);
            statement.setInt(2, delegatorId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("revisionId");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Map<String, Object> getDelegatedReviewInfo(int revisionId, int delegatorId) {
        String sql = """
                SELECT p.title, p.paperId, r.reviewerId, u.name, u.surname, r.rating, r.reviewComment, r.state
                FROM EntityReview r, EntityUser u, EntityPaper p
                WHERE r.reviewerId = u.userId AND r.paperId = p.paperId AND r.revisionId = ? AND r.delegator = ?
                """;

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, revisionId);
            statement.setInt(2, delegatorId);
            ResultSet resultSet = statement.executeQuery();
            Map<String, Object> review = new HashMap<>();

            if(resultSet.next()) {
                review.put("title", resultSet.getString("title"));
                review.put("paperId", resultSet.getInt("paperId"));
                review.put("reviewerId", resultSet.getInt("reviewerId"));
                review.put("reviewerName", resultSet.getString("name"));
                review.put("reviewerSurname", resultSet.getString("surname"));
                review.put("rating", resultSet.getInt("rating"));
                review.put("reviewComment", resultSet.getString("reviewComment"));
                review.put("state", resultSet.getString("state"));
            }

            return review;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static boolean removePaper(int  paperId){
        String sql = """
            DELETE FROM EntityPaper
            WHERE paperId = ?
        """;

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, paperId);
            statement.executeQuery();

            return true;

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean updatePaperFinalVersion(int  paperId, File file){
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String sql = """
            UPDATE EntityPaper
            SET isFinalVersion = 1, fileData= ?
            WHERE paperId= ?
        """;

        try(PreparedStatement statement = connection.prepareStatement(sql)){;
            statement.setBlob(1, inputStream);
            statement.setInt(2, paperId);
            statement.executeQuery();

            return true;

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return false;

    }


    // Get conference template
    public static InputStream getTemplate(int conferenceId) {
        String sql = """
        SELECT templateFile
        FROM EntityConference
        WHERE conferenceId = ?
    """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, conferenceId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Return file input stream
                return resultSet.getBinaryStream("templateFile");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Get user review list
    public static List<Map<String, Object>> getReviewList(int reviewerId, int conferenceId){
        String sql = """
            SELECT r.revisionId, p.title, r.state, p.paperId
            FROM EntityReview r
            JOIN EntityPaper p ON r.paperId = p.paperId
            WHERE r.reviewerId = ? AND p.conferenceId = ?

        """;

        List<Map<String, Object>> reviewList = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reviewerId);
            statement.setInt(2, conferenceId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Map<String, Object> review = new HashMap<>();
                review.put("reviewerId", reviewerId);
                review.put("revisionId", resultSet.getInt("revisionId"));
                review.put("title", resultSet.getString("title"));
                review.put("state", resultSet.getString("state"));
                review.put("paperId", resultSet.getInt("paperId"));

                reviewList.add(review);
            }

            return reviewList;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Get user review list count
    public static int getReviewListCount(int reviewerId, int conferenceId) {
        String sql = """
                SELECT COUNT(*) AS reviewCount
                FROM EntityReview r
                JOIN EntityPaper p ON p.paperId = r.paperId
                WHERE r.reviewerId = ? AND p.conferenceId = ?
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reviewerId);
            statement.setInt(2, conferenceId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("reviewCount");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }


    public static EntityReview getCompletedReview(int revisionId) {
        String sql = """
            SELECT p.paperId, p.title, r.reviewComment, r.rating, r.privateComment
            FROM EntityPaper p, EntityReview r
            where p.paperId = r.paperId AND r.revisionId = ?
        """;

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, revisionId);
            ResultSet resultSet = statement.executeQuery();

            EntityReview entityReview = new EntityReview();

            while (resultSet.next()) {
                entityReview.setRevisionId(revisionId);
                entityReview.setPaperId(resultSet.getInt("paperId"));
                entityReview.setTitle(resultSet.getString("title"));
                entityReview.setReviewComment(resultSet.getString("reviewComment"));
                entityReview.setRating(resultSet.getInt("rating"));
                entityReview.setPrivateComment(resultSet.getString("privateComment"));
            }

            return entityReview;

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static String getReviewTitle(int revisionId) {
        String sql = """
            SELECT p.title
            FROM EntityReview r, EntityPaper p
            where p.paperId=r.paperId AND r.revisionId = ?
        """;

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, revisionId);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.getString("title");

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }


    public static int getReviewId(int reviewerId, int paperId, boolean isSubReview) {
        String sql = """
                SELECT r.revisionId
                FROM EntityReview r
                WHERE r.reviewerId = ? AND r.paperId = ? AND r.isSubReview = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reviewerId);
            statement.setInt(2, paperId);
            statement.setBoolean(3, isSubReview);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("revisionId");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get paper file stream
    public static InputStream getPaperFile(int paperId) {
        String sql = """
        SELECT fileData
        FROM EntityPaper
        WHERE paperId = ?
    """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, paperId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Return file input stream
                return resultSet.getBinaryStream("fileData");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Update reviewer's paper review setting state 'done'
    public static boolean updateReview(int reviewerId, int paperId, String reviewComment, int rating, String privateComment) {
        String sql = """
            UPDATE EntityReview
            SET reviewComment = ?, rating = ?, privateComment = ?, state = 'done'
            WHERE reviewerId = ? AND paperId = ?
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reviewComment);
            statement.setInt(2, rating);
            statement.setString(3, privateComment);
            statement.setInt(4, reviewerId);
            statement.setInt(5, paperId);

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Accept a paper
    public static boolean acceptPaper(int paperId) {
        String sql = """
                UPDATE EntityPaper
                SET accepted = 1
                WHERE paperId = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, paperId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Reject a paper
    public static boolean rejectPaper(int paperId) {
        String sql = """
                UPDATE EntityPaper
                SET accepted = -1
                WHERE paperId = ?""";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, paperId);
            int rowAffected = statement.executeUpdate();
            return rowAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get reviews associated to the other reviewers
    public static Map<String, Object> getOthersReviewList(int paperId) {
        String sql = """
        SELECT p.title, u.name, u.surname, r.reviewComment
        FROM EntityReview r
        JOIN EntityPaper p ON p.paperId = r.paperId
        JOIN EntityUser u ON r.reviewerId = u.userId
        WHERE p.paperId = ?
    """;

        Map<String, Object> reviewData = new HashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, paperId);
            ResultSet resultSet = statement.executeQuery();

            List<Map<String, String>> reviews = new ArrayList<>();
            boolean titleSet = false;

            while (resultSet.next()) {
                if (!titleSet) {
                    reviewData.put("title", resultSet.getString("title"));
                    titleSet = true;
                }

                Map<String, String> singleReview = new HashMap<>();
                singleReview.put("name", resultSet.getString("name"));
                singleReview.put("surname", resultSet.getString("surname"));
                singleReview.put("reviewComment", resultSet.getString("reviewComment"));

                reviews.add(singleReview);
            }

            if (!reviews.isEmpty()) {
                reviewData.put("reviews", reviews);
                return reviewData;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static List<Map<String, Object>> getAvailableSubReviewer(int paperId) {
        List<Map<String, Object>> subReviewerList = new ArrayList<>();

        String sql = """
            SELECT u.userId, u.name, u.surname, u.email
            FROM EntityUser u
            WHERE u.userId NOT IN (
                SELECT r.reviewerId
                FROM EntityReview r
                WHERE r.paperId = ?
            )
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, paperId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Map<String, Object> subReviewer = new HashMap<>();
                subReviewer.put("userId", resultSet.getInt("userId"));
                subReviewer.put("name", resultSet.getString("name"));
                subReviewer.put("surname", resultSet.getString("surname"));
                subReviewer.put("email", resultSet.getString("email"));
                subReviewerList.add(subReviewer);
            }

            return subReviewerList;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Send a subreview invite
    public static boolean createSubReviewerInvite(int userId, int paperId, String paperTitle, int senderId, int conferenceId){
        String sql = """
                INSERT INTO EntityNotification (userId, type, paperId, conferenceId, text, sender)
                VALUES (?, 'invitation SUBREVIEWER', ?, ?, ?, ?)
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, paperId);
            statement.setInt(3, conferenceId);
            statement.setString(4, "Invito come sotto-revisore per il paper: " + paperTitle);
            statement.setInt(5, senderId);
            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Set rewis as on_going
    public static boolean setReviewStart(int revisionId) {
        // Imposta lo stato della revisione a 'on_going'
        String sql = """
            UPDATE EntityReview
            SET state = 'on_going'
            WHERE revisionId = ?
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, revisionId);
            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    public static boolean uploadTemplateFile(int conferenceId, File template){
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(template);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String sql = """
            UPDATE EntityConference
            SET templateFile=?
            WHERE conferenceId= ?
        """;

        try(PreparedStatement statement = connection.prepareStatement(sql)){;
            statement.setBlob(1, inputStream);
            statement.setInt(2, conferenceId);
            statement.executeQuery();

            return true;

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return false;

    }

    public static InputStream getArticle(int paperId) {
        String sql = """
            SELECT fileData
            FROM EntityPaper
            WHERE isFinalVersion=1 AND paperId = ?
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, paperId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Return file input stream
                return resultSet.getBinaryStream("fileData");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Get a conference chair with conferenceId
    public static Integer getConferenceChair(int conferenceId) {
        String sql = """
        SELECT userId
        FROM UserConferenceRole
        WHERE conferenceId = ? AND role = 'Chair'
    """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, conferenceId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("userId");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Get all the review with the paper not reviewed returns an array of userId
    public static ArrayList<Integer> getReviewerWithPaperNotReviewed(int conferenceId) {
        // Join reviewer with paper and conference
        // Select conference with valid expiration date
        // Select the paper with the 'under_review' state
        // Then select the reviewerId
        String sql = """
                SELECT r.reviewerId
                FROM EntityReview r, EntityPaper p, EntityConference c
                WHERE r.state = 'on_going' AND r.paperId = p.paperId AND p.conferenceId = c.conferenceId AND c.conferenceId = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, conferenceId);
            ResultSet resultSet = statement.executeQuery();

            ArrayList<Integer> reviewers = new ArrayList<>();
            while (resultSet.next()) {
                reviewers.add(resultSet.getInt("reviewerId"));
            }
            return reviewers;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Get conferences review submission deadlines
    public static Map<Integer, Date> getReviewDeadlines() {
        String sql = """
                SELECT conferenceId, reviewDeadline
                FROM EntityConference
                WHERE reviewDeadline > CURRENT_DATE()""";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            Map<Integer, Date> submissionDeadlines = new HashMap<>();

            while (resultSet.next()) {
                submissionDeadlines.put(resultSet.getInt("conferenceId"), resultSet.getDate("reviewDeadline"));
            }
            return submissionDeadlines;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Get conferences paper submission deadlines
    public static Map<Integer, Date> getSubmissionDeadlines() {
        String sql = """
                SELECT conferenceId, submissionDeadline
                FROM EntityConference
                WHERE submissionDeadline > CURRENT_DATE()""";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            Map<Integer, Date> submissionDeadlines = new HashMap<>();

            while (resultSet.next()) {
                submissionDeadlines.put(resultSet.getInt("conferenceId"), resultSet.getDate("submissionDeadline"));
            }
            return submissionDeadlines;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Get reviewer invite deadlines
    public static Map<Integer, Date> getInvitationDeadlines() {
        String sql = """
        SELECT conferenceId, invitationDeadline
        FROM EntityConference
        WHERE invitationDeadline > CURRENT_DATE()
    """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            Map<Integer, Date> deadlines = new HashMap<>();

            while (resultSet.next()) {
                deadlines.put(resultSet.getInt("conferenceId"), resultSet.getDate("invitationDeadline"));
            }
            return deadlines;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Get final version deadlines
    public static Map<Integer, Date> getFinalVersionDeadlines() {
        String sql = """
        SELECT conferenceId, finalVersionDeadline
        FROM EntityConference
        WHERE finalVersionDeadline > CURRENT_DATE()
    """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            Map<Integer, Date> deadlines = new HashMap<>();

            while (resultSet.next()) {
                deadlines.put(resultSet.getInt("conferenceId"), resultSet.getDate("finalVersionDeadline"));
            }

            return deadlines;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Map<Integer, Date> paperAssignmentDeadlines() {
        String sql = """
        SELECT conferenceId, paperAssignmentDeadline
        FROM EntityConference
        WHERE paperAssignmentDeadline
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            Map<Integer, Date> deadlines = new HashMap<>();

            while (resultSet.next()) {
                deadlines.put(resultSet.getInt("conferenceId"), resultSet.getDate("paperAssignmentDeadline"));
            }

            return deadlines;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static ArrayList<EntityPaper> getUnassignedPaper(int conferenceId){
        ArrayList<EntityPaper> paperList = new ArrayList<>();

        String sql = """
                SELECT
                    p.paperId,
                    p.title,
                    u.name,
                    u.surname,
                    COUNT(r.revisionId) AS numReviews,
                    c.reviewerForPaper
                FROM EntityPaper p
                         JOIN EntityUser u ON p.authorId = u.userId
                         JOIN EntityConference c ON p.conferenceId = c.conferenceId
                         LEFT JOIN EntityReview r ON p.paperId = r.paperId
                         WHERE c.conferenceId = ?
                GROUP BY p.paperId, p.title, u.name, u.surname, c.reviewerForPaper
                HAVING COUNT(r.revisionId) < c.reviewerForPaper;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, conferenceId);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                EntityPaper paper = new EntityPaper();
                paper.setPaperId(resultSet.getInt("paperId"));
                paper.setTitle(resultSet.getString("title"));
                paper.setAuthorName(resultSet.getString("name"));
                paper.setAuthorSurname(resultSet.getString("surname"));
                paperList.add(paper);
            }

            return paperList;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static boolean updateUserInterest(int userId, int paperId, int preference, boolean conflict) {
        String sqlPref = """
            SELECT pr.preferenceId
            FROM EntityPaper p, EntityReview r, EntityPreference pr
            WHERE p.paperId = r.paperId AND r.reviewerId = pr.userId AND p.paperId = ?;
        """;
        try (PreparedStatement statement = connection.prepareStatement(sqlPref)) {
            statement.setInt(1, paperId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String sql = """
                    UPDATE EntityPreference
                    SET isConflict=?, preference=?
                    WHERE userId=? AND paperId=?
                """;

                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setBoolean(1, conflict);
                    stmt.setInt(2, preference);
                    stmt.setInt(3, userId);
                    stmt.setInt(4, paperId);

                    stmt.executeUpdate();
                    return true;

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return false;
            }else{
                String sql = """
                    INSERT INTO entitypreference (userId, paperId, isConflict, preference)
                    VALUES (?, ?, ?, ?)
                """;

                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, userId);
                    stmt.setInt(2, paperId);
                    stmt.setBoolean(3, conflict);
                    stmt.setInt(4, preference);

                    stmt.executeUpdate();
                    return true;

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    // Get author with no submissions
    public static ArrayList<Integer> getAuthorsWithNoSubmissions(int conferenceId) {
        String sql = """
        SELECT ucr.userId
        FROM UserConferenceRole ucr
        WHERE ucr.conferenceId = ?
          AND ucr.role = 'Author'
          AND ucr.userId NOT IN (
              SELECT DISTINCT authorId
              FROM EntityPaper
              WHERE conferenceId = ?
          )
    """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, conferenceId);
            statement.setInt(2, conferenceId);
            ResultSet resultSet = statement.executeQuery();

            ArrayList<Integer> authorIds = new ArrayList<>();
            while (resultSet.next()) {
                authorIds.add(resultSet.getInt("userId"));
            }
            return authorIds;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Select users that have to submit the final version
    public static ArrayList<Integer> getAuthorsWithAcceptedPapers(int conferenceId) {
        String sql = """
        SELECT DISTINCT p.authorId
        FROM EntityPaper p, EntityReview r
        WHERE p.conferenceId = ? AND r.state = 'reviewed' AND p.isFinalVersion = FALSE AND r.paperId = p.paperId
    """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, conferenceId);
            ResultSet resultSet = statement.executeQuery();

            ArrayList<Integer> authorIds = new ArrayList<>();
            while (resultSet.next()) {
                authorIds.add(resultSet.getInt("userId"));
            }

            return authorIds;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
    ALGORITMI DI ASSEGNAZIONE
     */

    public static List<String> getPaperSpecializations(int paperId) {
        List<String> specs = new ArrayList<>();
        String query = """
        SELECT s.name
        FROM PaperSpecialization ps
        JOIN EntitySpecialization s ON ps.specializationId = s.specializationId
        WHERE ps.paperId = ?
    """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, paperId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                specs.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return specs;
    }

    public static List<String> getReviewerSpecializations(int userId) {
        List<String> specs = new ArrayList<>();
        String query = """
        SELECT s.name
        FROM UserSpecialization us
        JOIN EntitySpecialization s ON us.specializationId = s.specializationId
        WHERE us.userId = ?
    """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                specs.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return specs;
    }

    public static Integer getReviewerPreference(int userId, int paperId) {
        String query = """
        SELECT preference
        FROM EntityPreference
        WHERE userId = ? AND paperId = ?
    """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, paperId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("preference");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Boolean getReviewerConflict(int userId, int paperId) {
        String query = """
        SELECT isConflict
        FROM EntityPreference
        WHERE userId = ? AND paperId = ?
    """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, paperId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getBoolean("isConflict");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Gets the conference ID for a given paper ID
     * @param paperId The paper ID
     * @return The conference ID, or -1 if not found
     */
    public static int getConferenceIdFromPaper(int paperId) {
        String sql = "SELECT conferenceId FROM EntityPaper WHERE paperId = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, paperId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("conferenceId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 if not found or error occurred
    }
}
