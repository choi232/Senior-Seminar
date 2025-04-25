import java.util.ArrayList;

public class Student{

    private String name;
    private String email;
    private String time;
    private int studentID;
    private ArrayList<Integer> choice;
    private Seminar[] seminars = new Seminar[5];
    private int seminarsIndex = 0;
    private int placability = 0;

    public Student(String setName, String setEmail, String setTime, int setStudentID, ArrayList<Integer> setChoice){
        name = setName;
        email = setEmail;
        time = setTime;
        choice = setChoice;
        studentID = setStudentID;
    }
    public int getSeminarsIndex(){
        return seminarsIndex;
    }
    public void incrementSeminarsIndex(){
        seminarsIndex++;
    }
    public Seminar getSeminar(int index){
        return seminars[index];
    }

    public void addSeminar(Seminar setSeminar){
        seminars[seminarsIndex] = setSeminar;
        seminarsIndex++;

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
    public void setChoice(int index, int val){
        choice.set(index, val);
    }
    public int getChoice(int index){
        return choice.get(index);
    }
    public int getChoiceSize(){
        return choice.size();
    }

    public int getStudentID(){
        return studentID;
    }
    
}