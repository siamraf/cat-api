package siamraf;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CatAPIApplication {

    private final CatFactProvider factProvider;
    private final CatImageProvider imageProvider;
    private final File imageDir;
    private final PrintWriter output;
    private final CategoryProvider categoryProvider;

    public CatAPIApplication(OutputStream outputStream,
                             CategoryProvider categoryProvider,
                             CatFactProvider factProvider,
                             CatImageProvider imageProvider,
                             File imageDir) {
        this.factProvider = factProvider;
        this.imageProvider = imageProvider;
        this.imageDir = imageDir;
        this.output = new PrintWriter(outputStream);
        this.categoryProvider = categoryProvider;
    }

    public void printCategories() {
        List<String> categories = categoryProvider.getCategories();
        Collections.sort(categories, Comparator.naturalOrder());

        categories.forEach(output::println);
        output.flush();
    }

    public void printFact() {
        printAndFlush(factProvider.getFact());
    }

    private void printAndFlush(String fact) {
        output.println(fact);
        output.flush();
    }

    public void saveCatImage() throws IOException {
        CatImage catImage = imageProvider.getImageInputStream();
        Path localImagePath = imageDir.toPath().resolve(catImage.getImageName());
        Files.write(localImagePath, catImage.getImageData(), StandardOpenOption.CREATE);
        printAndFlush("Image from " + catImage.getImageLocation() + " saved to " + localImagePath.toUri());
    }
}
