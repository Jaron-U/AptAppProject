import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Connection;

public class UserService extends MicroService {
    private static boolean connected = false;
    private static Jedis jedis;
    private static String registryAddr = "http://localhost:8080/pub";
    private static String baseURL = "http://localhost:10000";

    public static void main(String[] args) throws Exception {
        connect();
        // Create server and context
        HttpServer server = HttpServer.create(new InetSocketAddress(10000), 0);

        // Set up and publish login service
        HttpContext loginAPI = server.createContext("/login");
        boolean published1 = register(registryAddr, baseURL + "/login", 10000, ServiceInfoModel.SERVICE_USER_LOGIN);
        loginAPI.setHandler(UserService::handleLogin);

        // Set up and publish user load service
        HttpContext loadAPI = server.createContext("/load");
        loadAPI.setHandler(UserService::handleLoad);
        boolean published2 = register(registryAddr, baseURL + "/load", 10000, ServiceInfoModel.SERVICE_USER_LOAD);

        // Setup and publish user save
        HttpContext saveAPI = server.createContext("/save");
        saveAPI.setHandler(UserService::handleSave);
        boolean published3 = register(registryAddr, baseURL + "/save", 10000, ServiceInfoModel.SERVICE_USER_SAVE);

        // Try to publish again if some failed
        while (true) {
            if (published1 && published2 && published3) {
                break;
            }
            if (!published1)
                published1 = register(registryAddr, baseURL + "/login", 10000, ServiceInfoModel.SERVICE_USER_LOGIN);

            if (!published2)
                published2 = register(registryAddr, baseURL + "/load", 10000, ServiceInfoModel.SERVICE_USER_LOAD);

            if (!published3)
                published3 = register(registryAddr, baseURL + "/save", 10000, ServiceInfoModel.SERVICE_USER_SAVE);

            Thread.sleep(1000);
        }

        // handle the CORS error
        loginAPI.getFilters().add(new CORSFilter());
        loadAPI.getFilters().add(new CORSFilter());
        saveAPI.getFilters().add(new CORSFilter());

        // Server will only start after the services are registered;
        server.start();
    }

    /**
     * Connect to the database
     */
    private static void connect() {
        jedis = new Jedis("redis://default:Regen008@redis-13244.c44.us-east-1-2.ec2.cloud.redislabs.com:13244");
        String result = jedis.ping();

        if (result.equals("PONG")) {
            System.out.println("Connection to redis db successful!");
        } else {
            System.out.println("Connection to redis db failed!");
        }
    }

    private static void handleLogin(HttpExchange exchange) throws IOException {
        String content = extractMessage(exchange);
        String idString;
        String usrname;
        String passwrd;
        int id;
        User userObj = new User();
        userObj.userID = -1;
        try {
            User data = gson.fromJson(content, User.class);
            usrname = data.username;
            passwrd = data.password;
            idString = jedis.get("user2id:" + usrname);
            // System.out.println(idString);

            // If there is no username of that set fail
            if (idString == null) {
                System.out.println("No username");
                userObj.userID = -1;
            } else {
                id = Integer.parseInt(idString);
                String userName = jedis.hget("user:" + idString, "userName");
                System.out.println(userName);
                // If the password is wrong, set fail
                if (!passwrd.equals(jedis.hget("user:" + idString, "passWord"))) {
                    System.out.println("Password is wrong");
                    userObj.userID = -1;
                } else {
                    userObj.username = userName;
                    userObj.userID = id;
                    userObj.fullName = jedis.hget("user:" + idString, "displayName");
                    userObj.password = passwrd;
                    userObj.email = jedis.hget("user:" + idString, "email");
                    userObj.wishListID = Integer.parseInt(jedis.hget("user:" + idString, "wishListID"));
                    System.out.println("logged in with userID " + userObj.userID);
                }
            }
            // return userObj;

        } catch (Exception e) {
            e.printStackTrace();
            userObj.userID = -1;
        }
        String jsonRes = gson.toJson(userObj);
        SendJSONResponse(exchange, jsonRes, false);
    }

    /**
     * Get user info according to given userID
     */
    private static void handleLoad(HttpExchange exchange) throws IOException {

        String content = extractMessage(exchange);
        content = gson.fromJson(content, String.class);
        System.out.println("Requested load user by ID" + content);
        String idKey;
        User userObj = new User();
        userObj.userID = -1;
        int id;
        try {
            // User data = gson.fromJson(content, User.class);
            id = Integer.parseInt(content);
            idKey = "user:" + id;
            System.out.println(idKey);
            if (jedis.exists(idKey)) {
                System.out.println("user found");
                userObj.username = jedis.hget(idKey, "userName");
                userObj.userID = id;
                userObj.fullName = jedis.hget(idKey, "displayName");
                userObj.password = jedis.hget(idKey, "password");
                userObj.email = jedis.hget(idKey, "email");
                userObj.wishListID = Integer.parseInt(jedis.hget(idKey, "wishListID"));
            } else {
                System.out.println("user of id does not exist");
                userObj.userID = -1;
            }
            // return userObj;

        } catch (Exception e) {
            e.printStackTrace();
            userObj.userID = -1;
        }
        String jsonRes = gson.toJson(userObj);
        SendJSONResponse(exchange, jsonRes, false);

    }

    private static void handleSave(HttpExchange exchange) throws IOException {

        String content = extractMessage(exchange);
        System.out.println("Requested register");
        try {
            User temp = gson.fromJson(content, User.class);
            System.out.println("Username: " + temp.username);
            System.out.println("password: " + temp.password);
            // Get the incrementing userid
            int newID = Integer.parseInt(jedis.get("userIDmax"));

            if (!jedis.exists("user2id:" + temp.username)) {
                // Increment incrementing userID
                jedis.set("userIDmax", Integer.toString(newID + 1));
                // Create new userdata hash
                Map<String, String> userData = new HashMap<String, String>();
                userData.put("userName", temp.username);
                userData.put("passWord", temp.password);
                userData.put("displayName", temp.fullName);
                userData.put("email", temp.email);
                userData.put("wishListID", Integer.toString(newID));
                // Create new user to id map
                jedis.set("user2id:" + temp.username, Integer.toString((newID)));
                // Insert new user
                jedis.hset("user:" + newID, userData);
                // Set the return object's user and wishlistID,
                temp.userID = newID;
                temp.wishListID = newID;
                // Send
                SendJSONResponse(exchange, gson.toJson(temp), true);
                return;
            } else {
                System.out.println("Username: " + temp.username + " already exist");
            }

        } catch (Exception e) {
            User empty = new User();
            empty.userID = -1;
            SendJSONResponse(exchange, gson.toJson(empty), false);
            e.printStackTrace();
            return;
        }
        // This will be reached if the username already exist
        User empty = new User();
        empty.userID = -1;
        SendJSONResponse(exchange, gson.toJson(empty), false);
        return;

    }

    /**
     * Get the request body content of http request
     */
    private static String extractMessage(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        // Read the JSON data from the input stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String json = reader.readLine();
        System.out.println("Received content: " + json);
        return json;
    }

    /**
     * Send back a http response that contains json content
     */
    private static void SendJSONResponse(HttpExchange exchange, String jsonRes, boolean isPost) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        if (isPost)
            exchange.sendResponseHeaders(201, jsonRes.length());
        else
            exchange.sendResponseHeaders(200, jsonRes.length());
        OutputStream os = exchange.getResponseBody();
        os.write(jsonRes.getBytes());
        os.close();
    }

}
