package service;

import entity.Chamomile;
import entity.Flower;
import entity.Lotus;
import entity.Rose;
import entity.Snowdrop;
import entity.Tulip;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BouquetService {

    private static final List<Flower> flowers = List.of(
            new Rose(100),
            new Lotus(300),
            new Chamomile(80),
            new Snowdrop(150),
            new Tulip(90)
    );

    public static List<Flower> generateBouquet(int numOfFlowers) {
        Random rnd = new Random();
        List<Flower> bouquet = new ArrayList<>();
        for (int i = 0; i < numOfFlowers; i++) {
            int num = rnd.nextInt(flowers.size());
            bouquet.add(flowers.get(num));
        }

        return bouquet;
    }

    public static double findBouquetCost(List<Flower> bouquet) {
        double sum = 0;

        for (Flower flower : bouquet) {
            sum += flower.getCost();
        }

        return sum;
    }

    public static void printBouquet(List<Flower> bouquet, double bouquetCost) {
        System.out.printf("Ваш букет состоит из %d цветов, в него входит:\n", bouquet.size());
        for (Flower flower : bouquet) {
            System.out.println(flower.getName());
        }
        System.out.printf("Его стоимость: %.2f", bouquetCost);
    }
}
