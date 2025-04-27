import java.io.File;                    //Import File to create file objects of CSVs
import java.io.FileNotFoundException;   //Import FileNotFoundException to catch FileNotFoundException errors
import java.util.ArrayList;             //Import ArrayList 
import java.util.Scanner;               //Import Scanner to read user input
import java.io.FileWriter;              // Import FileWriter
import java.io.IOException;             // Import IOException to catch IOException errors

/** 
 * SeniorSeminar.java File for creating SeniorSeminar object which contains all the methods to run SeniorSeminar from Main.java
 * @author Mikael Choi 
 * @since 4/26/2025
 * Preconditions: CSV file data to run loadCSV() method
 * Postconditions: Creates Student and Seminar objects as well as creates a SeniorSeminar object which can run the entire optimization program
 * Purpose: to load student and seminar objects from CSV and then create a random schedule 
 * to place students into seminar objects until desired optimization value is reached and then
 * saving the schedule into a corresponding schedule CSV file
 * **/

public class SeniorSeminar {

    ArrayList<Student> students = new ArrayList<Student>();
    ArrayList<Seminar> seminars = new ArrayList<Seminar>();

    ArrayList<Student> sortedStudents = students;

    public void runSeniorSeminar(){
        loadCSV();
        sortStudentsByPlacability();
        optimizeSchedule();
    }
    
