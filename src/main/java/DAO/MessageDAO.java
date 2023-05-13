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
    static Connection connection = ConnectionUtil.getConnection();

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
    public static Message createMessage(Message message) {
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
    public static boolean checkMessageTextInDb(String messageText) {
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

    public static List<Message> getAllMessages() {
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
    public static Message getMessagesById(int id) {

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

    /*
     * Delete a Message Given Message Id
     * As a User, I should be able to submit a DELETE request on the endpoint DELETE
     * localhost:8080/messages/{message_id}.
     * The deletion of an existing message should remove an existing message from
     * the database. If the message existed, the response body should contain the
     * now-deleted message.
     * The response status should be 200, which is the default.
     * If the message did not exist, the response status should be 200, but the
     * response body should be empty.
     * This is because the DELETE verb is intended to be idempotent, ie, multiple
     * calls to the DELETE endpoint should respond with the same type of response.
     */
    public static Message deleteMessageById(int id) {
        try {
            // Create a new prepared statement that deletes a message with the given ID
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM message WHERE message_id = ?;");
            // Set the value of the parameter in the SQL statement to the given ID
            ps.setInt(1, id);
            // Execute the SQL statement and get the result set
            ResultSet rs = ps.executeQuery();
            // If a message with the given ID exists in the "message" table
            if (rs.next()) {
                // Get the message details from the result set
                int postedBy = rs.getInt("posted_by");
                String messageText = rs.getString("message_text");
                long timePostedEpoch = rs.getLong("time_posted_epoch");
                // Prepare a SQL statement to delete the message with the given ID from the
                // "message" table
                ps = connection.prepareStatement("DELETE FROM message WHERE message_id = ?;");
                // Set the value of the parameter in the SQL statement to the given ID
                ps.setInt(1, id);
                // Execute the SQL statement and get the number of rows affected (should be 1 if
                // the message was deleted)
                int rowsAffected = ps.executeUpdate();
                // If the message was successfully deleted
                if (rowsAffected > 0) {
                    // Create a new Message object with the details of the deleted message and
                    // return it
                    return new Message(id, postedBy, messageText, timePostedEpoch);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        // Return null if no message was deleted
        return null;
    }

    /*
     * Update Message Given Message Id
     * As a user, I should be able to submit a PATCH request on the endpoint PATCH
     * localhost:8080/messages/{message_id}.
     * The request body should contain a new message_text values to replace the
     * message identified by message_id.
     * The request body can not be guaranteed to contain any other information.
     * The update of a message should be successful if and only if the message id
     * already exists and the new message_text is not blank and is not over 255
     * characters.
     * If the update is successful, the response body should contain the full
     * updated message (including message_id, posted_by, message_text, and
     * time_posted_epoch),
     * and the response status should be 200, which is the default. The message
     * existing on the database should have the updated message_text.
     * If the update of the message is not successful for any reason, the response
     * status should be 400. (Client error)
     */
    public static Message updateMessage(int id, Message message) {
        try {
            // Check if the message with the given ID exists in the database
            if (!checkMessageInDbById(id)) {
                throw new IOException("Message with ID " + id + " does not exist");

            }
            // Check if the new message meets the required criteria
            if (message.getMessage_text().isEmpty() || message.getMessage_text().length() > 255) {
                throw new IOException("Message text is empty or length greater than 255");
            }

            // Write SQL logic here
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, message.getMessage_text());
            // preparedStatement.setLong(2, message.getTime_posted_epoch());
            preparedStatement.setInt(2, id);

            // Execute the SQL statement and get the number of rows affected (should be 1 if
            // the message was updated)
            int rowsAffected = preparedStatement.executeUpdate();

            // If the message was successfully updated
            if (rowsAffected > 0) {
                // Get the updated message from the database
                Message updatedMessage = getMessagesById(id);
                return updatedMessage;

            } else {
                return null;
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Check if message with giving id exists in the Database
    public static boolean checkMessageInDbById(int id) {
        try {
            String sql = "SELECT COUNT(*) FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            return count > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * Get All Messages From User Given Account Id
     * As a user, I should be able to submit a GET request on the endpoint GET
     * localhost:8080/accounts/{account_id}/messages.
     * The response body should contain a JSON representation of a list containing
     * all messages posted by a particular user,
     * which is retrieved from the database. It is expected for the list to simply
     * be empty if there are no messages.
     * The response status should always be 200, which is the default
     */

     public static List<Message> getAllMessagesForUser(int accountId) {
        List<Message> listOfMessage = new ArrayList<>();
    
        // Get the messages by account ID from the database
        String sql = "SELECT * FROM messages WHERE posted_by = ?";
        try (
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setInt(1, accountId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Message message = new Message();
                message.setMessage_id(resultSet.getInt("message_id"));
                message.setPosted_by(resultSet.getInt("posted_by"));
                message.setMessage_text(resultSet.getString("message_text"));
                message.setTime_posted_epoch(resultSet.getLong("time_posted_epoch"));
                listOfMessage.add(message);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    
        // Check if there are any messages
        if (listOfMessage.isEmpty()) {
            // No messages found
            return Collections.emptyList();
        } else {
            // There are messages
            return listOfMessage;
        }
    }
    // public static List<Message> getAllMessagesForUser(int account_id) {
    //     try {
    //         // Check if there are any messages for the specified account
    //         String sql = "SELECT COUNT(*) FROM messages WHERE posted_by = ?";
    //         PreparedStatement preparedStatement = connection.prepareStatement(sql);
    //         preparedStatement.setInt(1, account_id);
    //         ResultSet resultSet = preparedStatement.executeQuery();
    //         resultSet.next();
    //         int count = resultSet.getInt(1);

    //         // If there are no messages, return an empty list
    //         if (count == 0) {
    //             return Collections.emptyList();
    //         }
    //         // Get the messages by account ID from the database
    //         sql = "SELECT * FROM messages WHERE posted_by = ?";
    //         preparedStatement = connection.prepareStatement(sql);
    //         preparedStatement.setInt(1, account_id);
    //         resultSet = preparedStatement.executeQuery();
    //         List<Message> messagesList = new ArrayList<>();
    //         while (resultSet.next()) {
    //             Message message = new Message();
    //             resultSet.getInt("message_id");
    //             resultSet.getInt("posted_by");
    //             resultSet.getString("message_text");
    //             resultSet.getLong("time_posted_epoch");
    //             messagesList.add(message);
    //         }

    //         System.out.println("Messages for account " + account_id + ": " + messagesList);
    //         System.out.println();

    //         return messagesList;
    //     } catch (SQLException e) {
    //         System.out.println(e.getMessage());
    //     }
    //     return Collections.emptyList();
    // }

    // public static boolean checkIfListOfMessagesExistFOrAccount(int account_id){
    // try{
    // String sql = "SELECT COUNT(*) AS message_count FROM messages WHERE account_id
    // = ?";
    // PreparedStatement preparedStatement = connection.prepareStatement(sql);
    // preparedStatement.setInt(1, account_id);
    // ResultSet rs = preparedStatement.executeQuery();
    // rs.next();
    // int count = rs.getInt(1);
    // return count > 0;
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // return false;
    // }

    public Message updateMessage(Message messageToUpdate) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            // Define SQL update statement
            String sql = "UPDATE message SET posted_by = ?, message_text = ?, time_posted_epoch = ? WHERE message_id = ?";
    
            // Create PreparedStatement object
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
    
            // Set PreparedStatement parameters
            preparedStatement.setInt(1, messageToUpdate.getPosted_by());
            preparedStatement.setString(2, messageToUpdate.getMessage_text());
            preparedStatement.setLong(3, messageToUpdate.getTime_posted_epoch());
            preparedStatement.setInt(4, messageToUpdate.getMessage_id());
    
            // Execute update statement
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Failed to update the Message record.");
            }
    
            // Close resources
            preparedStatement.close();
            connection.close();
    
            // Return updated Message object
            return messageToUpdate;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    

}
