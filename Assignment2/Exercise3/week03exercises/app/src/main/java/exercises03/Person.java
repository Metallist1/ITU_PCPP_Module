package exercises03;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Person {

    private static AtomicLong seed ;
    private final long id;
    private String name;
    private int zip;
    private String address;

    public Person() {
        synchronized (Person.class) {
            if (seed == null) {
                seed = new AtomicLong(0);
                this.id = seed.get();
            }else{
                this.id = seed.incrementAndGet();
            }
        }
    }

    public Person(long newId) {
        synchronized (Person.class) {
            if (seed == null) {
                seed = new AtomicLong(newId);
                this.id = seed.get();
            }else{
                this.id = seed.incrementAndGet();
            }
        }
    }

    public synchronized long getId() {
        return id;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized Object[] getAddress() {
        return new Object[] {zip,address};
    }

    public synchronized void change_zip_address(int newZip, String newAddress){
        this.zip = newZip;
        this.address = newAddress;
    }

    public synchronized void change_name(String newName){
        this.name = newName;
    }
}
