import com.google.gson.Gson;
import com.sun.net.httpserver.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class AptService extends MicroService{
    static AptMongoDBAdapter dataAdapter = new AptMongoDBAdapter();
    private static String registryAddr = "http://localhost:8080/pub";
    private static String baseURL = "http://localhost:8086";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8086), 0);

        server.createContext("/apt", AptService::handleRequestApt);
        boolean aptServiceGetOne = register(registryAddr, baseURL + "/apt", 8086, ServiceInfoModel.SERVICE_APT_LOAD);
        boolean aptServiceCreateOne = register(registryAddr, baseURL + "/apt", 8086, ServiceInfoModel.SERVICE_APT_SAVE);
        boolean aptServiceGetAll = register(registryAddr, baseURL + "/apt", 8086, ServiceInfoModel.SERVICE_APT_LOADALL);
        if (!aptServiceGetOne && !aptServiceCreateOne && !aptServiceGetAll) {
            System.out.println("error to register aptService");
        }

        server.createContext("/apt/findAptByPrice", AptService::handleRequestFindAptsByPrice);
        boolean aptServiceFindByPrice = register(registryAddr, baseURL + "/apt/findAptByPrice", 8086, ServiceInfoModel.SERVICE_APT_SEARCH_PRICE);
        if (!aptServiceFindByPrice) {
            System.out.println("Find apt by price service register failed");
        }

        server.createContext("/apt/findAptByType", AptService::handleRequestFindAptsByType);
        boolean aptServiceFindByType = register(registryAddr, baseURL + "/apt/findAptByType", 8086, ServiceInfoModel.SERVICE_APT_SEARCH_TYPE);
        if (!aptServiceFindByType) {
            System.out.println("Find apt by type service register failed");
        }

        server.start();

        System.out.println("Apartment Service started at http://localhost:8086/apt");
    }

    private static void handleRequestApt(HttpExchange exchange) throws IOException{
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        String method = exchange.getRequestMethod();
        // check quest method
        if (method.equals("GET")) {
            // if the quest query is "/apt" means get all apartments
            // else the quest query is "/apt/id"
            if (path.equals("/apt")){
                handleRequestGetAllApts(exchange);
            } else {
                handleRequestOneApt(exchange, path);
            }
        } else if (method.equals("POST")) {
            handleRequestCreateApt(exchange, exchange.getRequestBody());
        }
    }

    private static void handleRequestFindAptsByPrice(HttpExchange exchange) throws IOException{
        searchAptsByPrice(exchange, exchange.getRequestBody());
    }

    private static void handleRequestFindAptsByType(HttpExchange exchange) throws IOException{
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        searchAptsByType(exchange, path);
    }

    private static void handleRequestGetAllApts(HttpExchange exchange) throws IOException {
        List<Apartment> apartments = new ArrayList<>();
        Gson gsonApt = new Gson();
        int resCode = 0;
        String response = "";
        System.out.println("Get All Apts");
        apartments = dataAdapter.getAllApts();
        if (apartments != null) {
            resCode = 200;
            response = gsonApt.toJson(apartments);
        } else {
            resCode = 404;
            response = "Not Found";
        }
        exchange.sendResponseHeaders(resCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void handleRequestOneApt(HttpExchange exchange, String path) throws IOException {
        int resCode = 0;
        String response = "";

        String id = path.substring(path.lastIndexOf('/')+1);
        System.out.println("Get apt by id: " + id);
        Apartment apartment = dataAdapter.getAptById(id);
        if (apartment != null) {
            resCode = 200;
            response = gson.toJson(apartment);
        } else {
            resCode = 404;
            response = "Not Found";
        }
        exchange.sendResponseHeaders(resCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void handleRequestCreateApt(HttpExchange exchange, InputStream requestBody) throws IOException {
        Gson gson = new Gson();
        String response = "";
        int resCode = 0;
        InputStreamReader isr = new InputStreamReader(requestBody);
        try {
            Apartment aptReq = gson.fromJson(isr, Apartment.class);
            System.out.println("Request create apt name: " + aptReq.getAptName());
            String id = dataAdapter.createApt(aptReq);
            if (id != null) {
                response = id;
                resCode = 200; // OK
            } else {
                response = "error to create a apartment";
                resCode = 500;
            }

        } catch (Exception e) {
            response = "Error processing request: " + e.getMessage();
            resCode = 400; // Bad Request
        }

        exchange.sendResponseHeaders(resCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void searchAptsByPrice(HttpExchange exchange, InputStream requestBody) throws IOException{
        int resCode = 0;
        String response = "";
        List<Apartment> apartments = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
        StringBuilder requestBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBuilder.append(line);
        }
        String requestBodyString = requestBuilder.toString();

        JSONObject jsonObj = new JSONObject(requestBodyString);
        double lowPrice = jsonObj.getDouble("lowPrice");
        double highPrice = jsonObj.getDouble("highPrice");
        System.out.println("search apts by price, low: " + lowPrice + "high: "+highPrice);
        apartments = dataAdapter.searchAptByPrice(lowPrice, highPrice);
        if (apartments != null) {
            resCode = 200;
            response = gson.toJson(apartments);
        } else {
            resCode = 404;
            response = "Not Found";
        }

        exchange.sendResponseHeaders(resCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void searchAptsByType(HttpExchange exchange, String path) throws IOException {
        int resCode = 0;
        String response = "";

        String type = path.substring(path.lastIndexOf('/')+1);
        System.out.println("Get apts by type: " + type);
        List<Apartment> apartments = new ArrayList<>();

        apartments = dataAdapter.searchAptsByType(type);
        if (apartments != null) {
            resCode = 200;
            response = gson.toJson(apartments);
        } else {
            resCode = 404;
            response = "Not Found";
        }
        exchange.sendResponseHeaders(resCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
