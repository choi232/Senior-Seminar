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
                        //Try block to parseInt and add sessionID's from CSV into choice ArrayList
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
                        //Catch block to catch NumberFormatException
                        catch(NumberFormatException e){
                        }
                    }
                    //Create temp student from all the parsed data
                    Student tempStudent = new Student(name, email, time, studentID, choice);
                    //Add student into students ArrayList
                    students.add(tempStudent);
                }
                //Increment studentIndex
                studentIndex++;
            }

            //Copy over student ArrayList to sorted ArrayLists 
            //Learned to do so from https://www.w3schools.com/java/ref_arraylist_clone.asp
            sortedByPlacabilityStudents = (ArrayList) students.clone();
            sortedByNameStudents = (ArrayList) students.clone();

            //close scanner object
            scan.close();
        } 
        //Catch block to catch FileNotFoundException
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    /*
     * calculatePlacability() function to calculate the placability of each
     * seminar and each student object in seminars and students ArrayList
     */
    public void calculatePlacability(){
        //Placability it the total spots available in the seminar minus the students who want these spots
        //Calculate placability of each seminar
        for(int sessionIndex = 0; sessionIndex < seminars.size(); sessionIndex++){
            seminars.get(sessionIndex).setPlacability(16-seminars.get(sessionIndex).getNumEnrolled());
        }
        //Calculate placability of each student as the sum of all the student's seminar choice's placability
        for(int studentID = 0; studentID < students.size(); studentID++){
            int placability = 16;
            Student currStudent = students.get(studentID);
            //Iterate through student's choices and sum placability of seminar choices
            for(int rank = 0, len = currStudent.getChoiceSize(); rank < len; rank++){
                if(currStudent.getChoice(rank) != -1) placability -= seminars.get(currStudent.getChoice(rank)-1).getNumEnrolled();
                //if student did not pick make placability as highest number possible 400
                else placability = 16*25;
            }
            currStudent.setPlacability(placability);
        }
        
    }

    /*
     * sortStudentsByPlacability() function to sort students
     * by their placability score with a selection sort from lowest to highest
     */
    public void sortStudentsByPlacability(){
        //Calculate placability of all students
        calculatePlacability();

        //Sort each student by placability with selection sort from lowest to highest

        for(int i = 0, len = sortedByPlacabilityStudents.size(); i < len; i++){
            //Variable trackers to find current lowest placability student in unsorted portion of ArrayList
            int currMinIndex = i;
            int currMinimum = sortedByPlacabilityStudents.get(i).getPlacability();
            //Iterate through unsorted portion of ArrayList
            for(int j = i; j < sortedByPlacabilityStudents.size(); j++){
                Student currStudent = sortedByPlacabilityStudents.get(j);
                //compare placability of currStudent to currMinimum
                if(currStudent.getPlacability() < currMinimum){
                    //New currMinimum becomes currStudent if lower
                    currMinimum = currStudent.getPlacability();
                    currMinIndex = j;
                }
            }
            //Switch lowest student at end of each iteration with current position
            Student temp = sortedByPlacabilityStudents.get(i);
            sortedByPlacabilityStudents.set(i, sortedByPlacabilityStudents.get(currMinIndex));
            sortedByPlacabilityStudents.set(currMinIndex, temp);
        }

    }

    /*
     * sortSeminarsByPlacability(ArrayList<Seminar> arrayList) function to sort seminars
     * by their placability score with a selection sort from lowest to highest
     */
    public void sortSeminarsByPlacability(ArrayList<Seminar> arrayList){
        //Sort each seminar by placability with selection sort from lowest to highest
        for(int i = 0, len = arrayList.size(); i < len; i++){
            //Variable trackers to find current lowest placability seminar in unsorted portion of ArrayList
            int currMinIndex = i;
            int currMinimum = arrayList.get(i).getPlacability();
            //Iterate through unsorted portion of ArrayList
            for(int j = i; j < arrayList.size(); j++){
                Seminar currSeminar = arrayList.get(j);
                //compare placability of currSeminar to currMinimum
                if(currSeminar.getPlacability() < currMinimum){
                    //New currMinimum becomes currSeminar if lower
                    currMinimum = currSeminar.getPlacability();
                    currMinIndex = j;
                }
            }
            //Switch lowest seminar at end of each iteration with current position
            Seminar temp = arrayList.get(i);
            arrayList.set(i, arrayList.get(currMinIndex));
            arrayList.set(currMinIndex, temp);
        }
    }


    /*
     * sortSeminarsByRandom(ArrayList<Seminar> arrayList) sorts inputted Seminar ArrayList by random attribute
     * using selection sort from lowest to highest using the random int attribute within the Seminar class
     */
    public void sortSeminarsByRandom(ArrayList<Seminar> arrayList){
        //Sort each seminar by placability with selection sort from lowest to highest
        //Iterate through ArrayList
        for(int i = 0, len = arrayList.size(); i < len; i++){
            //Variable trackers to find current lowest random attribute Seminar in unsorted portion of ArrayList
            int currMinIndex = i;
            int currMinimum = arrayList.get(i).getRandom();
            //Iterate through unsorted portion of ArrayList
            for(int j = i; j < arrayList.size(); j++){
                Seminar currSeminar = arrayList.get(j);
                //compare random attribute of currSeminar to currMinimum
                if(currSeminar.getRandom() < currMinimum){
                    //New currMinimum becomes currSeminar if lower
                    currMinimum = currSeminar.getRandom();
                    currMinIndex = j;
                }
            }
            //Switch lowest random attribute seminar at end of each iteration with current position
            Seminar temp = arrayList.get(i);
            arrayList.set(i, arrayList.get(currMinIndex));
            arrayList.set(currMinIndex, temp);
        }    
    }

    /*
     * isPlaced(Student student, int sessionID) takes in Student student and int sessionID and returns
     * boolean to tell if the student's seminars ArrayList already has the seminar with inputted sessionID
     */
    public boolean isPlaced(Student student, int sessionID){
        //Iterate through placed seminars from student schedule and see if they have a match with the passed argument sessionID
        for(int i = 0, len = student.getSeminarsIndex(); i < len; i++){
            int currSessionID = student.getSeminar(i).getSessionID();
            int duplicateSessionID = student.getSeminar(i).getDuplicate();
            //Check if sessionID matches with current iteration of seminar
            if(currSessionID == sessionID){
                return true;
            }
            //Also check if any duplicates have a match with the argument sessionID as well
            else if(duplicateSessionID == sessionID){
                return true;
            }
        }
        //return false if no matches were found
        return false;
    }

    /*
     * placeStudents(Seminar[][] schedule) takes in a schedule and places students into
     * all the seminars within the schedule and returns a double representative of the average
     * courses requested and received per student
     */
    public double placeStudents(Seminar[][] schedule){
        //How many seminars the student requested and received
        int totalMatchedSeminars = 0;

        //iterates through every student
        for(int studentID = 0; studentID < sortedByPlacabilityStudents.size(); studentID++){
            Student currStudent = sortedByPlacabilityStudents.get(studentID);
            //Quadruple for loop consisting of for loop through students, double for loop for schedule, and also a for loop through Arraylist of Student choices
            //Iterate through all the rows of the schedule
            for(int row = 0; row < 5; row++){
                //isFilled boolean flag tells if the student has been placed in a given timeslot
                boolean isFilled = false;
                //iterate through all the cols of the schedule
                outer: for(int col = 0; col < 5; col++){
                    //iterate through all the choices in the choice ArrayList
                    for(int rank = 0, len = currStudent.getChoiceSize(); rank < len; rank++){
                        //Creation of variables representing current seminar, choice and index
                        Seminar currSeminar = schedule[row][col];
                        int currChoice = currStudent.getChoice(rank);
                        int studentIndex = currSeminar.getStudentsIndex();
                        //If the seminar is full set isFull to true
                        if(studentIndex == 16) currSeminar.setIsFull(true);
                        //If seminar is able to be placed in student then place it
                        if(currChoice == currSeminar.getSessionID() && !isPlaced(currStudent, currChoice) && !currSeminar.getIsFull()){
                            //If seminar can be placed then place the seminar into the student
                            currStudent.addSeminar(currSeminar);
                            //Place the student into the seminar
                            currSeminar.addStudent(currStudent);
                            //increment totalMatchedSeminars
                            totalMatchedSeminars++;
                            //If student is placed for this timeslot then isFilled is true
                            isFilled = true;
                            //break out of current timeslot
                            break outer;
                        }
                        
                    }   
                }

                //If student cannot be placed out of choices then place them into least popular course available in given timeslot
                if(!isFilled){
                    //Create timeslotSeminars the five seminars in this current timeslot
                    ArrayList<Seminar> timeslotSeminars = new ArrayList<Seminar>();
                    for(int col = 0; col < 5; col++){
                        timeslotSeminars.add(schedule[row][col]);
                    }
                    //Sort current timeslot of seminars by placability
                    sortSeminarsByPlacability(timeslotSeminars);

                    //Iterate backwards through sorted array to pick least popular session
                    for(int timeslotIndex = 4; timeslotIndex >= 0; timeslotIndex--){
                        Seminar currSeminar = timeslotSeminars.get(timeslotIndex);
                        //If the seminar has available space place the student into the seminar and seminar into the student
                        if(!currSeminar.getIsFull()){
                            //Place seminar into student
                            currStudent.addSeminar(currSeminar);
                            //Place student into seminar
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
     * createRandomSchedule(ArrayList<Seminar> popularSeminars) returns a Seminar[][] randomSchedule by assigning every seminar a random number
     * and sorting the seminars by this random number from least to greatest with selection sort.
     * Finally, this random order is placed into a schedule of 5x5 in sequentially in the random order placed by the selection sort.
     */
    public Seminar[][] createRandomSchedule(ArrayList<Seminar> popularSeminars){
        //Set the random attribute for all the seminars in seminars ArrayList
        for(int i = 0, len = seminars.size(); i < len; i++){
            seminars.get(i).setRandom();
        }
        //Sort random numbers from lowest to highest to create a new random order to add to schedule in this new pseudorandom sorted order
        sortSeminarsByRandom(popularSeminars);

        //Create new randomSchedule
        Seminar[][] randomSchedule = new Seminar[5][5];
        //Iterate through randomSchedule with a double for loop and set random Seminar
        for(int row = 0, seminarIndex = 0; row < 5; row++){
            for(int col = 0; col < 5; col++){
                //Place random seminar into schedule position
                randomSchedule[row][col] = popularSeminars.get(seminarIndex);
                //Increment seminarIndex
                seminarIndex++;
            }
        }
        //Return randomSchedule now reseeded with random seminars
        return randomSchedule;
    }

    /*
     * resetData() function resets all the changed attributes from placeStudents() function.
     * It basically allows optimizeSchedule to run multiple times by resetting the changed attributes.
     */
    public void resetData(){
        //Iterate through every student and resetSeminars()
        for(int studentID = 0, len = students.size(); studentID < len; studentID++){
            students.get(studentID).resetSeminars();
        }
        //Iterate through every seminar and resetStudents() 
        for(int seminarIndex = 0, len = seminars.size(); seminarIndex < len; seminarIndex++){
            seminars.get(seminarIndex).resetStudents();
            //Make sure all the seminars are set as not full
            seminars.get(seminarIndex).setIsFull(false);
        }
    }

    /*
     * optimizeSchedule() function optimizes a schedule by creating random schedules for however many times the user requests.
     * At each random schedule, we run the placeStudents() function and check if the returned optimization value is higher than or
     * equal to the inputted optimization value. Finally, once all the iterations are run or the optimization value is met (whichever comes first)
     * then we write the optimized schedule into saved schedules CSV.
     */
    public void optimizeSchedule(){
        //Variable for how many times the user wants to run schedules
        int stop;
        //Create scanner object
        Scanner scan = new Scanner(System.in);

        //Print out Senior Seminar introduction
        System.out.println("Welcome to Senior Seminar! This program will optimize a schedule to minimize course conflicts between a student's requested courses and scheduled courses.");
        System.out.println("This program relies on an optimization value which is the average requested courses a student receives out of five.");
        System.out.println("The program seeks to maximize this value by running possible iterations of randomly generated schedules and then running a placing algorithm and calculating the average courses placed.");

        //Prompt and save input and do input validation
        System.out.println("\nTo begin creating a schedule, please enter an integer between one and one million iterations that you would like to run: ");
        String input = scan.nextLine();
        //Input validation for the amount of times the program should run
        while(true){
            //Try block to parseInt and check if stop value is within 1 and 1 million
            try {
                stop = Integer.parseInt(input);
                if(stop < 1 || stop > 1000000) System.out.println("Please enter a valid integer input for how many iterations you would like to run from one to one million: ");
                else break;
                input = scan.nextLine();
            } 
            //Catch block to catch NumberFormatException
            catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer input for how many iterations you would like to run from one to one million: ");
                input = scan.nextLine();
            }
        }

        //Prompt and save input and do input validation
        System.out.println("Please input an optimization value (the program will stop early if this value is reached): ");
        double optimizationValue;
        //Seems like the max value that I can get from my placing algorithm without the computer taking forever is 4.2
        input = scan.nextLine();
        //Input validation for the inputted optimization value the program should try to hit
        while(true){
            //Try block to parseInt and check if optimizationValue is within (0, 5)
            try {
                optimizationValue = Double.parseDouble(input);
                if(optimizationValue < 0 || optimizationValue > 5) System.out.println("Please enter a valid double input from 0 to 5: ");
                else break;
                input = scan.nextLine();
            }
            //Catch block to catch NumberFormatException 
            catch (NumberFormatException e) {
                System.out.println("Please enter a valid double input from 0 to 5: ");
                input = scan.nextLine();
            }
        }

        //ArrayList of top 25 popular seminars (ArrayList accounts for max two sessions and min one session per professor rule)
        ArrayList<Seminar> popularSeminars = new ArrayList<Seminar>();

        //Iterate through seminars and add only seminars that will be used in a schedule to popularSeminars ArrayList
        for(int i = 0, len = seminars.size(); i < len; i++){
            //Check if seminars is to be cut and if not add into top 25 popular seminars
            if(!seminars.get(i).getIsCut()){
                popularSeminars.add(seminars.get(i));
            }
        }

        //Create randomSchedule
        Seminar[][] randomSchedule;

        //Counter to keep track of how many times program is ran
        int counter = 0;

        //Iterate through program and check when to break out of loop
        while(true){
            //Set randomSchedule to return of createRandomSchedule()
            randomSchedule = createRandomSchedule(popularSeminars);
            //currOptimization is the double return of placeStudents()
            double currOptimization = placeStudents(randomSchedule);
            //Increment counter
            counter++;
            
            //For first iteration of program save schedule
            if(counter == 1){
                //Save optimization value
                optimizedCourseAvg = currOptimization;
                //Save schedule
                optimizedSchedule = randomSchedule;
            }

            //If the currOptimization is better than highest optimizedCourseAvg then save currOptimization as new highest
            if(currOptimization > optimizedCourseAvg){
                //Save optimization value
                optimizedCourseAvg = currOptimization;
                //Save schedule
                optimizedSchedule = randomSchedule;
            }

            //If currOptimization is greater than the inputted optimization value then break out of program and print schedule / save data into variables
            if(currOptimization >= optimizationValue){
                System.out.println("\nThe program has ran through " + counter + " times and generated a schedule of " + optimizedCourseAvg + " optimization value");
                break;
            }

            //If the program has run through the total amount of iterations then break out of programa and print / save data of the highest optimized schedule
            if(counter == stop){
                System.out.println();
                System.out.println("\nThe program has ran through " + counter + " times and generated a schedule of " + optimizedCourseAvg + " optimization value");
                break;
            }

            //reset all changed data if this randomSchedule is not good
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

        //Try block to write into saved schedules CSVs
        try {
            //File title is optimization value + Schedules.csv so that data can be saved for any optimization value
            //Second argument true creates FileWriter as an appending FileWriter object so data can be saved and not overwritten
            FileWriter myWriter;
            myWriter = new FileWriter("Saved Schedules/" + ((int)(optimizedCourseAvg*10)/10.0) + "Schedules.csv", true);
            //Iterate through entire schedule and write sessionID of every Seminar in schedule through order
            for(int row = 0; row < 5; row++){
                for(int col = 0; col < 5; col++){
                    //Appends timeslots of seminars into CSV
                    if(col != 4) myWriter.append(randomSchedule[row][col].getSessionID() + ",");
                    else myWriter.append(randomSchedule[row][col].getSessionID()+"\n");
                }
            }
            myWriter.append("\n");
            //Close myWriter FileWriter object
            myWriter.close();
            //Tell user the optimizedCourseAvg and the file has been saved
            System.out.println("\nSchedule saved to " + ((int)(optimizedCourseAvg*10)/10.0) + "Schedules.csv");
          } 
          //Catch block to catch IOException
          catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }

    /*
     * sortByName() sorts sortedByNameStudents ArrayList of Students and Seminars by alphabetical order
     * for further use in binarySearchByName() with a selection sort using lexographical comparison
     */

    public void sortByName(){
        //Sort each student by alphabet with selection sort from lowest to highest
        //Iterate through double for loop of selection sort
        for(int i = 0, len = sortedByNameStudents.size(); i < len; i++){
            //Variable trackers to find current lowest alphabetic student in unsorted portion of ArrayList
            int currMinIndex = i;
            String currMinimum = sortedByNameStudents.get(i).getName().toLowerCase();
            //Iterate through unsorted portion of ArrayList
            for(int j = i; j < sortedByNameStudents.size(); j++){
                Student currStudent = sortedByNameStudents.get(j);
                //Lexographically compare currentStudent's name with currMinimum name
                if(currStudent.getName().toLowerCase().compareTo(currMinimum) < 0){
                    //If currStudent's name comes before currMinimum's name then swap the two
                    currMinimum = currStudent.getName().toLowerCase();
                    currMinIndex = j;
                }
            }
            //Switch lowest student at end of each iteration with current position
            Student temp = sortedByNameStudents.get(i);
            sortedByNameStudents.set(i, sortedByNameStudents.get(currMinIndex));
            sortedByNameStudents.set(currMinIndex, temp);
        }
    
        //Second selection sort for Seminars
        //Sort each seminar by alphabet with selection sort from lowest to highest
        //Iterate through double for loop of selection sort
        for(int i = 0, len = sortedByNameSeminars.size(); i < len; i++){
            //Variable trackers to find current lowest alphabetic seminar in unsorted portion of ArrayList
            int currMinIndex = i;
            String currMinimum = sortedByNameSeminars.get(i).getSessionName().toLowerCase();
            //Iterate through unsorted portion of ArrayList
            for(int j = i; j < sortedByNameSeminars.size(); j++){
                Seminar currSeminar = sortedByNameSeminars.get(j);
                //Lexographically compare currentSeminar's name with currMinimum name
                if(currSeminar.getSessionName().toLowerCase().compareTo(currMinimum) < 0){
                    //If currSeminar's name comes before currMinimum's name then swap the two
                    currMinimum = currSeminar.getSessionName().toLowerCase();
                    currMinIndex = j;
                }
            }
            //Switch lowest seminar at end of each iteration with current position
            Seminar temp = sortedByNameSeminars.get(i);
            sortedByNameSeminars.set(i, sortedByNameSeminars.get(currMinIndex));
            sortedByNameSeminars.set(currMinIndex, temp);
        }
    }

    /*
     * sortSplitNames(ArrayList<String[]> splitNames) sorts splitNames Arraylist<String>
     * by alphabetical order for further use in binarySearchByName() with a selection sort 
     * using lexographical comparison
     */
    public ArrayList<String[]> sortSplitNames(ArrayList<String[]> splitNames){
        for(int i = 0, len = splitNames.size(); i < len; i++){
            //Variable trackers to find current lowest alphabetic partial name in unsorted portion of ArrayList splitNames
            int currMinIndex = i;
            String currMinimum = splitNames.get(i)[0];
            //Iterate through unsorted portion of ArrayList
            for(int j = i; j < splitNames.size(); j++){
                String currName = splitNames.get(j)[0];
                //Lexographically compare currName with currMinimum name
                if(currName.compareTo(currMinimum) < 0){
                    //If currName comes before currMinimum's name then swap the two
                    currMinimum = splitNames.get(j)[0];
                    currMinIndex = j;
                }
            }
            //Switch lowest partial name at end of each iteration with current position
            String[] temp = splitNames.get(i);
            splitNames.set(i, splitNames.get(currMinIndex));
            splitNames.set(currMinIndex, temp);
        }
        //return sorted ArrayList of Strings splitNames
        return splitNames;
    }

    /*
     * binarySearchByName(String name, int type) takes in a name to search for as well as a type either 0 or 1.
     * The type will search either students (0) or seminars (1) depending on int type. Then it will use a binary
     * search to search for the name with a direct match in all of the possible names. If a direct match is not possible
     * the function will split all the seminar/student names as well as the inputted name using a space delimiter and search
     * for any possible matches and ask the user if any of these matches are the seminar or student the user is searching for.
     * The binarySearch disregards any upper or lower case because it converts everything to lower case.
     */
    public int binarySearchByName(String name, int type){
        //This method will search for the name twice, the first time it will look for an exact match
        //of the inputted name but the second time it will search for any matches of first, middle or last name

        //Creation of scanner object
        Scanner scan = new Scanner(System.in);

        //Creation of Necessary Variables for Binary Search
        String search = name.toLowerCase();
        int lowIndex = 0; //lower bound of possible values
        int highIndex; //Upper bound of possible values
        if(type == 0) highIndex = sortedByNameStudents.size()-1; //upper bound of possible values
        else highIndex = sortedByNameSeminars.size()-1;
        boolean isFound = false; //bool flag for if searching value is not in data set
        int middle, counter = 0;
        //Inputted guess
        String guess;

        //FIRST ATTEMPT SEARCH FOR A DIRECT MATCH
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

        //If direct match is not found split all the names of seminar/student
        ArrayList<String[]> splitNames = new ArrayList<String[]>();

        //If type (0) student then iterate through all students and split the names of all the students by space delimiter
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
        
        //If type(1) seminar then iterate through all seminars and split the names of all the seminars by space delimiter
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

        //set splitNames to a sorted version using sortSplitNames
        splitNames = sortSplitNames(splitNames);

        String[] splitInputtedName = name.split(" ");

        //Iterate through binary search with splitInputtedNames by comparing to splitNames ArrayList
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
                    //Save data into ID depending on type 0 or 1
                    if(type == 0) ID = Integer.parseInt(splitNames.get(middle)[1]);
                    else ID = Integer.parseInt(splitNames.get(middle)[1]) - 1;
                    String guessFullName;
                    if(type == 0) guessFullName = students.get(ID).getName();
                    else guessFullName = seminars.get(ID).getSessionName();

                    //User prompting and input validation
                    if(type == 0) System.out.println("Did you mean " + guessFullName + "? (Please respond with y/n)");
                    else System.out.println("Did you mean " + guessFullName + " (Session ID: " + seminars.get(ID).getSessionID() + ")" + "? (Please respond with y/n and note there are duplicates of this session)");
                    String input = scan.nextLine();
                    while(!input.equals("yes") && !input.equals("y") && !input.equals("no") && !input.equals("n")){
                        System.out.println("Please respond with either yes or no");
                        input = scan.nextLine();
                    }
                    if(input.equals("yes") || input.equals("y")){
                        //If a match return ID
                        return ID;
                    }
                    //if responds with no must search through all possible same partial names and ask
                    else{
                    
                        int guessIndex = middle+1;
                        if(guessIndex > 0 && guessIndex < splitNames.size()) guess = splitNames.get(guessIndex)[0];
                        //Check all possible same guesses by iterating forwards
                        while(guess.equals(search) && guessIndex > 0 && guessIndex < splitNames.size()){
                            //Save data into ID depending on type 0 or 1
                            if(type == 0) ID = Integer.parseInt(splitNames.get(guessIndex)[1]);
                            else ID = Integer.parseInt(splitNames.get(guessIndex)[1]) - 1;
                            if(type == 0) guessFullName = students.get(ID).getName();
                            else guessFullName = seminars.get(ID).getSessionName();
                            
                            //User prompting and input validation
                            if(type == 0) System.out.println("Did you mean " + guessFullName + "? (Please respond with y/n)");
                            else System.out.println("Did you mean " + guessFullName + " (Session ID: " + seminars.get(ID).getSessionID() + ")" + "? (Please respond with y/n and note there are duplicates of this session)");
                            input = scan.nextLine();
                            while(!input.equals("yes") && !input.equals("y") && !input.equals("no") && !input.equals("n")){
                                System.out.println("Please respond with either yes or no");
                                input = scan.nextLine();
                            }
                            if(input.equals("yes") || input.equals("y")){
                                //If a match return ID
                                return ID;
                            }
                            //increment guessIndex
                            guessIndex++;
                            //change guess value
                            guess = splitNames.get(guessIndex)[0];
                        }

                        //Check all possible same guesses by iterating backwards
                        guessIndex = middle-1;
                        if(guessIndex > 0 && guessIndex < splitNames.size()) guess = splitNames.get(guessIndex)[0];
                        while(guess.equals(search) && guessIndex > 0 && guessIndex < splitNames.size()){
                            
                            //Save data into ID depending on type 0 or 1
                            if(type == 0) ID = Integer.parseInt(splitNames.get(guessIndex)[1]);
                            else ID = Integer.parseInt(splitNames.get(guessIndex)[1]) - 1;
                            if(type == 0) guessFullName = students.get(ID).getName();
                            else guessFullName = seminars.get(ID).getSessionName();

                            //User prompting and input validation
                            if(type == 0) System.out.println("Did you mean " + guessFullName + "? (Please respond with y/n)");
                            else System.out.println("Did you mean " + guessFullName + " (Session ID: " + seminars.get(ID).getSessionID() + ")" + "? (Please respond with y/n and note there are duplicates of this session)");
                            input = scan.nextLine();
                            while(!input.equals("yes") && !input.equals("y") && !input.equals("no") && !input.equals("n")){
                                System.out.println("Please respond with either yes or no");
                                input = scan.nextLine();
                            }
                            if(input.equals("yes") || input.equals("y")){
                                //If a match return ID
                                return ID;
                            }
                            //decrement guessIndex
                            guessIndex--;
                            //change guess value
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
                    System.out.println("Please enter the sessionID you would like to search: ");
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

                System.out.println(seminar.getStudentsIndex());
                System.out.println("Enrolled Students: ");
                for(int i = 0; i < seminar.getStudentsIndex(); i++){
                    System.out.println(seminar.getStudent(i).getName() + " (StudentID: " + seminar.getStudent(i).getStudentID() + ")");
                }
            }
            else break;
        }
    }


}