OAuth-Spring-Workshop
=====================

This is the source code of the OAuth 2.0 workshop. It showcases the use of Spring in creating authorization with OAuth 2.0.
It is composed of 2 modules:
* Authorization Server - Issues and checks tokens
* Resource Server - Contains the protected resource

How to ru(i)n it
----------------
As in our day to day activities there are multiple ways to run the modules. In the following 2 ways are presented, one for the command line fans and one for the visual-oriented people.
### Maven (command line)
* Go to the root folder of the authorization server
* Open your favorite CLI
* Run `mvn spring-boot:run`
* Go to the root folder of the auto service
* Open anothe window/tab of your favorite CLI
* Run `mvn spring-boot:run`
### IDE
* Open your favorite IDE
* Import the project
* Run the `AuthorizationServer` class (it's the only one with a static main method 😉)
* Run the `AutoResourceServer` class (it's the only one with a static main method 😉)

Great job! You know have an authorization server and a resource server running.
Now let's get a token from the authorization server to use it to access the resource server.

### Obtain token from Authorization Server
The token is obtained from the Authorization Server via a HTTP request. In the following you ahave a descrption on how to create the request:

##### URL
The endpoint for getting a token is `/oauth/token` and if you used the default settings the host is `localhost` and the port `8088`. To this URL you need to add an URL parameter called `grant_type` with the value `client_credentials`. You must use the *POST* HTTP method.
e.g. `POST http://localhost:8088/oauth/token?grant_type=client_credentials`
##### Header
In order for the client for whom the token is issued spring uses Basic Authorization. This means you need to include in the request a header called `Authorization`. It's value is composed by appending to the value `Basic ` (the word Basic is followed by a space) the Base64 encoding of the string obtained by concatenating the client id and client secert using the ':' separator. Sounds complicated right?
Let's see an example: The client id is 'client' the client secret is 'so-secret'. We encode in Base64 the string 'client:so-secret' and obtain the following value `Y2xpZW50OnNvLXNlY3JldA==` (tip: use an online Base64 encoder/decoder for testing purposes). Having this the value of the `Authorization` header will be: `Basic Y2xpZW50OnNvLXNlY3JldA==`.

If the request is correct and the client credentials are alright a success response is returned by the authorization server that looks like this:
```json
{
    "access_token": "37057264-d60f-4e50-937a-9f7809c2f5da",
    "token_type": "bearer",
    "expires_in": 43199,
    "scope": "auto-read auto-write"
}
```
We got the token and some information related to it.

### Accessing the Protected Resource from the Resource Server
If you look in the `AutoController` you'll see an endpoint that is protected with the `@PreAuthorize` annotation that says you need to have the scope 'auto-read' in order to access it. The token obtained above has two scopes and one of them is the one that we need to access this endpoint. Now let's use the token from the previous step (37057264-d60f-4e50-937a-9f7809c2f5da)!

Token and security aside you would access that endpoint by creating a *GET* request to `http://localhost:9099/auto`. This will result in a 401 as the endpoint is secured. In order to add the token to the request you need an URL parameter with the name `access_token` with the value of the token. e.g. `http://localhost:9099/auto?access_token=37057264-d60f-4e50-937a-9f7809c2f5da`

TA-DA! You did it!

If you got here and you still want to play around you could try and get a token with limited scopes (for username try read-only-client instead of client when calling the token endpoint) and try to POST to the autos endpoint with the new token and see what happens.
P.S. The Content-Type header needs to be set to application/json, otherwise the resource server returns 415 Unsupported Media Type. 

⚠
The Resource Server in our case it is not a client (it does not request tokens to use to communicate with other resource servers) but due to the Spring Magic that happens under the bonnet we get a 401 if we do not configure the resource server as a client as well. This is done by adding the `@EnableOAuth2Client` annotation to our `OauthConfig` class and by specifying a client id and secret for the auto service. The client id and secret are specified in the property file.
e.g.
```yaml
security:
  oauth2:
    client:
       client-id: auto-server
       client-secret: secret!
```
If you look in the authorization server you'll see that this client does not have any scopes attached to it nor an authorized grant type. It is there just to fix the Spring Magic.