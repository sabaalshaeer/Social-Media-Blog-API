package Controller;

import java.sql.SQLException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Service.AccountService;
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

    public SocialMediaController() {
        // this creates the Javalin app
        this.app = Javalin.create();
        this.accountService = new AccountService();
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in
     * the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * 
     * @return a Javalin app object which defines the behavior of the Javalin
     *         controller.
     */
    // public Javalin startAPI() {
    // //Javalin app = Javalin.create();
    // app.post("/register", this::postAccountHandler);

    // return app;
    // }

    public Javalin startAPI() {
        app.post("/register", ctx -> {

            // We use this to convert body into a account object
            ObjectMapper om = new ObjectMapper();
            Account account = om.readValue(ctx.body(), Account.class);
            // Call account service method to insert account
            Account addNewAccount = accountService.addAccount(account);
            if (addNewAccount == null) {
                ctx.status(400); // Set the response status to 400 if registration is not successful
            } else {
                // using the ObjectMapper instance mapper to convert the Java object
                // addedAccount into a JSON string using the writeValueAsString() method.
                // Then, it's setting the response body of the HTTP response to this JSON string
                // using ctx.json()
                ctx.json(om.writeValueAsString(addNewAccount));
            }
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
     * @throws SQLException
     */

    // private void postAccountHandler(Context ctx) throws JsonMappingException,
    // JsonProcessingException {
    // // We use this to convert body into a account object
    // ObjectMapper om = new ObjectMapper();
    // Account account = om.readValue(ctx.body(), Account.class);
    // // Call account service method to insert account
    // Account addNewAccount = accountService.addAccount(account);
    // if(addNewAccount==null){
    // ctx.status(400); // Set the response status to 400 if registration is not
    // successful
    // }else{
    // //using the ObjectMapper instance mapper to convert the Java object
    // addedAccount into a JSON string using the writeValueAsString() method.
    // //Then, it's setting the response body of the HTTP response to this JSON
    // string using ctx.json()
    // ctx.json(om.writeValueAsString(addNewAccount));
    // }
    // }

}