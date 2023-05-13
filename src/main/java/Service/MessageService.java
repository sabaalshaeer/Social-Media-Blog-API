package Service;


import java.util.List;

import DAO.MessageDAO;
import Model.Message;

public class MessageService {
    public MessageDAO messageDAO;

    public MessageService(){
        this.messageDAO = new MessageDAO();
    }

    public Message addMessage(Message message) {
        Message createdMessage = MessageDAO.createMessage(message);
        if (createdMessage == null) {
            return null;
        } else {
            return createdMessage;
        }
    }

    public List<Message> getAllMessages(){
        return MessageDAO.getAllMessages();
    }

    public Message getMessageById(int id){
        return MessageDAO.getMessagesById(id);
    }

    public Message deleteMessageById(int id){
       return MessageDAO.deleteMessageById(id);
      
    }

    public Message updateMessageById(int id, Message message){
        return MessageDAO.updateMessage(id, message);
    }

    public List<Message> getAllMessagesForUser(int accountId){
        return MessageDAO.getAllMessagesForUser(accountId);

    }

    //public Message updateMessageById(int id, Message message){

       // return messageDAO.updateMessage(id, message);
        // Message existingMessage = messageDAO.updateMessage(id, message);
        // if (existingMessage == null) {
        //     try {
        //         throw new IOException("no message in the database");
        //     } catch (IOException e) {
        //         e.printStackTrace();
        //     } // Flight ID does not exist in database
        // }
        // // Update the flight data
        // existingMessage.setMessage_text(message.getMessage_text());
        // messageDAO.updateMessage(id, existingMessage);
        
        // return existingMessage; // Return the updated flight
       
     //}

    //  public Message updateMessageById(int id, Message message) {
    //     Message existingMessage;
    // if (messageDAO.getMessagesById(id) == null) {
    //     try {
    //         throw new IOException("Message with ID " + id + " does not exist");
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }
    
    // existingMessage = messageDAO.getMessagesById(id);
    // existingMessage.setMessage_text(message.getMessage_text());
    // existingMessage.setTime_posted_epoch(message.getTime_posted_epoch());
    
    // messageDAO.updateMessage(id,existingMessage);
    // return existingMessage;
    // }
 
    
    
}
