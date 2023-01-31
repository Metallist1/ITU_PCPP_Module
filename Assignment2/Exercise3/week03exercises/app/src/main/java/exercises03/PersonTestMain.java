package exercises03;

public class PersonTestMain {
    public PersonTestMain() {

        //Create x amount of new threads
        final int numReadersWriters = 5;

        for (int i = 0; i < numReadersWriters; i++) {

            // create a person and test out the names
            new Thread(() -> {
                Person newPerson = new Person(25);
                System.out.println("I am " + newPerson.getId());
                newPerson.change_name(newPerson.getId() + " man");
                System.out.println("my name is :  " + newPerson.getName());
            }).start();


        }

        //create a person using alt constructor and test out the zip and address.
        for (int i = 0; i < numReadersWriters; i++) {

            // start a consumer
            new Thread(() -> {
                Person newPerson = new Person(25);
                System.out.println("I am new " + newPerson.getId());

                newPerson.change_zip_address(777, newPerson.getId() + " 777 street");
                System.out.println("my address is :  " + newPerson.getAddress() [0] + " " + newPerson.getAddress()[1]);

                newPerson.change_zip_address(788,  newPerson.getId() + " 788 street");
                System.out.println("my new address is :  " + newPerson.getAddress() [0] + " " + newPerson.getAddress()[1]);
            }).start();


        }
    }

    public static void main(String[] args) {
        new PersonTestMain();
    }

}
