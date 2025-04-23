import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SeniorSeminar {

    ArrayList<Student> students = new ArrayList<Student>();
    ArrayList<Seminar> seminars = new ArrayList<Seminar>();

    Seminar[][] unweightedSchedule = new Seminar[5][5];
    Seminar[][] weightedSchedule = new Seminar[5][5];

    ArrayList<Student> sortedStudents = students;
    ArrayList<Seminar> sortedSeminars = seminars;
    Seminar[][] test;

    int[][] tally = new int[18][3];

    public SeniorSeminar(){

    }

    public void runSeniorSeminar(){
        loadCSV();
        sortTally();
        sortStudentsByPlacability(0);
        placeStudents();
    }

    public void calculatePlacability(int startIndex){
        //Calculate placability of each student
        
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
    public void sortStudentsByPlacability(int startIndex){
        calculatePlacability(startIndex);

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
    public boolean isPlaced(int choice, Seminar[] placed){
        for(int i = 0; i < 5; i++){
            if(placed[i] == null && placed[i].getSessionID() == choice) return true;
        }
        return false;
    }
    //public double placeStudents(Seminar[][] schedule){
    public double placeStudents(){
        Seminar[][] schedule = {{seminars.get(1-1), seminars.get(14-1), seminars.get(10-1), seminars.get(4-1), seminars.get(7-1)},
        {seminars.get(2-1), seminars.get(3-1), seminars.get(11-1), seminars.get(15-1), seminars.get(16-1)},
        {seminars.get(15-1), seminars.get(18-1), seminars.get(13-1), seminars.get(6-1), seminars.get(9-1)},
        {seminars.get(16-1), seminars.get(6-1), seminars.get(12-1), seminars.get(18-1), seminars.get(2-1)},
        {seminars.get(7-1), seminars.get(9-1), seminars.get(5-1), seminars.get(3-1), seminars.get(1-1)}
        };  


        //iterates through every student
        for(int studentID = 0; studentID < sortedStudents.size(); studentID++){
            Student currStudent = sortedStudents.get(studentID);
            int coursesPlaced = 0;
            int[] choice = currStudent.getChoice();
            Seminar[] placed = currStudent.getPlacedSeminars();

            for(int row = 0; row < 5; row++){
                boolean isFilled = false;
                outer: for(int col = 0; col < 5; col++){
                    for(int rank = 0; rank < 5; rank++){
                        
                        if(choice[rank] == schedule[row][col].getSessionID() && !isPlaced(choice[rank], placed)){
                            currStudent.setPlacedSeminar(coursesPlaced, schedule[row][col]);
                            schedule[row][col].setUnweightedStudent(schedule[row][col].getUnweightedCounter(), currStudent);
                            schedule[row][col].decrementPlacability();
                            placed[coursesPlaced] = schedule[row][col]; 
                            isFilled = true;
                            coursesPlaced++;
                            //cannot select more than one seminar in the same time slot so once selected break
                            break outer;
                        }
                        
                    }   
                }
                if(!isFilled){
                    int index = sortedSeminars.size()-1;
                    while(isPlaced(sortedSeminars.get(index).getSessionID(), placed)){
                        index--;
                    }
                    Seminar maxSeminar = sortedSeminars.get(index);
                    currStudent.setPlacedSeminar(coursesPlaced, maxSeminar);
                    placed[coursesPlaced] = maxSeminar;
                    coursesPlaced++;
                    System.out.println(currStudent.getStudentID());
                    //Add student into max
                    maxSeminar.setUnweightedStudent(maxSeminar.getUnweightedCounter(), currStudent);
                    maxSeminar.decrementPlacability();
                }
            }
        }

        //Calculate avgCoursePlacement
        int totalMatchedSeminars = 0;
        int totalStudents = 0;
        for(int studentID = 0; studentID < sortedStudents.size(); studentID++){
            Student currStudent = sortedStudents.get(studentID);
            int[] choice = currStudent.getChoice();
            Seminar[] placedSeminars = currStudent.getPlacedSeminars();
                for(int i = 0; i < choice.length; i++){
                    for(int j = 0; j < placedSeminars.length; j++){
                        if(choice[i] != -1 && choice[i] == placedSeminars[j].getSessionID()){
                            totalMatchedSeminars++;
                            totalStudents++;
                        }
                    }
                }
        }
        double avgCoursePlacement = totalMatchedSeminars/70;
        for(int studentID = 0; studentID < sortedStudents.size(); studentID++){
            for(int j = 0; j < 5; j++){
                System.out.print(sortedStudents.get(studentID).getPlacedSeminar(j).getSessionID() + "   ");
            }
            System.out.println();
        }
        System.out.println(totalMatchedSeminars);
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