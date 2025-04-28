import java.io.File;                    //Import File to create file objects of CSVs
import java.io.FileNotFoundException;   //Import FileNotFoundException to catch FileNotFoundException errors
import java.io.FileWriter;             //Import ArrayList
import java.io.IOException;               //Import Scanner to read user input
import java.util.ArrayList;              // Import FileWriter
import java.util.Scanner;             // Import IOException to catch IOException errors

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


/*
 * SeniorSeminar class to run all necessary functions to run optimization program. An object of Senior Seminar
 * is created in main and run with the runSeniorSeminar()
 */
public class SeniorSeminar {

    //Creation of seminars and students ArrayList to store all seminars and students pulled from csv
    ArrayList<Student> students = new ArrayList<Student>();
    ArrayList<Seminar> seminars = new ArrayList<Seminar>();

    //Creation of sortedSeminars and sortedStudents which are the same students and seminars just sorted by placability and by name
    ArrayList<Student> sortedByPlacabilityStudents;
    ArrayList<Student> sortedByNameStudents;
    ArrayList<Seminar> sortedByNameSeminars = new ArrayList<Seminar>();

    //Saves best schedule and its optimized course average into these variables
    Seminar[][] optimizedSchedule;
    double optimizedCourseAvg;

    /*
     * runSeniorSeminar() assembles all the necessary functions required to run the program so that
     * the Senior Seminar program can be run from a single function
     */
    public void runSeniorSeminar(){
        //Load in intial CSV
        loadCSV();

        //Optimize schedule
        sortStudentsByPlacability();
        optimizeSchedule();

        //Printing functions
        sortByName();
        printMasterSchedule();
        printSessionRoster();
        printStudentSchedule();
    }

