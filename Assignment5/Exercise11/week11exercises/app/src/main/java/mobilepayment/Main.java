package mobilepayment;

import akka.actor.typed.ActorSystem;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        // start actor system
        final ActorSystem<Guardian.GuardianCommand> guardian =
                ActorSystem.create(Guardian.create(), "guardian_actor");

        // init message
        guardian.tell(new Guardian.Start());

        // init message
        // To be implemented

        // wait until user presses enter
        try {
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException e) {
            System.out.println("Error " + e.getMessage());
            e.printStackTrace();
        } finally {
            // terminate actor system execution
            guardian.terminate();
        }

    }

}
