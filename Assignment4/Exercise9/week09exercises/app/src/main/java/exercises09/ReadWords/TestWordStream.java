package exercises09.ReadWords;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import java.io.*;

public class TestWordStream {
    public static void main(String[] args) {

        //readWords.subscribe(display);

        System.out.println("Character Exercises");

        characterExercise();

        System.out.println("Palindrome and parallel");

        palindromeExercise();


    }

    public static void characterExercise() {
        System.out.println("First 100 words");
        readWords.take(100).subscribe(display);
        System.out.println("All words with minimum 22 characters");
        readWords.filter(x -> x.length() >= 22).subscribe(display);
        System.out.println("One word with minimum of 22 characters");
        //It's enough to find the first word.
        readWords.filter(x -> x.length() >= 22).take(1).subscribe(display);
    }

    public static void palindromeExercise() {
        readWords.filter(x -> isPalindrome(x)).subscribe(display);
    }


    static Observable<String> readWords = Observable.create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> e) throws Exception {
            try {
                //Alternative to get the path to resource file
                File ourFile = new File(TestWordStream.class.getClassLoader().getResource("english-words.txt").getFile());
                final String filename = ourFile.getAbsolutePath();

                BufferedReader reader = new BufferedReader(new FileReader(filename));
                String next = reader.readLine();
                while (next != null){
                    e.onNext(next);
                    next = reader.readLine();
                }
            } catch (IOException exn) {
                System.out.println(exn);
            }
        }
    });

    final static Observer<String> display = new Observer<String>() {
        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onNext(String value) {
            System.out.println(value);
        }

        @Override
        public void onError(Throwable e) {
            System.out.println("onError: ");
        }

        @Override
        public void onComplete() {
            System.out.println("onComplete: All Done!");
        }
    };

    public static boolean isPalindrome(String s) {
        String rev = "";

        boolean ans = false;

        for (int i = s.length() - 1; i >= 0; i--) {
            rev = rev + s.charAt(i);
        }

        if (s.equals(rev)) {
            ans = true;
        }
        return ans;
    }

}
