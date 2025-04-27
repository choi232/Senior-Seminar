/** 
 * Student.java File for creating Student objects in Senior Seminar
 * @author Mikael Choi 
 * @since 4/26/2025
 * Preconditions: CSV file data to create Student objects
 * Postconditions: Creates student object with getter and setter methods as well as important attributes
 * Purpose: Create a blueprint of attributes and methods for the 
 * student object to better store data about students
 * **/

import java.util.ArrayList; //Import ArrayList

/* 
 * Student class creates a blueprint of Student object with all attributes to describe Student object
 * and methods required to get and set attributes as SeniorSeminar class requires
*/

public class Student{

    //Required attributes to create Student object
    private String name;
    private String email;
    private String time;
    private int studentID;
    //ArrayList for student's choices including duplicates, thus the size of the ArrayList can vary from 5 to 10
    private ArrayList<Integer> choice;

    //Student placability is a number used to represent how popular the courses the student chose were, it is calculated as the sum of all the chosen seminar's placabilities
    private int placability = 0;
    //seminars stores the five seminars the student is placed into based on the placeStudent() algorithm
    private Seminar[] seminars = new Seminar[5];
    //seminarsIndex is a index tracker for seminars
    private int seminarsIndex = 0;

    //Constructor to create student object with name, email, time, choice and studentID
    public Student(String setName, String setEmail, String setTime, int setStudentID, ArrayList<Integer> setChoice){
        name = setName;
        email = setEmail;
        time = setTime;
        choice = setChoice;
        studentID = setStudentID;
    }
    
    //Getter and Setter Methods for Student Objects

    /*
     * Getter method which gets seminarsIndex
     * Requires: none
     * Returns: int seminarsIndex
     */
    public int getSeminarsIndex(){
        return seminarsIndex;
    }

    /*
     * Getter method which gets seminar object from the seminars array
     * Requires: an int for the index you want to access
     * Returns: void
     */
    public Seminar getSeminar(int index){
        return seminars[index];
    }

    /*
     * Setter method which adds a Seminar object to the seminars array and increments seminarsIndex tracker by 1
     * Requires: Seminar object that you want to add
     * Returns: void
     */
    public void addSeminar(Seminar setSeminar){
        seminars[seminarsIndex] = setSeminar;
        seminarsIndex++;
    }

    /*
     * Setter method which resets all attributes changed from placeStudents() function, resets seminars array and seminarsIndex specifically
     * Requires: none
     * Returns: void
     */ 
    public void resetSeminars(){
        for(int i = 0; i < 5; i++){
            seminars[i] = null;
        }
        seminarsIndex = 0;
    }

    /*
     * Setter method which sets placability (an value meant to represent popularity of student's choices) to inputted value
     * Placability is calculated as the sum of all the student's chosen/selected seminar's placabilities
     * Requires: int setPlacability, the value you would like to set placability as
     * Returns: void
     */
    public void setPlacability(int setPlacability){
        placability = setPlacability;
    }

    /*
     * Getter method which gets placability (an value meant to represent popularity of student's choices) to inputted value
     * Placability is calculated as the sum of all the student's chosen/selected seminar's placabilities
     * Requires: none
     * Returns: int placability
     */
    public int getPlacability(){
        return placability;
    }

    /*
     * Setter method which sets an inputted sessionID into student's choice ArrayList
     * Requires: int val (the sessionID of the seminar you want to add)
     * Returns: void
     */
    public void setChoice(int index, int val){
        choice.set(index, val);
    }

    /*
     * Getter method which gets a sessionID from student's choice ArrayList at inputted index
     * Requires: int index
     * Returns: int, sessionID from choice ArrayList
     */
    public int getChoice(int index){
        return choice.get(index);
    }

    /*
     * Getter method which gets the size of the student's choice ArrayList
     * Requires: none
     * Returns: int, size of choice ArrayList
     */
    public int getChoiceSize(){
        return choice.size();
    }

    /*
     * Getter method which gets name
     * Requires: none
     * Returns: String name
     */
    public String getName(){
        return name;
    }

    /*
     * Getter method which gets email
     * Requires: none
     * Returns: String emai;
     */
    public String getEmail(){
        return email;
    }

    /*
     * Getter method which gets time
     * Requires: none
     * Returns: String time
     */
    public String getTime(){
        return time;
    }

    /*
     * Getter method which gets studentID
     * Requires: none
     * Returns: int studentID
     */
    public int getStudentID(){
        return studentID;
    }
    
}