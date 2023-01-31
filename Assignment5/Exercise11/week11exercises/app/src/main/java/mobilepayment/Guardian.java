package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.time.Duration;

public class Guardian extends AbstractBehavior<Guardian.GuardianCommand> {

    /* --- Messages ------------------------------------- */
    public interface GuardianCommand {
    }

    public static final class Start implements GuardianCommand { }

    // Feel free to add message types at your convenience

    /* --- State ---------------------------------------- */
    // empty


    /* --- Constructor ---------------------------------- */
    private Guardian(ActorContext<GuardianCommand> context) {
        super(context);
    }


    /* --- Actor initial state -------------------------- */
    public static Behavior<GuardianCommand> create() {
        return Behaviors.setup(Guardian::new);
    }


    /* --- Message handling ----------------------------- */
    @Override
    public Receive<GuardianCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(Start.class, this::startGuardian)
                .build();
    }


    /* --- Handlers ------------------------------------- */
    public Behavior<GuardianCommand> startGuardian(Start msg) {

        ActorRef<MobileApp.MobileAppCommand> mobileApp1 = getContext().spawn(MobileApp.create(), "mobile_actor_1");

        ActorRef<MobileApp.MobileAppCommand> mobileApp2 = getContext().spawn(MobileApp.create(), "mobile_actor_2");

        ActorRef<Bank.BankCommand> bank1 = getContext().spawn(Bank.create(1), "bank_actor_1");

        ActorRef<Bank.BankCommand> bank2 = getContext().spawn(Bank.create(2), "bank_actor_2");

        ActorRef<Account.AccountCommand> account1 = getContext().spawn(Account.create(1, 1000L), "account_actor_1");

        ActorRef<Account.AccountCommand> account2 = getContext().spawn(Account.create(2, 1000L), "account_actor_2");


        //mobileApp1.tell(new MobileApp.TestTransactions(account1,account2,bank1));

        //mobileApp2.tell(new MobileApp.TestTransactions(account2,account1,bank2));

        mobileApp1.tell(new MobileApp.CreateTransactions(account1,account2,bank1));

        mobileApp1.tell(new MobileApp.ReadBalance(account1,bank1));

        mobileApp1.tell(new MobileApp.ReadBalance(account2,bank1));




        return this;
    }
}

