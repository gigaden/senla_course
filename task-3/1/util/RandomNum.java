package util;

import java.util.Random;

public final class RandomNum {

    public static int generateThreeDigitNum() {
        int min = 100;
        int max = 999;
        return (new Random().nextInt(max - min + 1) + min);
    }
}
