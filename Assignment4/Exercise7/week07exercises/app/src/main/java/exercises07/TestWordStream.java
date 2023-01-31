// Week 3
// sestoft@itu.dk * 2015-09-09
package exercises07;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

import static exercises07.PrimeCountingPerf.Mark7;

public class TestWordStream {
    public static void main(String[] args) {
        //Alternative to get the path to resource file
        File ourFile = new File(TestWordStream.class.getClassLoader().getResource("english-words.txt").getFile());
        final String filename = ourFile.getAbsolutePath();

        final String urlLink = "https://staunstrups.dk/jst/english-words.txt";

        System.out.println("Total word count: " + readWords(filename).count());

        characterExercise(filename);

        System.out.println("Palindrome and parallel");

        palindromeExercise(filename);

        System.out.println("Measure speed of both palindromes");

        measureSpeed(filename);

        System.out.println("Get data from the internet");

        getStatistics(urlLink);

    }

    public static void characterExercise(String filename) {
        System.out.println("First 100 words");
        readWords(filename).limit(100).forEach(System.out::println);
        System.out.println("All words with minimum 22 characters");
        readWords(filename).filter(x -> x.length() >= 22).forEach(System.out::println);
        System.out.println("One word with minimum of 22 characters");
        //It's enough to find the first word.
        readWords(filename).filter(x -> x.length() >= 22).findFirst().ifPresent(System.out::println);
    }

    public static void getStatistics(String filename) {

        System.out.println("Total word count: " + readWordsStream(filename).count());

        readWordsStream(filename).map(String::length).min(Integer::compare).
                ifPresent((v) -> System.out.println("Minimum length: " + v));
        readWordsStream(filename).map(String::length).max(Integer::compare).
                ifPresent((v) -> System.out.println("Maximum length: " + v));
        readWordsStream(filename).map(String::length).mapToInt(Integer::intValue).average().
                ifPresent((v) -> System.out.println("Average length: " + v));
    }

    public static void palindromeExercise(String filename) {

        readWords(filename).filter(x -> isPalindrome(x)).forEach(System.out::println);

        readWords(filename).parallel().filter(x -> isPalindrome(x)).forEach(System.out::println);
    }


    public static void measureSpeed(String filename) {
        Mark7("Sequential", i -> readWords(filename).filter(x -> isPalindrome(x)).count());

        Mark7("Parallel", i -> readWords(filename).parallel().filter(x -> isPalindrome(x)).count());
    }

    public static Stream<String> readWords(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            return reader.lines();
        } catch (IOException exn) {
            System.out.println(exn);
            return Stream.<String>empty();
        }
    }

    public static Stream<String> readWordsStream(String Url) {
        try {
            URL oracle = new URL(Url);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(oracle.openStream()));
            return in.lines();
        } catch (IOException exn) {
            System.out.println(exn);
            return Stream.<String>empty();
        }
    }


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

    public static Map<Character, Integer> letters(String s) {
        Map<Character, Integer> res = new TreeMap<>();
        // TO DO: Implement properly
        return res;
    }
}
