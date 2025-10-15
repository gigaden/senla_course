import implement.Car;
import implement.CarAssemblyLine;
import implement.CarLineStep;
import interfaces.IAssemblyLine;
import interfaces.IProduct;

public class BuildProduct {
    public static void main(String[] args) {
        IAssemblyLine carAssemblyLine = new CarAssemblyLine(
                new CarLineStep("Кузов"),
                new CarLineStep("Шасси"),
                new CarLineStep("Двигатель")
        );

        IProduct iProduct = carAssemblyLine.assembleProduct(new Car());
        System.out.println(iProduct.toString());
    }
}
