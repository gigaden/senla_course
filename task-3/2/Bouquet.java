import entity.Flower;
import service.BouquetService;

import java.util.List;

public class Bouquet {

    public static void main(String[] args) {

        List<Flower> bouquet = BouquetService.generateBouquet(7);
        double bouquetCost = BouquetService.findBouquetCost(bouquet);

        BouquetService.printBouquet(bouquet, bouquetCost);
    }
}
