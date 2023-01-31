package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

// Hint: You may generate random numbers using Random::ints
import java.time.Duration;
import java.util.Random;
import java.util.stream.IntStream;

public class MobileApp extends AbstractBehavior<MobileApp.MobileAppCommand> {

    /* --- Messages ------------------------------------- */
    public interface MobileAppCommand {
    }
    // Feel free to add message types at your convenience

    public static final class CreateTransactions implements MobileAppCommand {

        private final ActorRef<Account.AccountCommand> account1;
        private final ActorRef<Account.AccountCommand> account2;
        private final ActorRef<Bank.BankCommand> bank;

        public CreateTransactions(ActorRef<Account.AccountCommand> account1, ActorRef<Account.AccountCommand> account2, ActorRef<Bank.BankCommand> bank) {
            this.account1 = account1;
            this.account2 = account2;
            this.bank = bank;
        }

        public ActorRef<Account.AccountCommand> getAccount1() {
            return account1;
        }

        public ActorRef<Account.AccountCommand> getAccount2() {
            return account2;
        }

        public ActorRef<Bank.BankCommand> getBank() {
            return bank;
        }
    }


    public static final class ReadBalance implements MobileAppCommand {

        private final ActorRef<Account.AccountCommand> account1;
        private final ActorRef<Bank.BankCommand> bank;

        public ReadBalance(ActorRef<Account.AccountCommand> account1, ActorRef<Bank.BankCommand> bank) {
            this.account1 = account1;
            this.bank = bank;
        }

        public ActorRef<Account.AccountCommand> getAccount1() {
            return account1;
        }

        public ActorRef<Bank.BankCommand> getBank() {
            return bank;
        }
    }


    public static final class TestTransactions implements MobileAppCommand {

        private final ActorRef<Account.AccountCommand> account1;
        private final ActorRef<Account.AccountCommand> account2;
        private final ActorRef<Bank.BankCommand> bank;

        public TestTransactions(ActorRef<Account.AccountCommand> account1, ActorRef<Account.AccountCommand> account2, ActorRef<Bank.BankCommand> bank) {
            this.account1 = account1;
            this.account2 = account2;
            this.bank = bank;
        }

        public ActorRef<Account.AccountCommand> getAccount1() {
            return account1;
        }

        public ActorRef<Account.AccountCommand> getAccount2() {
            return account2;
        }

        public ActorRef<Bank.BankCommand> getBank() {
            return bank;
        }
    }

    private static final class AdaptedResponse implements MobileAppCommand {
        public final String message;

        public AdaptedResponse(String message) {
            this.message = message;
        }
    }

    /* --- State ---------------------------------------- */
    private int transactionId = 0;
    private Random rand = new Random();
    private final Duration timeout = Duration.ofSeconds(3);


    /* --- Constructor ---------------------------------- */
    // Feel free to extend the contructor at your convenience
    private MobileApp(ActorContext context) {
        super(context);
        context.getLog().info("Mobile app {} started!",
                context.getSelf().path().name());
    }


    /* --- Actor initial state -------------------------- */
    public static Behavior<MobileApp.MobileAppCommand> create() {
        return Behaviors.setup(MobileApp::new);
        // You may extend the constructor if necessary
    }


    /* --- Message handling ----------------------------- */
    @Override
    public Receive<MobileAppCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(TestTransactions.class, this::testTransactions)
                .onMessage(CreateTransactions.class, this::generateTransactions)
                .onMessage(ReadBalance.class, this::readBalance)
                .onMessage(AdaptedResponse.class, this::RecordResponse)
                .build();
    }



    /* --- Handlers ------------------------------------- */

    public Behavior<MobileAppCommand> testTransactions(TestTransactions msg) {


        transactionId = transactionId + 1;

        getContext().ask(
                Bank.TransactionComplete.class,
                msg.getBank(),
                timeout,
                // construct the outgoing message
                (ActorRef<Bank.TransactionComplete> ref) -> new Bank.Transaction(transactionId, 1000, msg.getAccount1(), msg.getAccount2(), ref),
                // adapt the response (or failure to respond)
                (response, throwable) -> {
                    if (response != null) {
                        return new AdaptedResponse("Request" + response.getRequestId() + " Responded with  " + response.isSuccess());
                    } else {
                        return new AdaptedResponse(transactionId + ": Request failed");
                    }
                });

        transactionId = transactionId + 1;
        getContext().ask(
                Bank.TransactionComplete.class,
                msg.getBank(),
                timeout,
                // construct the outgoing message
                (ActorRef<Bank.TransactionComplete> ref) -> new Bank.Transaction(transactionId, 1000, msg.getAccount2(), msg.getAccount1(), ref),
                // adapt the response (or failure to respond)
                (response, throwable) -> {
                    if (response != null) {
                        return new AdaptedResponse("Request" + response.getRequestId() + " Responded with  " + response.isSuccess());
                    } else {
                        return new AdaptedResponse(transactionId + ": Request failed");
                    }
                });

        return this;
    }


    public Behavior<MobileAppCommand> generateTransactions(CreateTransactions msg) {

        IntStream.range(0, 100)
                .forEach(i -> {
                    transactionId = transactionId + 1;
                    getContext().ask(
                            Bank.TransactionComplete.class,
                            msg.getBank(),
                            timeout,
                            // construct the outgoing message
                            (ActorRef<Bank.TransactionComplete> ref) -> new Bank.Transaction(transactionId, rand.nextInt((1000 - 1) + 1) + 1, msg.getAccount1(), msg.getAccount2(), ref),
                            // adapt the response (or failure to respond)
                            (response, throwable) -> {
                                if (response != null) {
                                    return new AdaptedResponse("Request" + response.getRequestId() + " Responded with  " + response.isSuccess());
                                } else {
                                    return new AdaptedResponse(transactionId + ": Request failed");
                                }
                            });
                });

        return this;
    }


    public Behavior<MobileAppCommand> readBalance(ReadBalance msg) {

        transactionId = transactionId + 1;
        getContext().ask(
                Bank.TransactionComplete.class,
                msg.getBank(),
                timeout,
                // construct the outgoing message
                (ActorRef<Bank.TransactionComplete> ref) -> new Bank.ReadBalance(transactionId, msg.getAccount1(), ref),
                // adapt the response (or failure to respond)
                (response, throwable) -> {
                    if (response != null) {
                        return new AdaptedResponse("Request" + response.getRequestId() + " Responded with  " + response.getAmount());
                    } else {
                        return new AdaptedResponse(transactionId + ": Request failed");
                    }
                });

        return this;
    }

    public Behavior<MobileAppCommand> RecordResponse(AdaptedResponse msg) {
        getContext().getLog().info("Got response from Bank: {}", msg.message);
        return this;
    }
}
