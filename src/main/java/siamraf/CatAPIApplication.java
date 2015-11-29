package siamraf;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CatAPIApplication {

    private PrintWriter output;
    private CategoryProvider categoryProvider;

    public CatAPIApplication(OutputStream outputStream, CategoryProvider categoryProvider) {
        this.output = new PrintWriter(outputStream);
        this.categoryProvider = categoryProvider;
    }

    public void printCategories() {
        List<String> categories = categoryProvider.getCategories();
        Collections.sort(categories, Comparator.naturalOrder());

        categories.forEach(output::println);
        output.flush();
    }
}
