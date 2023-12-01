import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.*;
import java.util.*;

import com.google.gson.Gson;

public class ServiceRegistry {
    public static HashMap<Integer, List<ServiceInfoModel>> registry = new HashMap<>();
    private Gson gson = new Gson();

    public static void main(String[] args) throws Exception {

        System.out.println("Hello world!");

        // server is listening on port 5000

        // running infinite loop for getting
        // client request
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        HttpContext root = server.createContext("/pub");
        root.setHandler(ServiceRegistry::handlePublish);

        // HttpContext context = server.createContext("/users");
        // context.setHandler(WebServer::handleRequestUser);

        HttpContext product = server.createContext("/disc");
        product.setHandler(ServiceRegistry::handleDisc);

        server.start();
    }
    private static void handlePublish(HttpExchange exchange) throws IOException {

    }
    private static void handleDisc(HttpExchange exchange) throws IOException {
        
    }
}
