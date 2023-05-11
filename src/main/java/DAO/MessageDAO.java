package DAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import Model.Message;
import Util.ConnectionUtil;

public class MessageDAO {
    // Get a connection object from ConnectionUtil
    Connection connection = ConnectionUtil.getConnection();

    /*
     * Create New Message
     * As a user, I should be able to submit a new post on the endpoint POST
     * localhost:8080/messages. The request body will contain a JSON representation
     * of a message, which should be persisted to the database, but will not contain
     * a message_id.
     * The creation of the message will be successful if and only if the
     * message_text is not blank, is under 255 characters, and posted_by refers to a
     * real, existing user. If successful, the response body should contain a JSON
     * of the message, including its message_id.
     * The response status should be 200, which is the default. The new message
     * should be persisted to the database.
     * If the creation of the message is not successful, the response status should
     * be 400. (Client error)
     */
    // post -- This method will handle the creating new message in the database
    public Message createMessage(Message message) {
        try {
            // Check if the message meets the required criteria
            if (message == null || message.getMessage_text().isBlank() || message.getMessage_text().length() > 254) {
                throw new IOException("message is null or blank, or length greater than 254");
            }

            // Check if the message already exists in the database
            if (checkMessageTextInDb(message.getMessage_text())) {
                throw new IOException("MESSAGE already exists");
            }

            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) Values(?,?,?)";

            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());

            preparedStatement.executeUpdate();
            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
            if (pkeyResultSet.next()) {
                int generated_message_id = (int) pkeyResultSet.getLong(1);
                return new Message(generated_message_id, message.getPosted_by(), message.getMessage_text(),
                        message.getTime_posted_epoch());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // check if the message already in the database
    public boolean checkMessageTextInDb(String messageText) {
        try {
            String sql = "SELECT COUNT(*) FROM message WHERE message_text = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, messageText);

            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            return count > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    /*
     * Get All Messages
     * As a user, I should be able to submit a GET request on the endpoint GET
     * localhost:8080/messages.
     * The response body should contain a JSON representation of a list containing
     * all messages retrieved from the database.
     * It is expected for the list to simply be empty if there are no messages. The
     * response status should always be 200, which is the default.
     */

    public List<Message> getAllMessages() {
        try (
                PreparedStatement ps = connection.prepareStatement("select * from message;");
                // because we want to retrieve data meaningfully in java, we have to expect data
                // in the form of a 'resultset'
                // we also have to use executeQuery instead of executeUpdate, because
                // executeQuery is expecting a resultSet
                ResultSet rs = ps.executeQuery()) {
            List<Message> allMessage = new ArrayList<>();
            // we have to loop through the entire resultset for every item it contains
            while (rs.next()) {
                // we have to extract the DB column of each row into a meaningful java object
                Message message = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch"));
                // Add the newly created Account object to the list
                allMessage.add(message);
            }
            return allMessage;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        // in the event that we don't get to return allMessage because a SQLException
        // was thrown, just return null
        return null;
    }

    /*
     * Get One Message Given Message Id
     * As a user, I should be able to submit a GET request on the endpoint GET
     * localhost:8080/messages/{message_id}.
     * The response body should contain a JSON representation of the message
     * identified by the message_id. It is expected
     * for the response body to simply be empty if there is no such message. The
     * response status should always be 200, which is the default.
     */
    public Message getMessagesById(int id) {

        try {
            PreparedStatement ps = connection.prepareStatement("select * from message where message_id = ?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // public Message getMessagesById(int id) {
    // Message message = null;

    // try {
    // PreparedStatement ps = connection.prepareStatement("SELECT * FROM message
    // WHERE message_id = ?");
    // ps.setInt(1, id);

    // ResultSet rs = ps.executeQuery();

    // if (rs.next()) {
    // message = new Message(
    // rs.getInt("message_id"),
    // rs.getInt("posted_by"),
    // rs.getString("message_text"),
    // rs.getLong("time_posted_epoch")
    // );
    // }
    // } catch (SQLException e) {
    // System.out.println(e.getMessage());
    // }

    // return message;
    // }

}
