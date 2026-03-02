package ebookstore.model.enums;

public enum BookSortField {

    TITLE("title"),
    PRICE("price"),
    DATE("dateOfPublication"),
    STATUS("status");

    private final String field;

    BookSortField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public static BookSortField fromString(String value) {
        for (BookSortField sortField : BookSortField.values()) {
            if (sortField.name().equalsIgnoreCase(value) || sortField.field.equalsIgnoreCase(value)) {
                return sortField;
            }
        }
        throw new IllegalArgumentException("Неизвестное поле сортировки: " + value);
    }
}