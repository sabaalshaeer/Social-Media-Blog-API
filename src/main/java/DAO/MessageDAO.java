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
    // Initialize an empty list to store all Message objects retrieved from the
    // database
    List<Message> messageList = new ArrayList<>();

    // post -- This method will handle the creating new message in the database
    public Message createMessage(Message message) {
        try {
            // Check if the message meets the required criteria
            if (message == null || message.getMessage_text().isBlank() || message.getMessage_text().length() > 254) {
                throw new IOException ("message is null or blank, or length greater than 254");
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
    
    public List<Message> getAllMessages(){
        try(
            PreparedStatement ps = connection.prepareStatement("select * from message;");
//            because we want to retrieve data meaningfully in java, we have to expect data in the form of a 'resultset'
//            we also have to use executeQuery instead of executeUpdate, because executeQuery is expecting a resultSet
            ResultSet rs = ps.executeQuery()){
            List<Message> allMessage = new ArrayList<>();
//            we have to loop through the entire resultset for every item it contains
            while(rs.next()){
//                we have to extract the DB column of each row into a meaningful java object
                Message message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                     rs.getString("message_text"),
                     rs.getLong("time_posted_epoch"));
                    // Add the newly created Account object to the list
                     allMessage.add(message);
            }
            return allMessage;

        }catch(SQLException e){
            e.printStackTrace();
        }
//        in the event that we don't get to return allMessage because a SQLException was thrown, just return null
        return null;
    }
}
