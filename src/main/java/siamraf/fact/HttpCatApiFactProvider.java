package siamraf.fact;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpCatApiFactProvider implements CatFactProvider {

    private final String httpUrl;
    private final JsonParser jsonParser;

    public HttpCatApiFactProvider(String httpUrl) {
        this.httpUrl = httpUrl;
        this.jsonParser = new JsonParser();
    }

    @Override
    public String getFact() {
        HttpURLConnection conn = null;
        InputStreamReader inputStreamReader = null;

        try {
            conn = (HttpURLConnection) new URL(httpUrl).openConnection();
            inputStreamReader = new InputStreamReader(conn.getInputStream());
            return getFactFromJsonInputStream(inputStreamReader);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get fact from server", e);
        } finally {
            cleanup(conn, inputStreamReader);
        }
    }

    private String getFactFromJsonInputStream(InputStreamReader inputStreamReader) {
        try {
            JsonObject jsonResult = jsonParser.parse(inputStreamReader).getAsJsonObject();
            JsonArray facts = jsonResult.getAsJsonArray("facts");
            return facts.get(0).getAsString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response from server", e);
        }
    }

    private void cleanup(HttpURLConnection conn, InputStreamReader inputStreamReader) {
        if (inputStreamReader != null) {
            try {
                inputStreamReader.close();
            } catch (IOException ignored) {
            }
        }
        if (conn != null) {
            conn.disconnect();
        }
    }

}
