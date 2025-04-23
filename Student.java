
public class Student{

    private String name;
    private String email;
    private String time;
    private int studentID;
    private int[] choice = new int[5];
    private Seminar[] seminars = new Seminar[5];

    public Student(String setName, String setEmail, String setTime, int setStudentID, int[] setChoice){
        name = setName;
        email = setEmail;
        time = setTime;
        choice = setChoice;
        studentID = setStudentID;
    }

    public Seminar getSeminar(int index){
        return seminars[index];
    }

    public void setSeminar(int index, Seminar setSeminarVar){
        seminars[index] = setSeminarVar;
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

    public int getStudentID(){
        return studentID;
    }
}