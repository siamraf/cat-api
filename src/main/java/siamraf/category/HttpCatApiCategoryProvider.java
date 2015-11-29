package siamraf.category;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpCatApiCategoryProvider implements CategoryProvider {

    private final String httpUrl;
    private final DocumentBuilder documentBuilder;

    public HttpCatApiCategoryProvider(String httpUrl) {
        this.httpUrl = httpUrl;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            documentBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Failed to initialise xml parser");
        }
    }

    @Override
    public List<String> getCategories() {
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            conn = (HttpURLConnection) new URL(httpUrl).openConnection();
            inputStream = conn.getInputStream();
            Document document = documentBuilder.parse(inputStream);
            return getCategoryNames(document);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get categories from server");
        } catch (SAXException e) {
            throw new RuntimeException("Failed to parse response from server");
        } finally {
            cleanup(conn, inputStream);
        }
    }

    private void cleanup(HttpURLConnection conn, InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        }
        if (conn != null) {
            conn.disconnect();
        }
    }

    private List<String> getCategoryNames(Document document) {
        NodeList categoryNames = document.getElementsByTagName("name");
        List<String> result = new ArrayList<>(categoryNames.getLength());
        for (int i = 0; i < categoryNames.getLength(); i++) {
            result.add(categoryNames.item(i).getTextContent());
        }
        return result;
    }
}
