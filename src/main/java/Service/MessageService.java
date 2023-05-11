package Service;

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

    
}
