package siamraf;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CatAPIApplication {

    private final CatFactProvider factProvider;
    private PrintWriter output;
    private CategoryProvider categoryProvider;

    public CatAPIApplication(OutputStream outputStream,
                             CategoryProvider categoryProvider,
                             CatFactProvider factProvider) {
        this.factProvider = factProvider;
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
        output.println(factProvider.getFact());
        output.flush();
    }

}
