package siamraf.category;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import siamraf.testutil.TestHttpServer;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class HttpCatApiCategoryProviderTest {

    private static final String CATEGORIES_PATH = "/getCategories";
    private static final String SAMPLE_RESPONSE = "<?xml version=\"1.0\"?>\n" +
            "<response>\n" +
            "  <data>\n" +
            "    <categories>\n" +
            "      <category>\n" +
            "        <id>1</id>\n" +
            "        <name>hats</name>\n" +
            "      </category>\n" +
            "      <category>\n" +
            "        <id>2</id>\n" +
            "        <name>space</name>\n" +
            "      </category>\n" +
            "      <category>\n" +
            "        <id>3</id>\n" +
            "        <name>funny</name>\n" +
            "      </category>\n" +
            "      <category>\n" +
            "        <id>4</id>\n" +
            "        <name>sunglasses</name>\n" +
            "      </category>\n" +
            "    </categories>\n" +
            "  </data>\n" +
            "</response>\n";

    private static final String EMPTY_RESPONSE = "<?xml version=\"1.0\"?>\n" +
            "<response>\n" +
            "  <data>\n" +
            "    <categories>\n" +
            "    </categories>\n" +
            "  </data>\n" +
            "</response>\n";

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
    public void getValidCategories() throws Exception {
        // given
        httpServer.respondToPathWith(CATEGORIES_PATH, SAMPLE_RESPONSE);

        // when
        HttpCatApiCategoryProvider categoryProvider = new HttpCatApiCategoryProvider(httpServer.getServerAddress() + CATEGORIES_PATH);

        // then
        assertThat(categoryProvider.getCategories(), equalTo(asList("hats", "space", "funny", "sunglasses")));
    }

    @Test
    public void getEmptyCategories() throws Exception {
        // given
        httpServer.respondToPathWith(CATEGORIES_PATH, EMPTY_RESPONSE);

        // when
        HttpCatApiCategoryProvider categoryProvider = new HttpCatApiCategoryProvider(httpServer.getServerAddress() + CATEGORIES_PATH);

        // then
        assertThat(categoryProvider.getCategories(), equalTo(asList()));
    }

    @Test(expected = RuntimeException.class)
    public void throwExceptionOnServerError() throws Exception {
        // when
        HttpCatApiCategoryProvider categoryProvider = new HttpCatApiCategoryProvider(httpServer.getServerAddress() + "/badPath");

        // then
        categoryProvider.getCategories();
    }

    @Test(expected = RuntimeException.class)
    public void throwExceptionOnBadlyFormattedResponse() throws Exception {
        // given
        httpServer.respondToPathWith(CATEGORIES_PATH, "computer says no");

        // when
        HttpCatApiCategoryProvider categoryProvider = new HttpCatApiCategoryProvider(httpServer.getServerAddress() + CATEGORIES_PATH);

        // then
        categoryProvider.getCategories();
    }
}