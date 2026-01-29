package ebookstore.service;

import java.util.Collection;

public interface CsvExporter<T> {

    void exportToCsv(Collection<T> entities, String filePath);
}