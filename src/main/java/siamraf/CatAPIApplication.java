package siamraf;

import siamraf.category.CategoryProvider;
import siamraf.category.HttpCatApiCategoryProvider;
import siamraf.fact.CatFactProvider;
import siamraf.fact.HttpCatApiFactProvider;
import siamraf.image.CatImage;
import siamraf.image.CatImageProvider;
import siamraf.image.HttpCatApiImageProvider;

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

    public static final String CAT_API_CATEGORIES = "http://thecatapi.com/api/categories/list";
    public static final String CAT_API_FACTS = "http://catfacts-api.appspot.com/api/facts";
    public static final String CAT_API_IMAGES = "http://thecatapi.com/api/images/get?format=xml";

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

    public static void main(String[] args) {
        if (args.length != 1) {
            printUsage();
            return;
        }

        CatAPIApplication catAPIApplication = new CatAPIApplication(System.out,
                new HttpCatApiCategoryProvider(CAT_API_CATEGORIES),
                new HttpCatApiFactProvider(CAT_API_FACTS),
                new HttpCatApiImageProvider(CAT_API_IMAGES),
                new File(System.getProperty("java.io.tmpdir")));

        String command = args[0];
        switch (command) {
            case "file":
                catAPIApplication.saveCatImage();
                break;
            case "categories":
                catAPIApplication.printCategories();
                break;
            case "fact":
                catAPIApplication.printFact();
                break;
            default:
                printUsage();
        }
    }

    private static void printUsage() {
        System.out.println("Usage:\n run.sh [ file | categories | fact ]");
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

    public void saveCatImage() {
        try {
            CatImage catImage = imageProvider.getCatImage();
            Path localImagePath = imageDir.toPath().resolve(catImage.getImageName());
            Files.write(localImagePath, catImage.getImageData(), StandardOpenOption.CREATE);
            printAndFlush("Image from " + catImage.getImageLocation() + " saved to " + localImagePath.toUri());
        } catch (IOException e) {
            throw new RuntimeException("Error writing image to file", e);
        }
    }
}
