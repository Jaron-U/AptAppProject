import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.*;
import java.util.*;

import com.google.gson.Gson;

public class ServiceRegistry {
    public static HashMap<Integer, List<ServiceInfoModel>> registry = new HashMap<>();
    private static Gson gson = new Gson();

    public static void main(String[] args) throws Exception {

        System.out.println("Service Registry Started");

        // Create server at port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        HttpContext root = server.createContext("/pub");
        root.setHandler(ServiceRegistry::handlePublish);

        HttpContext product = server.createContext("/disc");
        product.setHandler(ServiceRegistry::handleDisc);

        server.start();
    }

    private static void handlePublish(HttpExchange exchange) throws IOException {
        System.out.println("Service publish requested");
        if (!containsJSON(exchange)) {
            System.out.println("lacks JSON body");
            return;
        }
        InputStream is = exchange.getRequestBody();
        // Read the JSON data from the input stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String json = reader.readLine();
        System.out.println(json);
        ServiceMessageModel res = new ServiceMessageModel();
        // Try to register
        try {
            // Get the request message
            ServiceMessageModel reqMsg = gson.fromJson(json, ServiceMessageModel.class);
            if (reqMsg.code != ServiceMessageModel.SERVICE_PUBLISH_REQUEST) {
                System.out.println("Wrong code");
                res.code = ServiceMessageModel.SERVICE_PUBLISH_FAILED;
            } else {
                // Get the request info
                ServiceInfoModel info = gson.fromJson(reqMsg.data, ServiceInfoModel.class);
                List<ServiceInfoModel> list = registry.get(info.serviceCode);
                // Add it to the registry
                if (list == null) {
                    list = new ArrayList<ServiceInfoModel>();
                    registry.put(info.serviceCode, list);
                }
                list.add(info);
                System.out
                        .println("Published service code: " + info.serviceCode + " address " + info.serviceHostAddress);
                System.out.println("check " + registry.get(info.serviceCode).get(0).serviceHostAddress);
                res.code = ServiceMessageModel.SERVICE_PUBLISH_OK;
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.code = ServiceMessageModel.SERVICE_PUBLISH_FAILED;
        }

        // Send the response
        res.data = "";
        String jsonRes = gson.toJson(res);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonRes.length());
        OutputStream os = exchange.getResponseBody();
        os.write(jsonRes.getBytes());
        os.close();

    }

    private static void handleDisc(HttpExchange exchange) throws IOException {
        System.out.println("Service discovery requested");
        if (!containsJSON(exchange)) {
            System.out.println("lacks JSON body");
            return;
        }
        InputStream is = exchange.getRequestBody();
        // Read the JSON data from the input stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String json = reader.readLine();
        System.out.println(json);
        // object for response
        ServiceMessageModel res = new ServiceMessageModel();
        res.data = "";
        try {
            // Get the request message
            ServiceMessageModel msg = gson.fromJson(json, ServiceMessageModel.class);
            if (msg.code != ServiceMessageModel.SERVICE_DISCOVER_REQUEST) {
                System.out.println("Wrong code");
                return;
            } else {
                // Get the request info
                ServiceInfoModel info = gson.fromJson(msg.data, ServiceInfoModel.class);
                System.out.println("seeking service " + info.serviceCode);
                List<ServiceInfoModel> list;
                if (!registry.containsKey(info.serviceCode)) {
                    // return nothing if server unregistered
                    System.out.println("service not found");
                    res.code = ServiceMessageModel.SERVICE_DISCOVER_NOT_FOUND;
                    res.data = "";
                } else {
                    // return service if registered
                    System.out.println("service found");
                    list = registry.get(info.serviceCode);
                    res.code = ServiceMessageModel.SERVICE_DISCOVER_OK;
                    // Get a random service info
                    Random rand = new Random();
                    int id = rand.nextInt() % list.size();
                    System.out.println(id);
                    String jsonINFO = gson.toJson(list.get(id));
                    res.data = gson.toJson(jsonINFO);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Send the response
        String jsonRes = gson.toJson(res);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonRes.length());
        OutputStream os = exchange.getResponseBody();
        os.write(jsonRes.getBytes());
        os.close();
    }

    private static boolean containsJSON(HttpExchange exchange) throws IOException {
        java.util.Map<String, List<String>> requestHeaders = exchange.getRequestHeaders();

        // Check if the request have specified json body
        boolean JSONReq = false;
        if (requestHeaders.containsKey("Content-type")) {
            // Get accept types
            List<String> acceptHeaderValues = requestHeaders.get("Content-type");
            // Iterate through the values (if there are multiple values)
            for (String acceptHeaderValue : acceptHeaderValues) {
                if (acceptHeaderValue.contains("application/json")) {
                    System.out.println("Client requests with JSON");
                    JSONReq = true;
                    break;
                }
                System.out.println("Content type: " + acceptHeaderValue);

            }
            return JSONReq;
        } else {
            System.out.println("No body specified");
            return JSONReq;
        }
    }
}
