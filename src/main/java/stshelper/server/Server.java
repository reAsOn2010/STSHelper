package stshelper.server;

import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import stshelper.MyHelper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    private Gson gson = new Gson();
    private List<AbstractCard> cards = CardLibrary.getAllCards();

    public Server () {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(27999), 0);
            server.createContext("/list-cards", new ListCardsHandler());
            server.createContext("/hack-card", new HackCardHandler());
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ListCardsHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if (cards.isEmpty()) {
                cards = CardLibrary.getAllCards();
            }
            Headers headers = httpExchange.getResponseHeaders();
            headers.set("Content-Type", "application/json; charset=UTF-8");
            Map<String, String> map = new HashMap<String, String>();
            for (Integer i = 0; i < cards.size(); i++) {
                map.put(i.toString(), cards.get(i).name);
            }
            byte[] response = gson.toJson(map).getBytes();
            httpExchange.sendResponseHeaders(200, response.length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    class HackCardHandler implements HttpHandler {

        class HackCardRequest {
            Integer id;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            HackCardRequest request = gson.fromJson(new InputStreamReader(httpExchange.getRequestBody()), HackCardRequest.class);
            Headers headers = httpExchange.getResponseHeaders();
            headers.set("Content-Type", "application/json; charset=UTF-8");
            Map<String, String> map = new HashMap<String, String>();
            MyHelper.putCard(cards.get(request.id));
            map.put("detail", "ok");
            byte[] response = gson.toJson(map).getBytes();
            httpExchange.sendResponseHeaders(200, response.length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }
}
