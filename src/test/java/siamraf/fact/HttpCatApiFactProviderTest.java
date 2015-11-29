package siamraf.fact;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import siamraf.testutil.TestHttpServer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class HttpCatApiFactProviderTest {
    public static final String THE_FACT = "In households in the UK and USA, there are more cats kept as pets than dogs. At least 35% of households with cats have 2 or more cats.";
    public static final String SAMPLE_FACT_RESPONSE = "{\"facts\": [\"" + THE_FACT + "\"], \"success\": \"true\"}";
    public static final String FACT_PATH = "/getFact";

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
    public void getValidFact() throws Exception {
        // given
        httpServer.respondToPathWith(FACT_PATH, SAMPLE_FACT_RESPONSE);

        // when
        HttpCatApiFactProvider factProvider = new HttpCatApiFactProvider(httpServer.getServerAddress() + FACT_PATH);

        // then
        assertThat(factProvider.getFact(), equalTo(THE_FACT));
    }

    @Test(expected = RuntimeException.class)
    public void throwExceptionOnServerError() throws Exception {
        // when
        HttpCatApiFactProvider factProvider = new HttpCatApiFactProvider(httpServer.getServerAddress() + "/badPath");
        factProvider.getFact();
    }

    @Test(expected = RuntimeException.class)
    public void throwExceptionOnBadlyFormattedResponse() throws Exception {
        // given
        httpServer.respondToPathWith(FACT_PATH, "Durp durp");

        // when
        HttpCatApiFactProvider factProvider = new HttpCatApiFactProvider(httpServer.getServerAddress() + FACT_PATH);

        // then
        factProvider.getFact();
    }
}