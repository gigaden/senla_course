package util;

public final class Finder {

    public static int findMaxNum(int num) {
        num = Math.abs(num);
        int result = num % 10;
        int tmp;

        while (num != 0) {
            tmp = num % 10;
            if (tmp > result) {
                result = tmp;
            }
            num /= 10;
        }

        return result;
    }
}
