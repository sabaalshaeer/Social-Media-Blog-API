Social Media Blog API (Java, Javalin, REST API, SQL):
Developed a Social Media Blog API using Java and Javalin.
Implemented RESTful endpoints to enable user registration and message creation.
User Registration:
Users can create a new account on the endpoint POST localhost:8080/register.
The registration is successful if the username is not blank, the password is at least 4 characters long, and an account with that username does not already exist.
If successful, the response contains a JSON representation of the account, including its account_id.
The response status is 200 OK, and the new account is persisted to the database.
If registration fails, the response status is 400 (Client error).
Create New Message:
Users can submit a new post on the endpoint POST localhost:8080/messages.
The request body contains a JSON representation of a message, which is persisted to the database.
The creation of the message is successful if the message_text is not blank, is under 255 characters, and posted_by refers to an existing user.
If successful, the response contains a JSON representation of the message, including its message_id.
The response status is 200, and the new message is persisted to the database.
If message creation fails, the response status is 400 (Client error).
This project demonstrates the implementation of a Social Media Blog API, allowing users to register and post messages while adhering to specific validation criteria.
