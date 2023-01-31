// For week07
// raup@itu.dk * 26/09/2021
// jst@itu.dk * 22/09/2022
package exercises07;

import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

class FunctionExamples {
    public static void main(String[] args) {
        new FunctionExamples();
    }

    public FunctionExamples() {
        // Define a function f : Integer -> Integer
        Function<Integer, Integer> f1 = (x) -> x + 1;
        System.out.println(f1.apply(1));

        // Define a function f : "void" -> Integer
        Supplier<Integer> f2 = () -> 2;
        System.out.println(f2.get());


        // Define a function f : Integer X Integer -> "void"
        BiConsumer<Integer, Integer> f3 = (x, y) -> {
            int z = x + y;
            System.out.println(z);
        };
        f3.accept(1, 2);

        // Define a function f : "void" -> "void"
        Runnable f4 = () -> System.out.println("text");
        f4.run();
    }
}
