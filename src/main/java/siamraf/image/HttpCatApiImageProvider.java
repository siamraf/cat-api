package siamraf.image;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;

public class HttpCatApiImageProvider implements CatImageProvider {
    private final DocumentBuilder documentBuilder;
    private String httpUrl;

    public HttpCatApiImageProvider(String httpUrl) {
        this.httpUrl = httpUrl;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            documentBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Failed to initialise xml parser");
        }
    }

    @Override
    public CatImage getCatImage() {
        HttpURLConnection apiConn = null;
        InputStream xmlInputStream = null;
        try {
            apiConn = (HttpURLConnection) new URL(httpUrl).openConnection();
            xmlInputStream = apiConn.getInputStream();
            String imageUrl = getImageUrl(xmlInputStream);
            String imageName = getNameFromUrl(imageUrl);
            byte[] imageBytes = getImageBytes(imageUrl);
            return new CatImage(imageUrl, imageName, imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get xml response from server");
        } catch (SAXException e) {
            throw new RuntimeException("Failed to parse xml response from server");
        } finally {
            cleanup(apiConn, xmlInputStream);
        }
    }

    public byte[] getImageBytes(String imageUrl) {
        HttpURLConnection imageConn = null;
        DataInputStream dataInputStream = null;
        try {
            imageConn = (HttpURLConnection) new URL(imageUrl).openConnection();
            int imageSize = imageConn.getContentLength();
            byte[] imageBytes = new byte[imageSize];
            dataInputStream = new DataInputStream(imageConn.getInputStream());
            dataInputStream.readFully(imageBytes);
            return imageBytes;
        } catch (IOException e) {
            throw new RuntimeException("Failed to get image from server", e);
        } finally {
            cleanup(imageConn, dataInputStream);
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

    public String getNameFromUrl(String imageUrl) {
        return Paths.get(imageUrl).getFileName().toString();
    }

    public String getImageUrl(InputStream xmlInputStream) throws SAXException, IOException {
        Document document = documentBuilder.parse(xmlInputStream);
        NodeList imageUrls = document.getElementsByTagName("url");
        if (imageUrls.getLength() < 1) {
            throw new RuntimeException("No images found");
        }
        return imageUrls.item(0).getTextContent();
    }
}
