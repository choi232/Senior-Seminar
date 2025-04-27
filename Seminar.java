/** 
 * Seminar.java File for creating Seminar objects in Senior Seminar
 * @author Mikael Choi 
 * @since 4/26/2025
 * Preconditions: CSV file data to create Seminar objects
 * Postconditions: Creates a seminar object with getter and setter methods as well as important attributes
 * Purpose: Create a blueprint of attributes and methods for the 
 * seminar object to better store data about seminars
 * **/

 /*
  * Seminar class creates a blueprint of Seminar object with all attributes to describe Seminar object
  * and methods required to get and set attributes as SeniorSeminar class requires
  */
public class Seminar {

    //Required attributes to create Seminar object
    private String sessionName;
    private int sessionID;
    private String presenterName;

    //Placability is a value used to determine student interest in a Seminar, calculated by taking spots available, 16, subtracted by numEnrolled
    private int placability = 16;

    //Amount of students who selected this seminar in their choices
    private int numEnrolled = 0;

    //Array of students to be placed into Seminar
    private Student[] students = new Student[16];

    //Tracker index of students
    private int studentsIndex = 0;

    //SessionID of other duplicate session
    private int duplicate = -1;

    //Boolean flag to tell if the class has been cut or not
    private boolean isCut = false;

    //Boolean flag to tell if the class is full or not
    private boolean isFull = false;

    private int random;

    //Constructor to create seminar object with sessionName, sessionID, and presenterName
    public Seminar(String setSessionName, int setSessionID, String setPresenterName){
        sessionName = setSessionName;
        sessionID = setSessionID;
        presenterName = setPresenterName;
    }

    //Getter and Setter Methods for Seminar Objects

    /*
     * Setter method which resets all attributes changed from placeStudents() function
     */
    public void resetStudents(){
        studentsIndex = 0;
        for(int i = 0; i < 16; i++){
            students[i] = null;
        }
    }

    /*
     * Setter method which sets random attribute (used to create a random order of seminar objects) to a random number from 0-99
     */
    public void setRandom(){
        random = (int)(Math.random()*100);
    }

    /*
     * Getter method which gets random attribute from Seminar object
     */
    public int getRandom(){
        return random;
    }

    /*
     * Setter method which sets boolean flag isFull to inputted value, helps track if a Seminar's students array is full or not
     */
    public void setIsFull(boolean val){
        isFull = val;
    }

    /*
     * Getter method which gets boolean flag isFull to identify if a Seminar's students array is full or not
     */
    public boolean getIsFull(){
        return isFull;
    }

    /*
     * Setter method which sets boolean flag isCut to inputted value, helps keep track if a Seminar object is going to be used in the schedule or not
     */
    public void setIsCut(boolean val){
        isCut = val;
    }
    /*
     * Getter method which gets boolean flag isCut to identify if a Seminar object is going to be used in the schedule or not
     */
    public boolean getIsCut(){
        return isCut;
    }
    /*
     * Setter method which sets duplicate attribute (the sessionID of other duplicate if exists) to value
     */
    public void setDuplicate(int val){
        duplicate = val;
    }
    /*
     * Getter method which gets duplicate attribute
     */
    public int getDuplicate(){
        return duplicate;
    }

    /*
     * Getter method which gets studentsIndex to keep track of current position in students array
     */
    public int getStudentsIndex(){
        return studentsIndex;
    }


    /*
     * Getter method which gets Student object in students array
     */
    public Student getStudent(int index){
        return students[index];
    }

    /*
     * Setter method which adds Student object in students array and also increments studentIndex to update current position of students in array
     */
    public void addStudent(Student student){
        students[studentsIndex] = student;
        studentsIndex++;
    }

    /*
     * Setter method which increments/decrements numEnrolled by step value, used to update the amount of students interested in a seminar in loadCSV()
     */
    public void incrementNumEnrolled(int step){
        numEnrolled += step;
    }

    /*
     * Getter method which gets numEnrolled
     */
    public int getNumEnrolled(){
        return numEnrolled;
    }

    /*
     * Setter method which sets placability (an value meant to represent student interest in a Seminar which is caclulated by the difference between spots available minus numEnrolled) to inputted value
     */
    public void setPlacability(int setPlacability){
        placability = setPlacability;
    }

    /*
    * Setter method to just increment/decrement placability by step value
    */
    public void incrementPlacability(int step){
        placability += step;
    }

    /*
     * Getter method which gets placability
     */
    public int getPlacability(){
        return placability;
    }

    /*
     * Getter method which gets sessionName
     */
    public String getSessionName(){
        return sessionName;
    }

    /*
     * Getter method which gets sessionID
     */
    public int getSessionID(){
        return sessionID;
    }

    /*
     * Getter method which gets presenterName
     */
    public String getPresenterName(){
        return presenterName;
    }


}
