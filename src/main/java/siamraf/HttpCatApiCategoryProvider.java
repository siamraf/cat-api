package siamraf;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
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
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(httpUrl).openConnection();
            Document document = documentBuilder.parse(conn.getInputStream());
            return getCategoryNames(document);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get categories from server");
        } catch (SAXException e) {
            throw new RuntimeException("Failed to parse response from server");
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
