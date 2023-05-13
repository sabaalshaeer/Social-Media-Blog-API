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

    public List<Message> getAllMessagesForUser(int account_id){
        return MessageDAO.getAllMessagesForUser(account_id);

    }
    
    
}
