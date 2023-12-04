import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class WebClient {

    public static void main(String[] args) throws IOException {
        int port = 8001;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Server started at port: " + port);
    }

    static class StaticFileHandler implements HttpHandler {
        private final String root = "web_templates";

        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();

            String filePath;
            if (path.endsWith(".html") || path.equals("/")) {
                filePath = root + "/html" + (path.equals("/") ? "/index.html" : path);
            } else {
                filePath = root + path;
            }

            File file = new File(filePath).getCanonicalFile();

            if (!file.getPath().startsWith(new File(root).getCanonicalPath())) {
                send404(exchange);
                return;
            }

            if (file.isFile()) {
                String mime = Files.probeContentType(file.toPath());
                exchange.getResponseHeaders().set("Content-Type", mime != null ? mime : "application/octet-stream");
                exchange.sendResponseHeaders(200, file.length());

                try (OutputStream os = exchange.getResponseBody(); FileInputStream fs = new FileInputStream(file)) {
                    final byte[] buffer = new byte[0x10000];
                    int count;
                    while ((count = fs.read(buffer)) >= 0) {
                        os.write(buffer, 0, count);
                    }
                }
            } else {
                send404(exchange);
            }
        }

        private void send404(HttpExchange exchange) throws IOException {
            String response = "404 (Not Found)\n";
            exchange.sendResponseHeaders(404, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}

