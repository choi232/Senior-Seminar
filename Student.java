import java.util.ArrayList;

public class Student{

    private String name;
    private String email;
    private String time;
    private int studentID;
    private ArrayList<Integer> choice;
    private Seminar[] seminars = new Seminar[5];
    private int placability;

    public Student(String setName, String setEmail, String setTime, int setStudentID, ArrayList<Integer> setChoice){
        name = setName;
        email = setEmail;
        time = setTime;
        choice = setChoice;
        studentID = setStudentID;
    }

    public Seminar getSeminar(int index){
        return seminars[index];
    }

    public void setSeminar(int index, Seminar setSeminar){
        seminars[index] = setSeminar;
    }

    public void setPlacability(int setPlacability){
        placability = setPlacability;
    }

    public int getPlacability(){
        return placability;
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

    public int getChoice(int index){
        return choice.get(index);
    }
    public int getChoiceSize(int index){
        return choice.size();
    }

    public int getStudentID(){
        return studentID;
    }
    
}