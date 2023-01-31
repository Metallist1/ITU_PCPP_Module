package exercises09;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

import exercises09.stopwatchUI;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/* This example is inspired by the StopWatch app in Head First. Android Development
   http://shop.oreilly.com/product/0636920029045.do
   Modified to Java, October 2020 by Jørgen Staunstrup, ITU, jst@itu.dk */

public class StopwatchRx {
    public static void main(String[] args) {
        new StopwatchRx();
    }

    final static JFrame f = new JFrame("Stopwatch");

    //Setting up the three streams for the Buttons and the display
    final static stopwatchUI myUI = new stopwatchUI(0, f);

    public StopwatchRx() {
        f.setBounds(0, 0, 420, 220);
        f.setLayout(null);
        f.setVisible(true);

        timer.subscribe(display);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    //Observable simulating clock ticking every second
    final static Observable<Integer> timer
            = Observable.create(new ObservableOnSubscribe<Integer>() {
        @Override
        public void subscribe(ObservableEmitter<Integer> e) throws Exception {
            new Thread() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            TimeUnit.MILLISECONDS.sleep(100);
                            e.onNext(1);
                        }
                    } catch (java.lang.InterruptedException e) {
                        System.out.println(e.toString());
                    }
                }
            }.start();
        }
    });

    // Observer updating the display
    final static Observer<Integer> display = new Observer<Integer>() {
        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onNext(Integer value) {
            myUI.updateTime();
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
}
