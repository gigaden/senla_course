package implement;

import interfaces.ILineStep;
import interfaces.IProductPart;

public class CarLineStep implements ILineStep {

    private final String partName;

    public CarLineStep(String partName) {
        this.partName = partName;
    }

    public String getPartName() {
        return partName;
    }

    @Override
    public IProductPart buildProductPart() {
        System.out.printf("Создаём часть продукта: %s\n", partName);
        return new CarProductPart(partName);
    }
}
