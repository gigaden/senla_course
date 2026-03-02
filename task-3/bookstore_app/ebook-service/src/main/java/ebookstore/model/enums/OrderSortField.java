package ebookstore.model.enums;

public enum OrderSortField {

    PRICE("price"),
    DATE("createdOn"),
    STATUS("orderStatus");

    private final String field;

    OrderSortField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public static OrderSortField fromString(String value) {
        for (OrderSortField sortField : OrderSortField.values()) {
            if (sortField.name().equalsIgnoreCase(value) || sortField.field.equalsIgnoreCase(value)) {
                return sortField;
            }
        }
        throw new IllegalArgumentException("Неизвестное поле сортировки: " + value);
    }
}