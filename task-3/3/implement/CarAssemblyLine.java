package implement;

import interfaces.IAssemblyLine;
import interfaces.ILineStep;
import interfaces.IProduct;

public class CarAssemblyLine implements IAssemblyLine {

    private final ILineStep step1;
    private final ILineStep step2;
    private final ILineStep step3;

    public CarAssemblyLine(ILineStep step1, ILineStep step2, ILineStep step3) {
        this.step1 = step1;
        this.step2 = step2;
        this.step3 = step3;
    }

    @Override
    public IProduct assembleProduct(IProduct iProduct) {
        System.out.println("Начинаем сборку продукта");
        iProduct.installFirstPart(step1.buildProductPart());
        iProduct.installSecondPart(step2.buildProductPart());
        iProduct.installThirdPart(step3.buildProductPart());

        return iProduct;
    }
}
