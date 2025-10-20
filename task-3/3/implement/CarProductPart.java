package implement;

import interfaces.IProductPart;

public class CarProductPart implements IProductPart {
    private final String name;

    public CarProductPart(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CarProductPart{" +
                "name='" + name + '\'' +
                '}' + '\n';
    }

    public String getName() {
        return name;
    }
}
