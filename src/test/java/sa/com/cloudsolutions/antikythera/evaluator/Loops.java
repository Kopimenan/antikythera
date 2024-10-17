package sa.com.cloudsolutions.antikythera.evaluator;

import java.util.List;

public class Loops {
    public static void main(String[] args) {
        Loops loops = new Loops();
        loops.forLoop();
    }

    private void forLoop() {
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
        }
    }

    private void forLoopWithBreak() {
        for (int i = 0; i < 20; ++i) {
            if (i == 10) {
                break;
            }
            System.out.println(i);
        }
    }

    private void whileLoop() {
        int i = 0;
        while (i < 10) {
            System.out.println(i);
            i++;
        }
    }

    private void whileLoopWithBreak() {
        int i = 0;
        while (i < 20) {
            System.out.println(i);
            i++;
            if(i == 10) {
                break;
            }
        }
    }

    private void doWhileLoop() {
        int i = 0;
        do {
            System.out.println(i);
            i++;
        } while (i < 10);
    }

    private void forEachLoop() {
        int[] numbers = {1, 2, 3, 4, 5};
        for (int number : numbers) {
            System.out.println(number);
        }
    }

    private void forEach() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        numbers.forEach(System.out::println);
    }
}