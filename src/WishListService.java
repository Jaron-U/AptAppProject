
import java.io.*;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WishListService extends MicroService {
    private static boolean connected = false;
    private static Jedis jedis;
    private static String registryAddr = "http://localhost:8080/pub";
    private static String baseURL = "http://localhost:10001";

    public static void main(String[] args) throws Exception {
        connect();
        // Create server and context
        HttpServer server = HttpServer.create(new InetSocketAddress(10001), 0);
        // Set up and publish load service
        HttpContext loadAPI = server.createContext("/load");
        boolean published1 = register(registryAddr, baseURL + "/load", 10001, ServiceInfoModel.SERVICE_WISHLIST_LOAD);
        loadAPI.setHandler(WishListService::handleLoad);

        // Set up and publish add service
        HttpContext addAPI = server.createContext("/add");
        addAPI.setHandler(WishListService::handleAdd);
        boolean published2 = register(registryAddr, baseURL + "/add", 10001, ServiceInfoModel.SERVICE_WISHLIST_ADD);

        // Set up and publish add service
        HttpContext deleteAPI = server.createContext("/delete");
        deleteAPI.setHandler(WishListService::handleDelete);
        boolean published3 = register(registryAddr, baseURL + "/delete", 10001,
                ServiceInfoModel.SERVICE_WISHLIST_DELETE);

        // Try to publish again if some failed
        while (true) {
            if (published1 && published2 && published3) {
                System.out.println("All Services published successfully");
                break;
            }
            if (!published1)
                published1 = register(registryAddr, baseURL + "/load", 10001, ServiceInfoModel.SERVICE_WISHLIST_LOAD);

            if (!published2)
                published2 = register(registryAddr, baseURL + "/add", 10001, ServiceInfoModel.SERVICE_WISHLIST_ADD);

            if (!published3)
                published3 = register(registryAddr, baseURL + "/delete", 10001,
                        ServiceInfoModel.SERVICE_WISHLIST_DELETE);

            Thread.sleep(1000);
        }
        // Server will only start after the services are registered;
        server.start();
    }

    private static void connect() {
        jedis = new Jedis("redis://default:Regen008@redis-13244.c44.us-east-1-2.ec2.cloud.redislabs.com:13244");
        String result = jedis.ping();

        if (result.equals("PONG")) {
            System.out.println("Connection to redis db successful!");
        } else {
            System.out.println("Connection to redis db failed!");
        }
    }

    private static void handleLoad(HttpExchange exchange) throws IOException {

        String content = extractMessage(exchange);
        content = gson.fromJson(content, String.class);
        System.out.println("Requested load wishlist ID: " + content);
        String keyString;
        int wishListID;
        List<String> wishList = new ArrayList<String>();
        Set<String> members;

        try {
            wishListID = Integer.parseInt(content);
            keyString = "wishList:" + content;
            // createListIfNone(wishListID);
            if (jedis.exists(keyString)) {
                System.out.println("Contents of wishlist " + wishListID + ":");
                members = jedis.smembers(keyString);
                for (String member : members) {
                    wishList.add(member);
                    System.out.println(member);
                }
            } else {
                System.out.println("WishList does not exist (Or is empty because nothing is added)");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String jsonRes = gson.toJson(wishList);
        SendJSONResponse(exchange, jsonRes);
    }

    private static void handleAdd(HttpExchange exchange) throws IOException {
        System.out.println("Requested add to wishlist");
        String content = extractMessage(exchange);
        String keyString;
        TupleArgs res = new TupleArgs();
        try {

            TupleArgs args = gson.fromJson(content, TupleArgs.class);
            // Not really needed, but to check if it is a valid ID
            int wishListID = Integer.parseInt(args.arg0);
            keyString = "wishList:" + args.arg0;
            String aptID = args.arg1;
            if (jedis.exists("user:" + args.arg0)) {
                // Adding the element, while getting value indicating whether it is added
                res.arg1 = Long.toString(jedis.sadd(keyString, args.arg1));
                res.arg0 = Integer.toString(wishListID);
            } else {
                res.arg0 = "-1";
                res.arg1 = "0";
                System.out.println("It is trying to insert to a wishlist that shouldn't exist");
            }
            // createListIfNone(wishListID);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String jsonRes = gson.toJson(res);
        SendJSONResponse(exchange, jsonRes);
    }

    private static void handleDelete(HttpExchange exchange) throws IOException {
        System.out.println("Requested delete from wishList");
        String content = extractMessage(exchange);
        String keyString;
        TupleArgs res = new TupleArgs();
        try {
            TupleArgs args = gson.fromJson(content, TupleArgs.class);
            int wishListID = Integer.parseInt(args.arg0);
            keyString = "wishList:" + args.arg0;
            System.out.println("Attempting to delete " + args.arg1 + " from " + keyString);
            if (jedis.exists("user:" + args.arg0)) {
                // Deleting the element, while getting value indicating whether it is deleted
                res.arg1 = Long.toString(jedis.srem(keyString, args.arg1));
                res.arg0 = Integer.toString(wishListID);
            } else {
                res.arg0 = "-1";
                res.arg1 = "0";
                System.out.println("It is trying to delete from a wishlist that shouldn't exist");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String jsonRes = gson.toJson(res);
        SendJSONResponse(exchange, jsonRes);
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
    private static void SendJSONResponse(HttpExchange exchange, String jsonRes) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonRes.length());
        OutputStream os = exchange.getResponseBody();
        os.write(jsonRes.getBytes());
        os.close();
    }
}
