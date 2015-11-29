package siamraf;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.File;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class CatAPIApplicationTest {

    @Rule
    public Timeout timeout = new Timeout(3, TimeUnit.SECONDS);

    private Scanner readFromProcess;
    private CatAPIApplication catApi;
    private CategoryProvider categoryProvider;
    private CatFactProvider factProvider;
    private File tempDirectory;
    private CatImageProvider imageProvider;

    @Before
    public void setUp() throws Exception {
        PipedOutputStream output = new PipedOutputStream();
        PipedInputStream source = new PipedInputStream(output);
        readFromProcess = new Scanner(source);

        categoryProvider = mock(CategoryProvider.class);
        factProvider = mock(CatFactProvider.class);
        imageProvider = mock(CatImageProvider.class);
        tempDirectory = Files.createTempDirectory("catTest").toFile();
        catApi = new CatAPIApplication(output, categoryProvider, factProvider, imageProvider, tempDirectory);
    }

    @After
    public void tearDown() throws Exception {
        tempDirectory.delete();
        readFromProcess.close();
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

    @Test
    public void shouldSaveImageToDirectory() throws Exception {
        // given
        byte[] myCatImage = "<( ^ . ^ )>  meow".getBytes(StandardCharsets.UTF_8);
        String imageName = "fakeImage.png";
        String imageLocation = "/test/location";
        given(imageProvider.getImageInputStream()).willReturn(new CatImage(imageLocation, imageName, myCatImage));

        // when
        catApi.saveCatImage();

        // then
        String expectedImagePath = tempDirectory.toPath() + "/" + imageName;
        expectRead("Image from " + imageLocation + " saved to file://" + expectedImagePath);
        byte[] fileContents = Files.readAllBytes(Paths.get(expectedImagePath));
        assertThat(fileContents, equalTo(myCatImage));
    }

    private void expectRead(String expect) {
        assertEquals(expect, read());
    }

    private String read() {
        return readFromProcess.nextLine();
    }
}