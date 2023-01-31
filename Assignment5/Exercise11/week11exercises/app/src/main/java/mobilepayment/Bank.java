package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.time.Duration;

public class Bank extends AbstractBehavior<Bank.BankCommand> {

    /* --- Messages ------------------------------------- */
    public interface BankCommand {
    }

    // Feel free to add message types at your convenience
    public static final class Transaction implements BankCommand {

        private final int requestId;

        private final long amount;

        private final ActorRef<Account.AccountCommand> sender;

        private final ActorRef<Account.AccountCommand> reciever;
        private final ActorRef<TransactionComplete> replyTo;

        public Transaction(int requestId, long amount, ActorRef<Account.AccountCommand> sender, ActorRef<Account.AccountCommand> reciever, ActorRef<TransactionComplete> replyTo) {
            this.requestId = requestId;
            this.amount = amount;
            this.sender = sender;
            this.reciever = reciever;
            this.replyTo = replyTo;
        }

        public long getAmount() {
            return amount;
        }

        public ActorRef<Account.AccountCommand> getSender() {
            return sender;
        }

        public ActorRef<Account.AccountCommand> getReciever() {
            return reciever;
        }

        public int getRequestId() {
            return requestId;
        }

        public ActorRef<TransactionComplete> getReplyTo() {
            return replyTo;
        }
    }

    public static final class ReadBalance implements BankCommand {

        private final int requestId;


        private final ActorRef<Account.AccountCommand> account;

        private final ActorRef<TransactionComplete> replyTo;

        public ReadBalance(int requestId,ActorRef<Account.AccountCommand> account, ActorRef<TransactionComplete> replyTo) {
            this.requestId = requestId;
            this.account = account;
            this.replyTo = replyTo;
        }


        public ActorRef<Account.AccountCommand> getAccount() {
            return account;
        }

        public int getRequestId() {
            return requestId;
        }

        public ActorRef<TransactionComplete> getReplyTo() {
            return replyTo;
        }
    }

    public static final class TransactionComplete {
        private final int requestId;

        private final long amount;

        private final ActorRef<Account.AccountCommand> account;

        private final boolean success;

        public TransactionComplete(int requestId, long amount, ActorRef<Account.AccountCommand> account, boolean success) {
            this.requestId = requestId;
            this.amount = amount;
            this.account = account;
            this.success = success;
        }

        public long getAmount() {
            return amount;
        }

        public boolean isSuccess() {
            return success;
        }

        public ActorRef<Account.AccountCommand> getAccount() {
            return account;
        }

        public int getRequestId() {
            return requestId;
        }
    }

    private static final class AdaptedResponse implements BankCommand {
        public final String message;

        public AdaptedResponse(String message) {
            this.message = message;
        }
    }

    /* --- State ---------------------------------------- */
    public final int bankId;
    private final Duration timeout = Duration.ofSeconds(3);

    /* --- Constructor ---------------------------------- */
    // Feel free to extend the contructor at your convenience


    public static Behavior<BankCommand> create(int bankId) {
        return Behaviors.setup(context -> new Bank(context, bankId));
    }


    private Bank(ActorContext<BankCommand> context, int bankId) {
        super(context);
        this.bankId = bankId;
        context.getLog().info("Bank {} started!",
                this.bankId);
    }


    /* --- Actor initial state -------------------------- */
    // To be Implemented


    /* --- Message handling ----------------------------- */
    @Override
    public Receive<BankCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(Transaction.class, this::makeTransaction)
                .onMessage(AdaptedResponse.class, this::ResponseLog)
                .onMessage(ReadBalance.class, this::readBalance)
                .build();
    }


    /* --- Handlers ------------------------------------- */
    public Behavior<BankCommand> makeTransaction(Transaction msg) {

        getContext().ask(
                Account.BalanceAdded.class,
                msg.getSender(),
                timeout,
                // construct the outgoing message
                (ActorRef<Account.BalanceAdded> ref) -> new Account.Deposit(-msg.getAmount(), msg.getRequestId(), ref),
                // adapt the response (or failure to respond)
                (response, throwable) -> {
                    if (response != null) {
                        msg.getReplyTo().tell(new TransactionComplete(msg.getRequestId(), -msg.getAmount(), msg.getSender(), true));
                        return new AdaptedResponse(msg.getRequestId() + ": Request Succeeded");
                    } else {
                        msg.getReplyTo().tell(new TransactionComplete(msg.getRequestId(), -msg.getAmount(), msg.getSender(), false));
                        return new AdaptedResponse(msg.getRequestId() + ": Request failed");
                    }
                });


        getContext().ask(
                Account.BalanceAdded.class,
                msg.getReciever(),
                timeout,
                // construct the outgoing message
                (ActorRef<Account.BalanceAdded> ref) -> new Account.Deposit(msg.getAmount(), msg.getRequestId(), ref),
                // adapt the response (or failure to respond)
                (response, throwable) -> {
                    if (response != null) {
                        msg.getReplyTo().tell(new TransactionComplete(msg.getRequestId(), msg.getAmount(), msg.getReciever(), true));
                        return new AdaptedResponse(msg.getRequestId() + ": Request Succeeded");
                    } else {
                        msg.getReplyTo().tell(new TransactionComplete(msg.getRequestId(), msg.getAmount(), msg.getReciever(), false));
                        return new AdaptedResponse(msg.getRequestId() + ": Request failed");
                    }
                });

        return this;
    }


    public Behavior<BankCommand> readBalance(ReadBalance msg) {

        getContext().ask(Account.BalanceRead.class, msg.getAccount(), timeout,
                // construct the outgoing message
                (ActorRef<Account.BalanceRead> ref) -> new Account.PrintBalance(msg.getRequestId(), ref),
                // adapt the response (or failure to respond)
                (response, throwable) -> {
                    if (response != null) {
                        msg.getReplyTo().tell(new TransactionComplete(response.getRequestId(), response.getBalance(), msg.getAccount(), true));
                        return new AdaptedResponse("Request:" + response.getRequestId() + ", Balance: " + response.getBalance());
                    } else {
                        msg.getReplyTo().tell(new TransactionComplete(msg.getRequestId(), -1, msg.getAccount(), false));
                        return new AdaptedResponse(msg.getRequestId() + ": Request failed");
                    }
                });

        return this;
    }

    public Behavior<BankCommand> ResponseLog(AdaptedResponse msg) {
        //getContext().getLog().info("Bank: {} Got response from Account: {}", bankId, msg.message);
        return this;
    }
}
