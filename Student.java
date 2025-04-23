
public class Student{

    private String name;
    private String email;
    private String time;
    private int studentID;
    private int[] choice = new int[5];
    private Seminar[] placedSeminars = new Seminar[5];
    private int placability;

    public Student(String setName, String setEmail, String setTime, int setStudentID, int[] setChoice){
        name = setName;
        email = setEmail;
        time = setTime;
        choice = setChoice;
        studentID = setStudentID;
        // Seminar dummy = new Seminar("N/A", -1, "N/A", -1);
        // for(int i = 0; i < 5; i++){
        //     placedSeminars[i] = dummy;
        // }
    }

    public Seminar[] getPlacedSeminars(){
        return placedSeminars;
    }
    public Seminar getPlacedSeminar(int index){
        return placedSeminars[index];
    }

    public void setPlacedSeminar(int index, Seminar setSeminar){
        placedSeminars[index] = setSeminar;
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

    public int[] getChoice(){
        return choice;
    }

    public int getStudentID(){
        return studentID;
    }
}