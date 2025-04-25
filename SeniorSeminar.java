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
        placeStudents();
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
        for(int i = 0, len = sortedStudents.size(); i < len; i++){
            System.out.print(sortedStudents.get(i).getPlacability() + "   ");
        }
    }

    public ArrayList<Seminar> sortSeminarsByPlacability(ArrayList<Seminar> arrayList){
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
        return arrayList;
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
    public double placeStudents(){
        Seminar[][] schedule = 
        {{seminars.get(1-1), seminars.get(6-1), seminars.get(12-1), seminars.get(18-1), seminars.get(23-1)},
        {seminars.get(2-1), seminars.get(7-1), seminars.get(13-1), seminars.get(19-1), seminars.get(24-1)},
        {seminars.get(3-1), seminars.get(9-1), seminars.get(14-1), seminars.get(20-1), seminars.get(25-1)},
        {seminars.get(4-1), seminars.get(10-1), seminars.get(15-1), seminars.get(21-1), seminars.get(26-1)},
        {seminars.get(5-1), seminars.get(11-1), seminars.get(16-1), seminars.get(22-1), seminars.get(27-1)}
        };  

        sortStudentsByPlacability();

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
                        int seminarIndex = currStudent.getSeminarsIndex();
                        if(studentIndex == 16) currSeminar.setIsFull(true);
                        if(currChoice == currSeminar.getSessionID() && !isPlaced(currStudent, currChoice) && !currSeminar.getIsFull()){
                            currStudent.addSeminar(currSeminar);
                            currSeminar.addStudent(currStudent);
                            isFilled = true;
                            break outer;
                        }
                        
                    }   
                }
                // if(row == 0 && studentID == 40){
                //     for(int i = 0; i < 5; i++){
                //         System.out.println(timeslotSeminars.get(i).getStudentsIndex());
                //     }
                // }
                //If student cannot be placed out of choices then place them into least popular course available in given timeslot
                if(!isFilled){
                    ArrayList<Seminar> timeslotSeminars = new ArrayList<Seminar>();
                    if(studentID == 72)System.out.println();
                    for(int col = 0, timeslotIndex = 0; col < 5; col++, timeslotIndex++){
                        timeslotSeminars.add(schedule[row][col]);
                    }
                    if(studentID == 72) System.out.println();
                    timeslotSeminars = sortSeminarsByPlacability(timeslotSeminars);
                    for(int col = 0; col < 5; col++){
                        if(studentID == 72) System.out.println(timeslotSeminars.get(col).getStudentsIndex());
                    }
                    for(int timeslotIndex = 4; timeslotIndex >= 0; timeslotIndex--){
                        Seminar currSeminar = timeslotSeminars.get(timeslotIndex);
                        if(!currSeminar.getIsFull()){
                            currStudent.addSeminar(currSeminar);
                            currSeminar.addStudent(currStudent);
                            break;
                        }
                        if(timeslotIndex == 0){
                            // for(int i = 0; i < 5; i++){
                            //     System.out.println(timeslotSeminars.get(i).getStudentsIndex());
                            // }
                        }
                    }
                }
            }
        }   
    

        //Calculate avgCoursePlacement
        int totalMatchedSeminars = 0;
        for(int studentID = 0; studentID < sortedStudents.size(); studentID++){
            Student currStudent = sortedStudents.get(studentID);
            System.out.println("");
            for(int i = 0; i < currStudent.getSeminarsIndex(); i++){
                System.out.print(currStudent.getSeminar(i).getSessionID() + "   ");
            }
            //System.out.print("***" + currStudent.getStudentID());
            System.out.println();
        }   
        double avgCoursePlacement = totalMatchedSeminars/74.0;
        System.out.println(seminars.get(5).getDuplicate());
        System.out.println(totalMatchedSeminars);
        System.out.print(avgCoursePlacement);
        
        return 0.1;
    }

    public void printStudentSchedule(int studentID){
        Student student = students.get(studentID-1);
        for(int i = 0; i < 5; i++){
            System.out.println(student.getSeminar(i).getSessionID());
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
                                    seminars.get(seminars.get(sessionID-1).getDuplicate()-1).incrementNumEnrolled(1);;
                                }
                                //Count seminar choice into tally 2D Array (subtract one to match array indexing)
                                tally[sessionID-1][1]++;
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
        /* 
        for(int i = 0; i < students.size(); i++){
            for(int j = 0; j < students.get(i).getChoiceSize(); j++){
                System.out.print(students.get(i).getChoice(j) + "   ");
            }
            System.out.println("");
        }
        */
        for(int i = 0; i < seminars.size(); i++){
            System.out.print(seminars.get(i).getNumEnrolled() + "   ");
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


}