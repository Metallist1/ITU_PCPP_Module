package exercises09.RxExamples;

import io.reactivex.Flowable;
public class TestRx {
    public static void main(String[] args) {
        Flowable.just("Hello World!").subscribe(System.out::println);
    }
}