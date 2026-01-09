import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URLDecoder;

public class TypingSpeedServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/calculate", new TypingSpeedHandler());
        server.setExecutor(null); // default executor
        System.out.println("Server started at http://localhost:8080");
        server.start();
    }

    static class TypingSpeedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Handle CORS preflight
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);

                String[] params = sb.toString().split("&");
                double time = 0;
                String text = "";

                for (String param : params) {
                    String[] pair = param.split("=");
                    if (pair[0].equals("text")) text = URLDecoder.decode(pair[1], "UTF-8");
                    if (pair[0].equals("time")) time = Double.parseDouble(pair[1]);
                }

                // Debugging: Log received data
                System.out.println("Received Text: " + text);
                System.out.println("Received Time: " + time);

                double wpm = 0;
                if (text != null && !text.isEmpty() && time > 0) {
                    int wordCount = text.trim().split("\\s+").length;
                    wpm = (wordCount / time) * 60;
                }

                // If wpm is still 0, print a warning
                if (wpm == 0) {
                    System.out.println("Warning: WPM calculation failed. Ensure the sentence is not empty or invalid.");
                }

                String jsonResponse = String.format("{\"time\": %.2f, \"wpm\": %.2f}", time, wpm);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                byte[] responseBytes = jsonResponse.getBytes("UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // Method not allowed
            }
        }
    }
}