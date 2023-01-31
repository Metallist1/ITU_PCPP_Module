package mathsserver;

// Hint: The imports below may give you hints for solving the exercise.
//       But feel free to change them.

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.ChildFailed;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.*;

import java.util.*;
import java.util.stream.IntStream;

import akka.japi.Pair;
import mathsserver.Task;
import mathsserver.Task.BinaryOperation;

public class Server extends AbstractBehavior<Server.ServerCommand> {
    /* --- Messages ------------------------------------- */
    public interface ServerCommand {
    }

    public static final class ComputeTasks implements ServerCommand {
        public final List<Task> tasks;
        public final ActorRef<Client.ClientCommand> client;

        public ComputeTasks(List<Task> tasks,
                            ActorRef<Client.ClientCommand> client) {
            this.tasks = tasks;
            this.client = client;
        }
    }

    public static final class WorkDone implements ServerCommand {
        ActorRef<Worker.WorkerCommand> worker;

        public WorkDone(ActorRef<Worker.WorkerCommand> worker) {
            this.worker = worker;
        }
    }

    /* --- State ---------------------------------------- */

    private Queue<ActorRef<Worker.WorkerCommand>> idleWorkers;
    private Queue<ActorRef<Worker.WorkerCommand>> busyWorkers;
    private Queue<Pair<Task, ActorRef<Client.ClientCommand>>> tasksToDo;
    private final int minWorkers;
    private final int maxWorkers;


    /* --- Constructor ---------------------------------- */
    private Server(ActorContext<ServerCommand> context,
                   int minWorkers, int maxWorkers) {
        super(context);
        idleWorkers = new LinkedList<>();
        busyWorkers = new LinkedList<>();
        tasksToDo = new LinkedList<>();
        this.minWorkers = minWorkers;
        this.maxWorkers = maxWorkers;
        IntStream
                .range(1, minWorkers + 1)
                .forEach((workerId) -> {

                    ActorRef<Worker.WorkerCommand> worker = generateWorker( "worker_" + workerId);
                    idleWorkers.add(worker);

                });
    }


    /* --- Actor initial state -------------------------- */
    public static Behavior<ServerCommand> create(int minWorkers, int maxWorkers) {
        return Behaviors.setup(context -> new Server(context, minWorkers, maxWorkers));
    }


    /* --- Message handling ----------------------------- */
    @Override
    public Receive<ServerCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ComputeTasks.class, this::onComputeTasks)
                .onMessage(WorkDone.class, this::onWorkDone)
                .onSignal(ChildFailed.class, this::onChildFailed)
                .onSignal(Terminated.class, this::onTerminated)
                .build();
    }


    /* --- Handlers ------------------------------------- */
    public Behavior<ServerCommand> onComputeTasks(ComputeTasks msg) {

        for (Task task : msg.tasks) {
            tasksToDo.add(Pair.create(task, msg.client));
        }

        while (!tasksToDo.isEmpty()) {
            if (!idleWorkers.isEmpty()) {
                ActorRef<Worker.WorkerCommand> worker = idleWorkers.remove();
                busyWorkers.add(worker);
                assignWork(worker);

            } else if (busyWorkers.size() < maxWorkers) {
                ActorRef<Worker.WorkerCommand> worker = generateWorker( "worker_" + (busyWorkers.size() + 1));
                idleWorkers.add(worker);
            } else {
                break;
            }
        }
        return this;
    }

    public Behavior<ServerCommand> onWorkDone(WorkDone msg) {

        if (!tasksToDo.isEmpty()) {
            assignWork(msg.worker);
        } else if (busyWorkers.size() + idleWorkers.size() > minWorkers) {
            busyWorkers.remove(msg.worker);
            msg.worker.tell(new Worker.Stop());
        } else {
            busyWorkers.remove(msg.worker);
            idleWorkers.add(msg.worker);
        }

        return this;
    }

    public Behavior<ServerCommand> onChildFailed(ChildFailed msg) {
        ActorRef<Void> crashedChild = msg.getRef();
        busyWorkers.remove(msg.getRef());

        ActorRef<Worker.WorkerCommand> worker = generateWorker( crashedChild.path().name());

        if (!tasksToDo.isEmpty()) {
            assignWork(worker);
            busyWorkers.add(worker);
        } else if (busyWorkers.size() + idleWorkers.size() < minWorkers) {
            idleWorkers.add(worker);
        }
        return this;
    }


    public Behavior<ServerCommand> onTerminated(Terminated msg) {
        return this;
    }

    private ActorRef<Worker.WorkerCommand>  generateWorker(String name){
        final ActorRef<Worker.WorkerCommand> worker =
                getContext().spawn(Worker.create(getContext().getSelf()), name);
        getContext().watch(worker);
        return worker;
    }

    private void assignWork(ActorRef<Worker.WorkerCommand> worker){
        Pair<Task, ActorRef<Client.ClientCommand>> p = tasksToDo.remove();
        worker.tell(new Worker.ComputeTask(p.first(), p.second()));
    }
}
