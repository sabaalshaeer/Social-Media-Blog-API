package Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import DAO.MessageDAO;
import Model.Message;

public class MessageService {
    public MessageDAO messageDAO;

    public MessageService(){
        this.messageDAO = new MessageDAO();
    }

    public Message addMessage(Message message) {
        Message createdMessage = messageDAO.createMessage(message);
        if (createdMessage == null) {
            return null;
        } else {
            return createdMessage;
        }
    }

    public List<Message> getAllMessages(){
        return messageDAO.getAllMessages();
    }

    public Message getMessageById(int id){
        return messageDAO.getMessagesById(id);
    }

    public Message deleteMessageById(int id){
       return messageDAO.deleteMessageById(id);
      
    }

    
    
}
