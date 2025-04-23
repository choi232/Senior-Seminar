import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SeniorSeminar {

    ArrayList<Student> students = new ArrayList<Student>();
    ArrayList<Seminar> seminars = new ArrayList<Seminar>();

    Seminar[][] unweightedSchedule = new Seminar[5][5];
    Seminar[][] weightedSchedule = new Seminar[5][5];

    Seminar[][] test = new Seminar[5][5];

    int[][] tally = new int[18][3];

    public SeniorSeminar(){

    }

    public void initializeTestSchedule(){
        for(int i = 0, k = 0; i < test.length; i++){
            for(int j = 0; j < test.length; j++){
                test[i][j] = seminars.get(k);
                k++;
            }
        }
    }
    public void runSeniorSeminar(){
        loadCSV();
        sortTally();
        sortStudentsByPlacability();
        //initializeTestSchedule();
    }
    public void sortStudentsByPlacability(){
        //Calculate placability of each student
        
        for(int studentID = 0; studentID < students.size(); studentID++){
            int placability = 0;
            Student currStudent = students.get(studentID);
            int[] choice = currStudent.getChoice();
            for(int rank = 0; rank < choice.length; rank++){
                if(choice[rank] != -1) placability += seminars.get(choice[rank]-1).getPlacability();
                else placability = 16*25;
            }
            currStudent.setPlacability(placability);
            System.out.println(currStudent.getPlacability());
        }

        //

    }
    public double placeStudents(Seminar[][] schedule){
        initializeTestSchedule();
        double avgCoursePlacement = 0;
        //iterates through every student
        for(int studentID = 0; studentID < students.size(); studentID++){
            Student currStudent = students.get(studentID);
            int coursesPlaced = 0;
            int[] choice = currStudent.getChoice();
            for(int row = 0; row < 5; row++){
                for(int col = 0; col < 5; col++){
                    for(int rank = 0; rank < 5; rank++){
                        if(choice[rank] == schedule[row][col].getSessionID()){
                            currStudent.setSeminar(coursesPlaced, schedule[row][col]);
                            //cannot select more than one seminar in the same time slot so once selected break
                            break;
                        }
                    }
                }
            }
        }
        return avgCoursePlacement;
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
            //while loop which runs through every line of the csv with scan.hasNextLine()
            while (scan.hasNextLine()) { 
                String data = scan.nextLine();
                if(studentIndex > -1){
                    String[] splitStr = data.split(",");
                    
                    String sessionName = splitStr[0];
                    int sessionID = Integer.parseInt(splitStr[1]);
                    String presenterName = splitStr[2];
                    int spots = Integer.parseInt(splitStr[3]);
                    
                    Seminar tempSeminar = new Seminar(sessionName, sessionID, presenterName, spots);
                    tempSeminar.setPlacability(-1*spots);
                    seminars.add(tempSeminar);
                    
                }
                studentIndex++;
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
                    int[] choice = new int[5];
                    
                    outer: for(int i = 3, arrayIndex = 0; i < splitStr.length; i++, arrayIndex++){
                        try{
                            choice[arrayIndex] = Integer.parseInt(splitStr[i]);
                            //Count seminar choice into tally 2D Array (subtract one to match array indexing)
                            if(Integer.parseInt(splitStr[i]) != -1){
                                tally[Integer.parseInt(splitStr[i])-1][1]++;
                                seminars.get(Integer.parseInt(splitStr[i])-1).incrementPlacability();
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