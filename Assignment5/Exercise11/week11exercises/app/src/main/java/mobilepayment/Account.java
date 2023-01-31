package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Account extends AbstractBehavior<Account.AccountCommand> {

    /* --- Messages ------------------------------------- */
    public interface AccountCommand { }

    public static final class Deposit implements AccountCommand {
        private final long amount;
        private final int requestId;
        private final ActorRef<BalanceAdded> replyTo;

        public Deposit(long amount, int requestId, ActorRef<BalanceAdded> replyTo) {
            this.amount = amount;
            this.requestId = requestId;
            this.replyTo = replyTo;
        }

        public long getAmount() {
            return amount;
        }

        public int getRequestId() {
            return requestId;
        }

        public ActorRef<BalanceAdded> getReplyTo() {
            return replyTo;
        }
    }

    public static final class PrintBalance implements AccountCommand {

        private final int requestId;
        private final ActorRef<BalanceRead> replyTo;

        public PrintBalance(int requestId, ActorRef<BalanceRead> replyTo) {
            this.requestId = requestId;
            this.replyTo = replyTo;
        }

        public int getRequestId() {
            return requestId;
        }

        public ActorRef<BalanceRead> getReplyTo() {
            return replyTo;
        }
    }

    public static final class BalanceAdded {
        private final int requestId;

        public BalanceAdded(int requestId) {
            this.requestId = requestId;
        }

        public int getRequestId() {
            return requestId;
        }
    }

    public static final class BalanceRead {
        private final int requestId;

        private final Long balance;

        public BalanceRead(int requestId, Long balance) {
            this.requestId = requestId;
            this.balance = balance;
        }

        public int getRequestId() {
            return requestId;
        }

        public Long getBalance() {
            return balance;
        }
    }
    // Feel free to add message types at your convenience


    /* --- State ---------------------------------------- */
    private final int accountId;
    private long balance;


    /* --- Constructor ---------------------------------- */
    // Feel free to extend the contructor at your convenience
    public static Behavior<AccountCommand> create(int accountId, Long balance) {
        return Behaviors.setup(context -> new Account(context, accountId, balance));
    }
    private Account(ActorContext<AccountCommand> context, int accountId, Long balance) {
        super(context);
        this.accountId = accountId;
        this.balance = balance;
        context.getLog().info("Account {} started! Current Balance {}",
                this.accountId, this.balance);
    }


    /* --- Actor initial state -------------------------- */
    @Override
    public Receive<AccountCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(Deposit.class, this::depositToAccount)
                .onMessage(PrintBalance.class, this::readBalance)
                .build();
    }


    /* --- Message handling ----------------------------- */
    // To be Implemented


    /* --- Handlers ------------------------------------- */
    public Behavior<AccountCommand> depositToAccount(Deposit msg) {
        balance = balance + msg.getAmount();
        msg.getReplyTo().tell(new BalanceAdded(msg.getRequestId()));
        return this;
    }

    public Behavior<AccountCommand> readBalance(PrintBalance msg) {
        msg.getReplyTo().tell(new BalanceRead(msg.getRequestId(), balance));
        return this;
    }
}