    /*
     * loadCSV() loads all the data from the CSV into student and seminar objects which are then 
     * subsequently added to the students and seminars ArrayLists
     */
    public void loadCSV(){

        //Creates Seminar objects from the file and adds them into seminar
        //try block to load seminar.csv data
        try {
            //Create File object from csv/seminar.csv
            File seminarCSV = new File("csv/seminar.csv");
            //Create scanner object
            Scanner scan = new Scanner(seminarCSV);
            //Create seminarIndex to track where in seminars ArrayList we currently are
            //Starts at -1 because the first iteration is skipped because of the column titles in the CSV
            int seminarIndex = -1;
            //Number of duplicates counted in the seminar.csv
            int numDuplicates = 0;

            //while loop which runs through every line of the csv with scan.hasNextLine()
            while (scan.hasNextLine()) { 
                //Turn csv line data into a String
                String data = scan.nextLine();

                //If the data from the scanned line of the csv is not the column titles parse the csv data and create a seminar object and add to seminars
                if(seminarIndex > -1){
                    //Split data into string with delimiter ","
                    String[] splitStr = data.split(",");
                    
                    //Parse splitStr into different types of data
                    //Create required variables and important variables to create Seminar object
                    String sessionName = splitStr[0];
                    int sessionID = Integer.parseInt(splitStr[1]);
                    String presenterName = splitStr[2];
                    int spots = Integer.parseInt(splitStr[3]);
                    
                    //Create a temp seminar from parsed data
                    Seminar tempSeminar = new Seminar(sessionName, sessionID, presenterName);

                    //If not the seminar is decided to have a duplicate based on available spots then add duplicate sessionID into duplicate attribute and increment numDuplicates
                    if(spots == 32){
                        //Add duplicate sessionID into duplicate attribute
                        tempSeminar.setDuplicate(numDuplicates + 19);
                        //increment numDuplicates
                        numDuplicates++;
                    }

                    //Otherwise if the session has no spots / meant to be cut set the isCut flag boolean to true
                    else if(spots == 0) tempSeminar.setIsCut(true);
                        
                    //Add created Seminar object into seminars ArrayList
                    seminars.add(tempSeminar);
                    
                }
                //Increment seminarIndex after adding a seminar into seminars ArrayList to track curr position in ArrayList 
                seminarIndex++;
            }

            //For every single seminar with a duplicate session iterate through and create a duplicate seminar
            //Iterate through seminars ArrayList and find seminars with a duplicate AKA duplicate != -1
            for(int i = 0, len = seminars.size(), sessionID = 19; i < len; i++){
                //If seminar has a duplicate create a new duplicate seminar
                if(seminars.get(i).getDuplicate() != -1){
                    //currSeminar within seminars ArrayList
                    Seminar currSeminar = seminars.get(i);
                    //Create a new duplicate seminar with a sessionID from 19-27 depending on when created
                    Seminar tempSeminar = new Seminar(currSeminar.getSessionName(), sessionID, currSeminar.getPresenterName());
                    //Set duplicate sessionID of original seminar
                    tempSeminar.setDuplicate(sessionID-18);
                    //Add seminar into seminars ArrayList
                    seminars.add(tempSeminar);
                    //Increment sessionID after a duplicate session is added
                    sessionID++;
                }
            }
            
            //Iterate through for loop to see which seminars are the top 25 selected seminars in schedule
            for(int i = 0, len = seminars.size(); i < len; i++){
                //Check if seminars is to be cut and if not add into top 25 popular seminars for sortedByNameSeminars
                if(!seminars.get(i).getIsCut()){
                    //sortedByNameSeminars is a different ArrayList for a later binary search which we initialize with only seminars that are in the final schedule
                    sortedByNameSeminars.add(seminars.get(i));
                }
            }
            //Close scanner
            scan.close();
        } 

        //Catch block to catch FileNotFoundException errors
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        //Creates Student objects from the file and adds them into students ArrayList
        //Try block to load csv/student.csv data into corresponding Student objects
        try {
            //Create File object from csv/seminar.csv
            File studentCSV = new File("csv/student.csv");
            //Create scanner object
            Scanner scan = new Scanner(studentCSV);
            //Create studentIndex to track where in students ArrayList we currently are
            //Starts at -1 because the first iteration is skipped because of the column titles in the CSV
            int studentIndex = -1;

            //while loop which runs through every line of the csv with scan.hasNextLine()
            while (scan.hasNextLine()) { 
                //Turn csv line data into a String                
                String data = scan.nextLine();

                //If the data from the scanned line of the csv is not the column titles parse the csv data and create a student object and add to students
                if(studentIndex > -1){
                    //Split data into string with delimiter ","
                    String[] splitStr = data.split(",");
                    
                    //Parse splitStr into different types of data
                    //Create required variables and important variables to create Student object
                    String time = splitStr[0];
                    String email = splitStr[1];
                    String name = splitStr[2];
                    int studentID = studentIndex;
                    ArrayList<Integer> choice = new ArrayList<Integer>();
                    
                    //Iterate through column 3 until the end to parse student's choice data from CSV and place into Student object's choice ArrayList
                    //For loop to iterate through from column 3 until end
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

            //Copy over student ArrayList to sorted ArrayLists 
            //Learned to do so from https://www.w3schools.com/java/ref_arraylist_clone.asp
            sortedByPlacabilityStudents = (ArrayList) students.clone();
            sortedByNameStudents = (ArrayList) students.clone();

            scan.close();
        } 
        catch (FileNotFoundException e) {
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

        for(int i = 0, len = sortedByPlacabilityStudents.size(); i < len; i++){
            int currMinIndex = i;
            int currMinimum = sortedByPlacabilityStudents.get(i).getPlacability();

            for(int j = i; j < sortedByPlacabilityStudents.size(); j++){
                Student currStudent = sortedByPlacabilityStudents.get(j);
                if(currStudent.getPlacability() < currMinimum){
                    currMinimum = currStudent.getPlacability();
                    currMinIndex = j;
                }
            }
            Student temp = sortedByPlacabilityStudents.get(i);
            sortedByPlacabilityStudents.set(i, sortedByPlacabilityStudents.get(currMinIndex));
            sortedByPlacabilityStudents.set(currMinIndex, temp);
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
        for(int studentID = 0; studentID < sortedByPlacabilityStudents.size(); studentID++){
            Student currStudent = sortedByPlacabilityStudents.get(studentID);
            
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
    
        //Calculate avgCoursePlacement by dividing totalMatchedSeminars by the 69 students who signed up for a course (we ignore students who did not select any choice)
        double avgCoursePlacement = totalMatchedSeminars/69.0;
        //Print out every schedule's calculated avgCoursePlacement
        System.out.println(avgCoursePlacement);

        //Return avgCoursePlacement
        return avgCoursePlacement;
    }
    
    /*
     * createRandomSchedule() creates a random schedule by assigning every seminar a random number
     * and sorting the seminars by this random number from least to greatest with selection sort.
     * Finally, this random order is placed into a schedule of 5x5 in sequentially in the random order placed by the selection sort.
     */
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
        int stop;
        Scanner scan = new Scanner(System.in);

        System.out.println("Welcome to Senior Seminar! This program will optimize a schedule to minimize course conflicts between a student's requested courses and scheduled courses.");
        System.out.println("This program relies on an optimization value which is the average requested courses a student receives out of five.");
        System.out.println("The program seeks to maximize this value by running possible iterations of randomly generated schedules and then running a placing algorithm and calculating the average courses placed.");

        System.out.println("\nTo begin creating a schedule, please enter an integer between one and one million iterations that you would like to run: ");
        String input = scan.nextLine();
        while(true){
            try {
                stop = Integer.parseInt(input);
                if(stop < 1 || stop > 1000000) System.out.println("Please enter a valid integer input for how many iterations you would like to run from one to one million: ");
                else break;
                input = scan.nextLine();
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer input for how many iterations you would like to run from one to one million: ");
                input = scan.nextLine();
            }
        }
        System.out.println("Please input an optimization value (the program will stop early if this value is reached): ");
        double optimizationValue;
        input = scan.nextLine();
        while(true){
            try {
                optimizationValue = Double.parseDouble(input);
                if(optimizationValue < 0 || optimizationValue > 5) System.out.println("Please enter a valid double input from 0 to 5: ");
                else break;
                input = scan.nextLine();
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid double input from 0 to 5: ");
                input = scan.nextLine();
            }
        }

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

        int counter = 0;
        while(true){
            randomSchedule = createRandomSchedule(popularSeminars);
            double currOptimization = placeStudents(randomSchedule);
            counter++;
            if(counter == 1){
                optimizedCourseAvg = currOptimization;
                optimizedSchedule = randomSchedule;
            }

            if(currOptimization > optimizedCourseAvg){
                optimizedCourseAvg = currOptimization;
                optimizedSchedule = randomSchedule;
            }

            if(currOptimization >= optimizationValue){
                System.out.println("\nThe program has ran through " + counter + " times and generated a schedule of " + optimizedCourseAvg + " optimization value");
                break;
            }
            if(counter == stop){
                System.out.println();
                System.out.println("\nThe program has ran through " + counter + " times and generated a schedule of " + optimizedCourseAvg + " optimization value");
                break;
            }

            resetData();


        }
        //Print schedule
        System.out.println();
        for(int row = 0; row < 5; row++){
            for(int col = 0; col < 5; col++){
                if(col != 4) System.out.print(randomSchedule[row][col].getSessionID() + ",");
                else System.out.print(randomSchedule[row][col].getSessionID()+"\n");
            }
        }

        try {
            //File title is optimization value + Schedules.csv so that data can be saved for any optimization value
            //Second argument true creates FileWriter as an appending FileWriter object so data can be saved and not overwritten
            FileWriter myWriter;
            myWriter = new FileWriter("Saved Schedules/" + ((int)(optimizedCourseAvg*10)/10.0) + "Schedules.csv", true);
            for(int row = 0; row < 5; row++){
                for(int col = 0; col < 5; col++){
                    if(col != 4) myWriter.append(randomSchedule[row][col].getSessionID() + ",");
                    else myWriter.append(randomSchedule[row][col].getSessionID()+"\n");
                }
            }
            myWriter.append("\n");
            myWriter.close();
            System.out.println("\nSchedule saved to " + ((int)(optimizedCourseAvg*10)/10.0) + "Schedules.csv");
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }

    public void sortByName(){
        
        for(int i = 0, len = sortedByNameStudents.size(); i < len; i++){
            int currMinIndex = i;
            String currMinimum = sortedByNameStudents.get(i).getName().toLowerCase();

            for(int j = i; j < sortedByNameStudents.size(); j++){
                Student currStudent = sortedByNameStudents.get(j);
                //Lexographically compare currentStudent's name with currMinimum name
                if(currStudent.getName().toLowerCase().compareTo(currMinimum) < 0){
                    //If currStudent's name comes before currMinimum's name then swap the two
                    currMinimum = currStudent.getName().toLowerCase();
                    currMinIndex = j;
                }
            }
            Student temp = sortedByNameStudents.get(i);
            sortedByNameStudents.set(i, sortedByNameStudents.get(currMinIndex));
            sortedByNameStudents.set(currMinIndex, temp);
        }
    
    
        for(int i = 0, len = sortedByNameSeminars.size(); i < len; i++){
            int currMinIndex = i;
            String currMinimum = sortedByNameSeminars.get(i).getSessionName().toLowerCase();

            for(int j = i; j < sortedByNameSeminars.size(); j++){
                Seminar currSeminar = sortedByNameSeminars.get(j);
                //Lexographically compare currentStudent's name with currMinimum name
                if(currSeminar.getSessionName().toLowerCase().compareTo(currMinimum) < 0){
                    //If currStudent's name comes before currMinimum's name then swap the two
                    currMinimum = currSeminar.getSessionName().toLowerCase();
                    currMinIndex = j;
                }
            }
            Seminar temp = sortedByNameSeminars.get(i);
            sortedByNameSeminars.set(i, sortedByNameSeminars.get(currMinIndex));
            sortedByNameSeminars.set(currMinIndex, temp);
        }
        // System.out.println(sortedByNameSeminars.size());
        // for(int j = 0; j < sortedByNameSeminars.size(); j++){
        //     System.out.println(sortedByNameSeminars.get(j).getSessionName());
        // }
    }

    public ArrayList<String[]> sortSplitNames(ArrayList<String[]> splitNames){
        for(int i = 0, len = splitNames.size(); i < len; i++){
            int currMinIndex = i;
            String currMinimum = splitNames.get(i)[0];

            for(int j = i; j < splitNames.size(); j++){
                String currName = splitNames.get(j)[0];
                //Lexographically compare currentStudent's name with currMinimum name
                if(currName.compareTo(currMinimum) < 0){
                    //If currStudent's name comes before currMinimum's name then swap the two
                    currMinimum = splitNames.get(j)[0];
                    currMinIndex = j;
                }
            }
            String[] temp = splitNames.get(i);
            splitNames.set(i, splitNames.get(currMinIndex));
            splitNames.set(currMinIndex, temp);
        }

        return splitNames;
    }

    public int binarySearchByName(String name, int type){
        //This method will search for the name twice, the first time it will look for an exact match
        //of the inputted name but the second time it will search for any matches of first, middle or last name
        Scanner scan = new Scanner(System.in);

        String search = name.toLowerCase();
        int lowIndex = 0; //lower bound of possible values
        int highIndex;
        if(type == 0) highIndex = sortedByNameStudents.size()-1; //upper bound of possible values
        else highIndex = sortedByNameSeminars.size()-1;
        boolean isFound = false; //bool flag for if searching value is not in data set
        int middle, counter = 0;
        String guess;

        //Size of ArrayList is greater than zero
        while(highIndex - lowIndex + 1 > 0){ 
            counter++;
            //Set middle to half of possible ArrayList plus current lower bound
            middle = lowIndex + (highIndex - lowIndex)/2; 
            guess = sortedByNameStudents.get(middle).getName().toLowerCase();

            //check if guess is right
            if(guess.compareTo(search) == 0){
                System.out.println("Found Match: " + guess);
                isFound = true;
                System.out.println("\n" + sortedByNameStudents.get(middle).getName());
                return sortedByNameStudents.get(middle).getStudentID();
            }

            //guess too high then adjust upper bound
            else if(guess.compareTo(search) > 0){
                //If guess is too high then cut the list into half with highIndex at middle - 1
                highIndex = middle - 1;
            }

            //guess too low then adjust lower bound
            else if(guess.compareTo(search) < 0){
                //If guess is too low then cut the list into half with lowIndex at middle + 1
                lowIndex = middle + 1;
            }
        }


        ArrayList<String[]> splitNames = new ArrayList<String[]>();

        if(type == 0){
            for(int i = 0, len = students.size(); i < len; i++){
                String studentID = students.get(i).getStudentID() + "";
                String[] splitStudentName = students.get(i).getName().split(" ");
                for(int j = 0; j < splitStudentName.length; j++){
                    String partialName = splitStudentName[j].toLowerCase();
                    //Deletes the end parenthesis () around a nickname or pronounciation of a name within a partialName
                    if(partialName.length()>0 && partialName.charAt(0) == '(') partialName = partialName.substring(1, partialName.length()-1);
                    //Stores each splitName into an array with both the splitName and which studentID the partial name comes from
                    String[] nameID = {partialName, studentID};
                    splitNames.add(nameID);
                }
            }
        }

        else{
            for(int i = 0, len = seminars.size(); i < len; i++){
                String studentID = seminars.get(i).getSessionID() + "";
                String[] splitSeminarName = seminars.get(i).getSessionName().split(" ");
                for(int j = 0; j < splitSeminarName.length; j++){
                    //Stores each splitName into an array with both the splitName and which studentID the partial name comes from
                    String[] nameID = {splitSeminarName[j].toLowerCase(), studentID};
                    splitNames.add(nameID);
                }
            } 
        }

        splitNames = sortSplitNames(splitNames);

        for(int i = 0; i < splitNames.size(); i++){
            System.out.println(splitNames.get(i)[0] + "  " + splitNames.get(i)[1]);
        }

        String[] splitInputtedName = name.split(" ");


        for(int i = 0; i < splitInputtedName.length; i++){
            lowIndex = 0; //lower bound of possible values
            highIndex = splitNames.size()-1; //upper bound of possible values
            counter = 0;
            search = splitInputtedName[i].toLowerCase();
            //Size of ArrayList is greater than zero
            while(highIndex - lowIndex + 1 > 0){ 
                counter++;
                //Set middle to half of possible ArrayList plus current lower bound
                middle = lowIndex + (highIndex - lowIndex)/2; 
                guess = splitNames.get(middle)[0];
 
                //check if guess is right
                if(guess.compareTo(search) == 0){
                    int ID;
                    if(type == 0) ID = Integer.parseInt(splitNames.get(middle)[1]);
                    else ID = Integer.parseInt(splitNames.get(middle)[1]) - 1;
                    String guessFullName;
                    if(type == 0) guessFullName = students.get(ID).getName();
                    else guessFullName = seminars.get(ID).getSessionName();
                    if(type == 0) System.out.println("Did you mean " + guessFullName + "? (Please respond with y/n)");
                    else System.out.println("Did you mean " + guessFullName + " (Session ID: " + seminars.get(ID).getSessionID() + ")" + "? (Please respond with y/n and note there are duplicates of this session)");
                    String input = scan.nextLine();
                    while(!input.equals("yes") && !input.equals("y") && !input.equals("no") && !input.equals("n")){
                        System.out.println("Please respond with either yes or no");
                        input = scan.nextLine();
                    }
                    if(input.equals("yes") || input.equals("y")){
                        //System.out.println("\n" + guessFullName);
                        return ID;
                    }
                    //if responds with no must search through all possible same partial names and ask
                    else{
                    
                        int guessIndex = middle+1;
                        if(guessIndex > 0 && guessIndex < splitNames.size()) guess = splitNames.get(guessIndex)[0];
                        //Check all possible same guesses by iterating forwards
                        while(guess.equals(search) && guessIndex > 0 && guessIndex < splitNames.size()){
                            if(type == 0) ID = Integer.parseInt(splitNames.get(guessIndex)[1]);
                            else ID = Integer.parseInt(splitNames.get(guessIndex)[1]) - 1;

                            if(type == 0) guessFullName = students.get(ID).getName();
                            else guessFullName = seminars.get(ID).getSessionName();

                            if(type == 0) System.out.println("Did you mean " + guessFullName + "? (Please respond with y/n)");
                            else System.out.println("Did you mean " + guessFullName + " (Session ID: " + seminars.get(ID).getSessionID() + ")" + "? (Please respond with y/n and note there are duplicates of this session)");
                            input = scan.nextLine();
                            while(!input.equals("yes") && !input.equals("y") && !input.equals("no") && !input.equals("n")){
                                System.out.println("Please respond with either yes or no");
                                input = scan.nextLine();
                            }
                            if(input.equals("yes") || input.equals("y")){
                                //System.out.println("\n" + guessFullName);
                                return ID;
                            }
                            guessIndex++;
                            guess = splitNames.get(guessIndex)[0];
                        }

                        //Check all possible same guesses by iterating backwards

                        guessIndex = middle-1;
                        if(guessIndex > 0 && guessIndex < splitNames.size()) guess = splitNames.get(guessIndex)[0];
                        while(guess.equals(search) && guessIndex > 0 && guessIndex < splitNames.size()){
                            if(type == 0) ID = Integer.parseInt(splitNames.get(guessIndex)[1]);
                            else ID = Integer.parseInt(splitNames.get(guessIndex)[1]) - 1;

                            if(type == 0) guessFullName = students.get(ID).getName();
                            else guessFullName = seminars.get(ID).getSessionName();
                            if(type == 0) System.out.println("Did you mean " + guessFullName + "? (Please respond with y/n)");
                            else System.out.println("Did you mean " + guessFullName + " (Session ID: " + seminars.get(ID).getSessionID() + ")" + "? (Please respond with y/n and note there are duplicates of this session)");
                            input = scan.nextLine();
                            while(!input.equals("yes") && !input.equals("y") && !input.equals("no") && !input.equals("n")){
                                System.out.println("Please respond with either yes or no");
                                input = scan.nextLine();
                            }
                            if(input.equals("yes") || input.equals("y")){
                                //System.out.println("\n" + guessFullName);
                                return ID;
                            }
                            guessIndex--;
                            guess = splitNames.get(guessIndex)[0];
                        }

                        //If all possible same guesses are responded with no then return -1 because there is no possible match
                        return -1;
                    }
                    
                }

                //guess too high then adjust upper bound
                else if(guess.compareTo(search) > 0){
                    //If guess is too high then cut the list into half with highIndex at middle - 1
                    highIndex = middle - 1;
            
                }

                //guess too low then adjust lower bound
                else if(guess.compareTo(search) < 0){
                    //If guess is too low then cut the list into half with lowIndex at middle + 1
                    lowIndex = middle + 1;
                
                }
            }
            
        }

        //Return -1 if match was not found in ArrayList
        return -1;
    
    }

    public void printStudentSchedule(){
        Scanner scan = new Scanner(System.in);
        while(true){
            System.out.println();
            System.out.println("Would you like to search up a student's schedule? (Please respond with either yes or no)");
            String input = scan.nextLine();
            while(!input.equals("yes") && !input.equals("y") && !input.equals("no") && !input.equals("n")){
                System.out.println("Please respond with either yes or no");
                input = scan.nextLine();
            }

            if(input.equals("yes") || input.equals("y")){
                System.out.println("Would you like to search up a student by name (first and/or last name) or by student ID? Please enter \"name\" or \"id\": ");
                input = scan.nextLine();
                while(!input.equals("name") && !input.equals("id")){
                    System.out.println("Please respond with either \"name\" or \"id\"");
                    input = scan.nextLine();
                }
                int id;
                if(input.equals("name")){
                    System.out.println("Please enter the name you would like to search: ");
                    input = scan.nextLine();
                    id = binarySearchByName(input, 0);
                    while(id == -1){
                        System.out.println("Match not found please enter a new name to search or press \"q\" to quit: ");
                        input = scan.nextLine();
                        if(input.equals("q")) return;
                        id = binarySearchByName(input, 0);
                    }
                    id++;
                }
                else{
                    System.out.println("Please enter the student ID you would like to search: ");
                    input = scan.nextLine();
                    while(true){
                        try {
                            id = Integer.parseInt(input);
                            if(id < 1 || id > 74) System.out.println("Please enter a valid integer input for student ID from 1-74");
                            else break;
                            input = scan.nextLine();
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter a valid integer input for student ID from 1-74");
                            input = scan.nextLine();
                        }
                    }
                }

                Student student = students.get(id-1);
                System.out.println();
                System.out.println("Student Name: " + student.getName());
                System.out.println("Student Email: " + student.getEmail());
                System.out.println();
                for(int i = 0; i < 5; i++){
                    System.out.println("Block " + (i+1) + ": " + student.getSeminar(i).getSessionName() + " (" + student.getSeminar(i).getSessionID() + ")");
                }
                
            }
            else break;
        }

        
    }

    public void printMasterSchedule(){
        //Print out Session Name and all relevant information

        for(int row = 0; row < 5; row++){
            for(int col = 0; col < 5; col++){

            }
        }
    }

    public void printSessionRoster(){
        Scanner scan = new Scanner(System.in);
        while(true){
            System.out.println();
            System.out.println("Would you like to search up a seminar schedule? (Please respond with either yes or no)");
            String input = scan.nextLine();
            while(!input.equals("yes") && !input.equals("y") && !input.equals("no") && !input.equals("n")){
                System.out.println("Please respond with either yes or no");
                input = scan.nextLine();
            }

            if(input.equals("yes") || input.equals("y")){
                System.out.println("Would you like to search up a seminar by name or by sessionID? Please enter \"name\" or \"id\": ");
                input = scan.nextLine();
                while(!input.equals("name") && !input.equals("id")){
                    System.out.println("Please respond with either \"name\" or \"id\"");
                    input = scan.nextLine();
                }
                int id;
                if(input.equals("name")){
                    System.out.println("Please enter the name you would like to search: ");
                    input = scan.nextLine();
                    id = binarySearchByName(input, 1);
                    while(id == -1){
                        System.out.println("Match not found please enter a new name to search or press \"q\" to quit: ");
                        input = scan.nextLine();
                        if(input.equals("q")) return;
                        id = binarySearchByName(input, 1);
                    }
                    id++;
                }
                else{
                    System.out.println("Please enter the student ID you would like to search: ");
                    input = scan.nextLine();
                    while(true){
                        try {
                            id = Integer.parseInt(input);
                            if(id < 1 || id > 27) System.out.println("Please enter a valid integer input for sessionID from 1-27");
                            else break;
                            input = scan.nextLine();
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter a valid integer input for student ID from 1-27");
                            input = scan.nextLine();
                        }
                    }
                }

                Seminar seminar = seminars.get(id-1);
                System.out.println();
                System.out.println("Seminar Name: " + seminar.getSessionName());
                System.out.println("Presenter Name: " + seminar.getPresenterName());
                System.out.println("Session ID: " + seminar.getSessionID());
                System.out.println();

                System.out.println("Enrolled Students: ");
                for(int i = 0; i < 16; i++){
                    System.out.println(seminar.getStudent(i).getName() + " (StudentID: " + seminar.getStudent(i).getStudentID() + ")");
                }
            }
            else break;
        }
    }


}