package siamraf.image;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import siamraf.testutil.TestHttpServer;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class HttpCatApiImageProviderTest {

    private static final String GET_IMAGE_API_PATH = "/get";
    private static final String IMAGE_NAME = "kittycat.jpg";
    private static final String SAMPLE_IMAGE_XML_RESPONSE =
            "<?xml version=\"1.0\"?>\n" +
                    "<response>\n" +
                    "  <data>\n" +
                    "    <images>\n" +
                    "      <image>\n" +
                    "        <url>%s/" + IMAGE_NAME + "</url>\n" +
                    "        <id>610</id>\n" +
                    "        <source_url>http://thecatapi.com/?id=610</source_url>\n" +
                    "      </image>\n" +
                    "    </images>\n" +
                    "  </data>\n" +
                    "</response>\n";

    private static final String IMAGE_DATA = "<( ^o^ )> rawr";

    private TestHttpServer httpServer;

    @Before
    public void setUp() throws Exception {
        httpServer = new TestHttpServer();
        httpServer.start();
    }

    @After
    public void tearDown() throws Exception {
        httpServer.stop();
    }

    @Test
    public void saveSampleImage() throws Exception {
        // given
        httpServer.respondToPathWith(GET_IMAGE_API_PATH,
                String.format(SAMPLE_IMAGE_XML_RESPONSE, httpServer.getServerAddress()));

        httpServer.respondToPathWith("/" + IMAGE_NAME, IMAGE_DATA);

        // when
        HttpCatApiImageProvider imageProvider = new HttpCatApiImageProvider(httpServer.getServerAddress() + GET_IMAGE_API_PATH);
        CatImage catImage = imageProvider.getCatImage();

        // then
        assertThat(catImage.getImageLocation(), equalTo(httpServer.getServerAddress() + "/" + IMAGE_NAME));
        assertThat(catImage.getImageName(), equalTo(IMAGE_NAME));
        assertThat(catImage.getImageData(), equalTo(IMAGE_DATA.getBytes(StandardCharsets.UTF_8)));
    }

    @Test(expected = RuntimeException.class)
    public void throwExceptionOnServerError() throws Exception {
        // when
        HttpCatApiImageProvider imageProvider = new HttpCatApiImageProvider(httpServer.getServerAddress() + "/badPath");

        // then
        imageProvider.getCatImage();
    }

    @Test(expected = RuntimeException.class)
    public void throwExceptionOnBadlyFormattedResponse() throws Exception {
        // given
        httpServer.respondToPathWith(GET_IMAGE_API_PATH, "computer says no");

        // when
        HttpCatApiImageProvider imageProvider = new HttpCatApiImageProvider(httpServer.getServerAddress() + GET_IMAGE_API_PATH);

        // then
        imageProvider.getCatImage();
    }
}