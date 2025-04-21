
public class Student{

    private String name;
    private String email;
    private String time;
    private int[] choice = new int[5];

    public Student(String setName, String setEmail, String setTime, int[] setChoice){
        name = setName;
        email = setEmail;
        time = setTime;
        choice = setChoice;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getTime(){
        return time;
    }

    public int[] getChoice(){
        return choice;
    }
}