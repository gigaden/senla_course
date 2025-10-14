import util.Finder;
import util.RandomNum;

public class Main {

    public static void main(String[] args) {

        int num = RandomNum.generateThreeDigitNum();
        int result = Finder.findMaxNum(num);

        System.out.println(result);
    }
}
