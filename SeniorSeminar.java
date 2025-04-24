import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SeniorSeminar {

    ArrayList<Student> students = new ArrayList<Student>();
    ArrayList<Seminar> seminars = new ArrayList<Seminar>();

    ArrayList<Student> sortedStudents = students;
    ArrayList<Seminar> sortedSeminars = seminars;
    Seminar[][] test;

    int[][] tally = new int[18][3];

    public SeniorSeminar(){

    }

    public void runSeniorSeminar(){
        loadCSV();
        //sortTally();
        //sortByPlacability("student", 0);
    }

    public void calculatePlacability(String type, int startIndex){
        //Calculate placability of each student
        if(type.equals("student")){
            for(int studentID = startIndex; studentID < students.size(); studentID++){
                int placability = 0;
                Student currStudent = students.get(studentID);
                int[] choice = currStudent.getChoice();
                for(int rank = 0; rank < choice.length; rank++){
                    if(choice[rank] != -1) placability += seminars.get(choice[rank]-1).getPlacability();
                    else placability = 16*25;
                }
                currStudent.setPlacability(placability);
            }
        }
        else if (type.equals("seminar")){

        }
    }
    public void sortByPlacability(String type, int startIndex){
        calculatePlacability(type, startIndex);

        //Sort each student by placability with selection sort from lowest to highest

        for(int i = startIndex, len = sortedStudents.size(); i < len; i++){
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
        //Sort each seminar by placability with selection sort from lowest to highest

        for(int i = startIndex, len = sortedSeminars.size(); i < len; i++){
            int currMinIndex = i;
            int currMinimum = sortedStudents.get(i).getPlacability();

            for(int j = i; j < sortedSeminars.size(); j++){
                Seminar currSeminar = sortedSeminars.get(j);
                if(currSeminar.getPlacability() < currMinimum){
                    currMinimum = currSeminar.getPlacability();
                    currMinIndex = j;
                }
            }
            Seminar temp = sortedSeminars.get(i);
            sortedSeminars.set(i, sortedSeminars.get(currMinIndex));
            sortedSeminars.set(currMinIndex, temp);
        }
    }
    
    public void sortTally(){
        //Initialize tally with sessionID values
        for(int i = 0; i < 18; i++){
            tally[i][0] = i+1;
        }
        //temp var
        int currSmallest;
        for(int i = 0; i < tally.length; i++){
            currSmallest = tally[i][1];
            for(int j = i; j < tally.length; j++){
                //Switch both sesion number  values if participants is greater in another array
                if(tally[j][1] < currSmallest){
                    currSmallest = tally[j][1];
                    tally[j][1] = tally[i][1];
                    tally[i][1] = currSmallest;
                    int temp = tally[j][0];
                    tally[j][0] = tally[i][0];
                    tally[i][0] = temp;
                }
            }
        }

        /*for(int i = 0; i < tally.length; i++){
            tally[i][3] = seminars.get(tally[i][0]-1).getSpots()-tally[i][1];
            System.out.print(tally[i][1] + "(" + (seminars.get(tally[i][0]-1).getSpots()-tally[i][1]) + ")    ");
        }
            */
    }

    public void updateTally(){

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
                    
                    //If not a seminar decided to be cut
                    if(spots == 16){
                        Seminar tempSeminar = new Seminar(sessionName, sessionID, presenterName);
                        seminars.add(tempSeminar);
                    }
                    else if(spots == 32){
                        Seminar tempSeminar = new Seminar(sessionName, sessionID, presenterName);
                        tempSeminar.setDuplicate(numDuplicates + 19);
                        seminars.add(tempSeminar);
                        numDuplicates++;
                    }
                    
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
                            choice.add(sessionID);
                            if(sessionID != -1 && seminars.get(sessionID-1).getDuplicate() != -1){
                                //Adds duplicate choice into choice ArrayList
                                choice.add(seminars.get(sessionID-1).getDuplicate());
                            }
                            //Count seminar choice into tally 2D Array (subtract one to match array indexing)
                            if(Integer.parseInt(splitStr[i]) != -1){
                                tally[Integer.parseInt(splitStr[i])-1][1]++;
                                seminars.get(Integer.parseInt(splitStr[i])-1).incrementNumEnrolled(1);
                            }
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

        for(int i = 0; i < students.size(); i++){
            for(int j = 0; j < students.get(i).getChoiceSize(); j++){
                System.out.print(students.get(i).getChoice(j) + "   ");
            }
            System.out.println("");
        }

        for(int i = 0; i < seminars.size(); i++){
            System.out.print(seminars.get(i).getSessionID() + "   ");
        }
    }



    public void createUnweightedSchedule(){

    }

    public void createWeightedSchedule(){
        
    }

    public void printMasterSchedule(){

    }

    public void printRoomRoster(){

    }

    public void printStudentSchedule(){

    }

}