    public void loadCSV(){

        //Creates Seminar objects from the file and adds them into seminar
        try {
            File seminarCSV = new File("csv/seminar.csv");
            Scanner scan = new Scanner(seminarCSV);
            int studentIndex = -1;
            int numDuplicates = 0;
            //while loop which runs through every line of the csv with scan.hasNextLine()
            while (scan.hasNextLine()) { 
                String data = scan.nextLine();
                if(studentIndex > -1){
                    String[] splitStr = data.split(",");
                    
                    String sessionName = splitStr[0];
                    int sessionID = Integer.parseInt(splitStr[1]);
                    String presenterName = splitStr[2];
                    int spots = Integer.parseInt(splitStr[3]);
                    
                    Seminar tempSeminar = new Seminar(sessionName, sessionID, presenterName);
                    //If not a seminar decided to be cut
                    if(spots == 32){
                        tempSeminar.setDuplicate(numDuplicates + 19);
                        numDuplicates++;
                    }
                    else if(spots == 0) tempSeminar.setIsCut(true);
                        
                    seminars.add(tempSeminar);
                    
                }
                studentIndex++;
            }
            for(int i = 0, len = seminars.size(), sessionID = 19; i < len; i++){
                if(seminars.get(i).getDuplicate() != -1){
                    Seminar currSeminar = seminars.get(i);
                    Seminar tempSeminar = new Seminar(currSeminar.getSessionName(), sessionID, currSeminar.getPresenterName());
                    tempSeminar.setDuplicate(sessionID-18);
                    seminars.add(tempSeminar);
                    sessionID++;
                }
            }
            scan.close();
        } 
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        //Creates Student objects from the file and adds them into students ArrayList
        try {
            File studentCSV = new File("csv/student.csv");
            Scanner scan = new Scanner(studentCSV);
            int studentIndex = -1;
            //while loop which runs through every line of the csv with scan.hasNextLine()
            while (scan.hasNextLine()) { 
                String data = scan.nextLine();
                if(studentIndex > -1){
                    String[] splitStr = data.split(",");
                    
                    String time = splitStr[0];
                    String email = splitStr[1];
                    String name = splitStr[2];
                    int studentID = studentIndex;
                    ArrayList<Integer> choice = new ArrayList<Integer>();
                    
                    for(int i = 3; i < splitStr.length; i++){
                        try{
                            int sessionID = Integer.parseInt(splitStr[i]);
                            if(sessionID != -1 && !seminars.get(sessionID-1).getIsCut()){
                                choice.add(sessionID);
                                if(seminars.get(sessionID-1).getDuplicate() != -1){
                                    //Adds duplicate choice into choice ArrayList
                                    choice.add(seminars.get(sessionID-1).getDuplicate());
                                    seminars.get(seminars.get(sessionID-1).getDuplicate()-1).incrementNumEnrolled(1);
                                }
                                //Increment number of students enrolled into seminars attribute
                                seminars.get(sessionID-1).incrementNumEnrolled(1);
                            }
                            //Count seminar choice into tally 2D Array (subtract one to match array indexing)
                            else if(sessionID == -1) choice.add(sessionID);
                            else seminars.get(sessionID-1).incrementNumEnrolled(1);

                            
                        }
                        catch(NumberFormatException e){
                        }
                    }
                    Student tempStudent = new Student(name, email, time, studentID, choice);
                    students.add(tempStudent);
                }
                studentIndex++;
            }
            scan.close();
        } 
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    public Seminar[][] createRandomSchedule(ArrayList<Seminar> popularSeminars){
        for(int i = 0, len = seminars.size(); i < len; i++){
            seminars.get(i).setRandom();
        }
        //Sort random numbers from lowest to highest to create a new random order to add to schedule in this new pseudorandom sorted order
        sortSeminarsByRandom(popularSeminars);

        Seminar[][] randomSchedule = new Seminar[5][5];
        for(int row = 0, seminarIndex = 0; row < 5; row++){
            for(int col = 0; col < 5; col++){
                randomSchedule[row][col] = popularSeminars.get(seminarIndex);
                seminarIndex++;
            }
        }

        return randomSchedule;
    }
    public void resetData(){
        for(int studentID = 0, len = students.size(); studentID < len; studentID++){
            students.get(studentID).resetSeminars();
            
        }
        for(int seminarIndex = 0, len = seminars.size(); seminarIndex < len; seminarIndex++){
            seminars.get(seminarIndex).resetStudents();
            seminars.get(seminarIndex).setIsFull(false);
        }
    }
    public void optimizeSchedule(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Please input optimization value: ");
        double optimizationValue = scan.nextDouble();
        //Seems like the max value that I can get from my placing algorithm without the computer taking forever is 4.2
        //double optimizationValue = 4.2;
        //ArrayList of top 25 popular seminars (ArrayList accounts for max two sessions and min one session per professor rule)
        ArrayList<Seminar> popularSeminars = new ArrayList<Seminar>();
        for(int i = 0, len = seminars.size(); i < len; i++){
            //Check if seminars is to be cut and if not add into top 25 popular seminars
            if(!seminars.get(i).getIsCut()){
                popularSeminars.add(seminars.get(i));
            }
        }
        Seminar[][] randomSchedule;
        while(true){
            randomSchedule = createRandomSchedule(popularSeminars);
            if(placeStudents(randomSchedule) < optimizationValue){
                resetData();
            }
            else break;
        }

        try {
            //File title is optimization value + Schedules.csv so that data can be saved for any optimization value
            //Second argument true creates FileWriter as an appending FileWriter object so data can be saved and not overwritten4.4
            FileWriter myWriter = new FileWriter("Saved Schedules/" + optimizationValue + "Schedules.csv", true);
            for(int row = 0; row < 5; row++){
                for(int col = 0; col < 5; col++){
                    if(col != 4) myWriter.append(randomSchedule[row][col].getSessionID() + ",");
                    else myWriter.append(randomSchedule[row][col].getSessionID()+"\n");
                }
            }
            myWriter.append("\n");
            myWriter.close();
            System.out.println("\nSchedule saved to " + optimizationValue + "Schedules.csv");
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }



    public void calculatePlacability(){
        //Placability it the total spots available in the seminar minus the students who want these spots
        //Calculate placability of each seminar
        for(int sessionIndex = 0; sessionIndex < seminars.size(); sessionIndex++){
            seminars.get(sessionIndex).setPlacability(16-seminars.get(sessionIndex).getNumEnrolled());
        }
        //Calculate placability of each student
        for(int studentID = 0; studentID < students.size(); studentID++){
            int placability = 16;
            Student currStudent = students.get(studentID);
            for(int rank = 0, len = currStudent.getChoiceSize(); rank < len; rank++){
                if(currStudent.getChoice(rank) != -1) placability -= seminars.get(currStudent.getChoice(rank)-1).getNumEnrolled();
                else placability = 16*25;
            }
            currStudent.setPlacability(placability);
        }
        
    }
    public void sortStudentsByPlacability(){
        calculatePlacability();

        //Sort each student by placability with selection sort from lowest to highest

        for(int i = 0, len = sortedStudents.size(); i < len; i++){
            int currMinIndex = i;
            int currMinimum = sortedStudents.get(i).getPlacability();

            for(int j = i; j < sortedStudents.size(); j++){
                Student currStudent = sortedStudents.get(j);
                if(currStudent.getPlacability() < currMinimum){
                    currMinimum = currStudent.getPlacability();
                    currMinIndex = j;
                }
            }
            Student temp = sortedStudents.get(i);
            sortedStudents.set(i, sortedStudents.get(currMinIndex));
            sortedStudents.set(currMinIndex, temp);
        }

    }

    public void sortSeminarsByRandom(ArrayList<Seminar> arrayList){
        //Sort each seminar by placability with selection sort from lowest to highest

        for(int i = 0, len = arrayList.size(); i < len; i++){
            int currMinIndex = i;
            int currMinimum = arrayList.get(i).getRandom();

            for(int j = i; j < arrayList.size(); j++){
                Seminar currSeminar = arrayList.get(j);
                if(currSeminar.getRandom() < currMinimum){
                    currMinimum = currSeminar.getRandom();
                    currMinIndex = j;
                }
            }
            Seminar temp = arrayList.get(i);
            arrayList.set(i, arrayList.get(currMinIndex));
            arrayList.set(currMinIndex, temp);
        }    }

    public void sortSeminarsByPlacability(ArrayList<Seminar> arrayList){
        //Sort each seminar by placability with selection sort from lowest to highest

        for(int i = 0, len = arrayList.size(); i < len; i++){
            int currMinIndex = i;
            int currMinimum = arrayList.get(i).getPlacability();

            for(int j = i; j < arrayList.size(); j++){
                Seminar currSeminar = arrayList.get(j);
                if(currSeminar.getPlacability() < currMinimum){
                    currMinimum = currSeminar.getPlacability();
                    currMinIndex = j;
                }
            }
            Seminar temp = arrayList.get(i);
            arrayList.set(i, arrayList.get(currMinIndex));
            arrayList.set(currMinIndex, temp);
        }
    }

    public boolean isPlaced(Student student, int sessionID){
        //Iterate through placed seminars from student schedule and see if they have a match with the passed argument sessionID
        //Also check if any duplicates have a match with the argument sessionID as well
        for(int i = 0, len = student.getSeminarsIndex(); i < len; i++){
            int currSessionID = student.getSeminar(i).getSessionID();
            int duplicateSessionID = student.getSeminar(i).getDuplicate();
            if(currSessionID == sessionID){
                return true;
            }
            else if(duplicateSessionID == sessionID){
                return true;
            }
        }
        return false;
    }
    
    public double placeStudents(Seminar[][] schedule){

        int totalMatchedSeminars = 0;

        //iterates through every student
        for(int studentID = 0; studentID < sortedStudents.size(); studentID++){
            Student currStudent = sortedStudents.get(studentID);
            
            for(int row = 0; row < 5; row++){
                boolean isFilled = false;
                outer: for(int col = 0; col < 5; col++){
                    for(int rank = 0, len = currStudent.getChoiceSize(); rank < len; rank++){
                        Seminar currSeminar = schedule[row][col];
                        int currChoice = currStudent.getChoice(rank);
                        int studentIndex = currSeminar.getStudentsIndex();
                        if(studentIndex == 16) currSeminar.setIsFull(true);
                        if(currChoice == currSeminar.getSessionID() && !isPlaced(currStudent, currChoice) && !currSeminar.getIsFull()){
                            currStudent.addSeminar(currSeminar);
                            currSeminar.addStudent(currStudent);
                            totalMatchedSeminars++;
                            isFilled = true;
                            break outer;
                        }
                        
                    }   
                }

                //If student cannot be placed out of choices then place them into least popular course available in given timeslot
                if(!isFilled){
                    ArrayList<Seminar> timeslotSeminars = new ArrayList<Seminar>();
                    for(int col = 0; col < 5; col++){
                        timeslotSeminars.add(schedule[row][col]);
                    }
                    sortSeminarsByPlacability(timeslotSeminars);

                    //Iterate backwards through sorted array to pick least popular session
                    for(int timeslotIndex = 4; timeslotIndex >= 0; timeslotIndex--){
                        Seminar currSeminar = timeslotSeminars.get(timeslotIndex);
                        if(!currSeminar.getIsFull()){
                            currStudent.addSeminar(currSeminar);
                            currSeminar.addStudent(currStudent);
                            break;
                        }
                    }
                }
            }
        }   
    
        //Calculate avgCoursePlacement 
        double avgCoursePlacement = totalMatchedSeminars/70.0;
        //Print out every schedule's calculated avgCoursePlacement
        System.out.println(avgCoursePlacement);

        //Return avgCoursePlacement
        return avgCoursePlacement;
    }

    public void printStudentSchedule(int studentID){
        Student student = students.get(studentID-1);
        for(int i = 0; i < 5; i++){
            System.out.print(student.getSeminar(i).getSessionID() + "   ");
        }
        System.out.println();
    }

    public void printMasterSchedule(){
        for(int row = 0; row < 5; row++){
            for(int col = 0; col < 5; col++){

            }
        }
    }

    public void printRoomRoster(){

    }


}