package ebookstore.model.enums;

public enum RequestSortField {

    TITLE("title"),
    COUNT("requestCount");

    private final String field;

    RequestSortField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public static RequestSortField fromString(String value) {
        for (RequestSortField sortField : RequestSortField.values()) {
            if (sortField.name().equalsIgnoreCase(value) || sortField.field.equalsIgnoreCase(value)) {
                return sortField;
            }
        }
        throw new IllegalArgumentException("Неизвестное поле сортировки: " + value);
    }
}