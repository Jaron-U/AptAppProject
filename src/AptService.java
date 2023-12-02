import com.google.gson.Gson;
import com.sun.net.httpserver.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static void handleRequestGetAllApts(HttpExchange exchange){

    }

    private static void handleRequestOneApt(HttpExchange exchange, String path){

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
}
