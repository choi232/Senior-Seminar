
public class Student{

    private String name;
    private String email;
    private int time;
    private int[] choice = new int[5];

    public Student(String setName, String setEmail, int setTime, int[] setChoice){
        name = setName;
        email = setEmail;
        time = setTime;
        choice = setChoice;
    }
}