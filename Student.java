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

    private String name;
    private String email;
    private String time;
    private int studentID;
    private int placability = 0;
    private ArrayList<Integer> choice;
    private Seminar[] seminars = new Seminar[5];
    private int seminarsIndex = 0;

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

    public void resetSeminars(){
        for(int i = 0; i < 5; i++){
            seminars[i] = null;
        }
        seminarsIndex = 0;
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