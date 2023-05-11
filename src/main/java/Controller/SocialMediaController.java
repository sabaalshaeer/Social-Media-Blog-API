package Controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your
 * controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a
 * controller may be built.
 */
public class SocialMediaController {
   

    Javalin app;

    AccountService accountService;
    MessageService messageService;

    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();

    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in
     * the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * 
     * @return a Javalin app object which defines the behavior of the Javalin
     *         controller.
     */
    public Javalin startAPI() {
        // this creates the Javalin app
        this.app = Javalin.create();
        // endpoint that register new account
        app.post("/register", this::postAccountHandler);
        // endpoint for login
        app.post("/login", this::loginAccountHandler);
        // endpoint that create new message
        app.post("/messages", this::postMessageHandler);
        
        // endpoint that returns all message ( other way to use endpoint)
        app.get("/messages", ctx -> {
            // This uses the message service class to get all the messages. Then turns to JSON and responds to request
        List<Message> allMessages = messageService.getAllMessages();
        ctx.json(allMessages);
        });

        return app;
    }

    
    /**
     * This is an example handler for an example endpoint.
     * 
     * @param context The Javalin Context object manages information about both the
     *                HTTP request and response.
     * @throws JsonProcessingException
     * @throws JsonMappingException
     * @throws IOException
     * @throws SQLException
     */

    private void postAccountHandler(Context ctx) throws JsonMappingException, JsonProcessingException  {
        //ObjectMapper is used to convert the HTTP request body (which is in JSON format) into an instance of the Account class, 
        //which represents the user's account information. This is done using the readValue() method of the ObjectMapper class.
        ObjectMapper om = new ObjectMapper();
        Account account = om.readValue(ctx.body(), Account.class);
        // Call account service method to add account
        Account addNewAccount = accountService.addAccount(account);
        if (addNewAccount == null) {
            ctx.status(400); // Set the response status to 400 if registration is not successful
        } else {
            // addedAccount into a JSON string using the writeValueAsString() method.
            // Then, it's setting the response body of the HTTP response to this JSON
            // string using ctx.json()
            ctx.json(om.writeValueAsString(addNewAccount));
        }
    }

    //Handles a login request 
    private void loginAccountHandler(Context ctx) throws JsonMappingException, JsonProcessingException, SQLException   {
        // ObjectMapper provides functionality for converting Java objects to JSON format and vice versa.
        ObjectMapper om = new ObjectMapper();
        // Convert the request body to an Account object
        Account account = om.readValue(ctx.body(), Account.class);
        // Extract the username and password from the Account object
        String username = account.getUsername();
        String password = account.getPassword();
        // Verify the login credentials by calling the accountService method
        Account login = accountService.getAccountByUsernameAndPassword(username, password);
        // If login is null, the credentials were invalid, so respond with a 401 status code
        if (login == null) {
            ctx.status(401); 
        } else {
            ctx.json(om.writeValueAsString(login));
        }
    }
    //Handler for adding new message
    private void postMessageHandler(Context ctx) throws JsonProcessingException  {
        // ObjectMapper provides functionality for converting Java objects to JSON format and vice versa.
        ObjectMapper om = new ObjectMapper();
        // Convert the request body to an Message object
        Message message = om.readValue(ctx.body(), Message.class);
    
        // Verify the login credentials by calling the accountService method
        Message addMessage = messageService.addMessage(message);
        // If login is null, the credentials were invalid, so respond with a 401 status code
        if (addMessage == null) {
            ctx.status(400); 
        } else {
            ctx.json(om.writeValueAsString(addMessage));
        }
    }

    

    


   


}