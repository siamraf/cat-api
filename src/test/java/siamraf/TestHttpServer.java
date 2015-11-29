package siamraf;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility for stubbing out external server behaviour
 */
public class TestHttpServer implements HttpHandler {

    private final HttpServer server;
    private final Map<String, String> pathResponses;

    public TestHttpServer() throws IOException {
        pathResponses = new HashMap<>();
        server = HttpServer.create(new InetSocketAddress(Inet4Address.getLocalHost(), 0), 0);
        server.createContext("/", this);
    }

    public void start() {
        server.start();
    }

    public synchronized void respondToPathWith(String path, String response) {
        pathResponses.put(path, response);
    }

    public String getServerAddress() {
        return "http://" + server.getAddress().getHostString() + ":" + server.getAddress().getPort();
    }

    @Override
    public synchronized void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String response = pathResponses.get(path);
        OutputStream os = exchange.getResponseBody();
        if (response == null) {
            exchange.sendResponseHeaders(500, 0);
            response = "No response mapped for path " + path;
        } else {
            exchange.sendResponseHeaders(200, response.length());
        }
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }

    public void stop() {
        server.stop(0);
    }
}
