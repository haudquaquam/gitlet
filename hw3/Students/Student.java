package Students;

public class Student {

    String name;
    int age;

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public static void motto() {
        System.out.println("Woohoo!");
    }

    public void study() {
        System.out.println("Time to read!");
    }

    public void greet(Student s) {
        System.out.println("Hi fellow student, I'm " + name);
    }

    public void playGame() {
        System.out.print("I love Clash Royale!");
    }

    public static void main(String[] args) {

        Student dominic = new Student("Dominic", 19);
        Student dexter = new BerkeleyStudent("Dexter", 21);
        Student shirley = new StanfordStudent("Shirley", 20);
        BerkeleyStudent grace = new CS61BStudent("Grace", 20);
        CS61BStudent kyle = new CS61BStudent("Kyle", 19);
        StanfordStudent vidya = new StanfordStudent("Vidya", 20);

       // ((CS61BStudent) shirley).study();

      //  ((BerkeleyStudent) dominic).playGame();

       // ((Student) dexter).playGame();


        System.out.println(((BerkeleyStudent) kyle).favClassAtCal);


    }

}