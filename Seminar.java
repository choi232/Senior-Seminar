/** 
 * Seminar.java File for creating Seminar objects in Senior Seminar
 * @author Mikael Choi 
 * @since 4/26/2025
 * Preconditions: CSV file data to create Seminar objects
 * Postconditions: Creates a seminar object with getter and setter methods as well as important attributes
 * Purpose: Create a blueprint of attributes and methods for the 
 * seminar object to better store data about seminars
 * **/
public class Seminar {
    private String sessionName;
    private int sessionID;
    private String presenterName;

    private int placability = 16;
    private int numEnrolled = 0;
    private Student[] students = new Student[16];
    private int studentsIndex = 0;
    //SessionID of other duplicate session
    private int duplicate = -1;
    //Boolean flag to tell if the class has been cut or not
    private boolean isCut = false;
    private boolean isFull = false;

    private int random;

    //Constructor to create seminar object with sessionName, sessionID, and presenterName
    public Seminar(String setSessionName, int setSessionID, String setPresenterName){
        sessionName = setSessionName;
        sessionID = setSessionID;
        presenterName = setPresenterName;
    }
    public void resetStudents(){
        studentsIndex = 0;
        for(int i = 0; i < 16; i++){
            students[i] = null;
        }
    }
    public void setRandom(){
        random = (int)(Math.random()*100);
    }
    public int getRandom(){
        return random;
    }

    public void setIsFull(boolean val){
        isFull = val;
    }
    public boolean getIsFull(){
        return isFull;
    }
    public void setIsCut(boolean val){
        isCut = val;
    }
    public boolean getIsCut(){
        return isCut;
    }
    public void setDuplicate(int val){
        duplicate = val;
    }
    public int getDuplicate(){
        return duplicate;
    }

    public int getStudentsIndex(){
        return studentsIndex;
    }
    public void incrementStudentsIndex(){
        studentsIndex++;
    }

    public Student getStudent(int index){
        return students[index];
    }

    public void addStudent(Student student){
        students[studentsIndex] = student;
        studentsIndex++;
    }

    public void incrementNumEnrolled(int step){
        numEnrolled += step;
    }

    public int getNumEnrolled(){
        return numEnrolled;
    }

    public void setPlacability(int setPlacability){
        placability = setPlacability;
    }

    //Overloaded method of setPlacability to just increment/decrement by set value
    public void incrementPlacability(int step){
        placability += step;
    }

    public int getPlacability(){
        return placability;
    }

    public String getSessionName(){
        return sessionName;
    }

    public int getSessionID(){
        return sessionID;
    }

    public String getPresenterName(){
        return presenterName;
    }


}
