package siamraf;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class CatAPIApplicationTest {

    @Rule
    public Timeout timeout = new Timeout(3, TimeUnit.SECONDS);

    private Scanner readFromProcess;
    private CatAPIApplication catApi;
    private CategoryProvider categoryProvider;
    private CatFactProvider factProvider;

    @Before
    public void setUp() throws Exception {
        PipedOutputStream output = new PipedOutputStream();
        PipedInputStream source = new PipedInputStream(output);

        categoryProvider = mock(CategoryProvider.class);
        factProvider = mock(CatFactProvider.class);
        readFromProcess = new Scanner(source);
        catApi = new CatAPIApplication(output, categoryProvider, factProvider);
    }

    @Test
    public void shouldPrintCategoriesAlphabetically() throws Exception {
        // given
        given(categoryProvider.getCategories()).willReturn(asList("ties", "dream", "space", "funny", "hats"));

        // when
        catApi.printCategories();

        // then
        expectRead("dream");
        expectRead("funny");
        expectRead("hats");
        expectRead("space");
        expectRead("ties");
    }

    @Test
    public void shouldPrintCatFact() throws Exception {
        // given
        String fact = "Today there are about 100 distinct breeds of the domestic cat.";
        given(factProvider.getFact()).willReturn(fact);

        // when
        catApi.printFact();

        // then
        expectRead(fact);
    }

    private void expectRead(String expect) {
        assertEquals(expect, read());
    }

    private String read() {
        return readFromProcess.nextLine();
    }
}