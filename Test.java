import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.ArrayList; // Import ArrayList
import java.util.Scanner; //Import Scanner Class

/*
 * Test class just meant to test out some code like the in class portions of printing out a 2d ArrayList and tallying
 */
public class Test{
    public static void main(String[] args){
        ArrayList<ArrayList<Integer>> choice = new ArrayList<ArrayList<Integer>>();
        int[] tally = new int[18];
        //Try catch block for file reading with fileExceptionError Catch
        try {
            //Creation of scanner, file object and index counter
            File studentChoice = new File("studentChoice.csv");
            Scanner scan = new Scanner(studentChoice);
            //Start at -1 to make it easy to get rid of first line of title columns when reading
            int studentIndex = -1;

            //while loop which runs through every line of the csv with scan.hasNextLine()
            while (scan.hasNextLine()) { 
                //create string of data for each row / line of csv
                String data = scan.nextLine();

                //make sure this is not the first title row of csv
                if(studentIndex > -1){
                    //Create arrayList for current student's choices (this will be added to the larger choice 2d ArrayList)
                    ArrayList<Integer> currStudentChoices = new ArrayList<Integer>();
                    choice.add(currStudentChoices);   

                    //Split string of csv with delimiter of comma
                    String[] splitStr = data.split(",");

                    System.out.println(splitStr.length);
                    outer: for(int i = 4; i < splitStr.length; i++){
                        System.out.println(splitStr[i]);
                        //need to use try catch block because there are some values in csv which are just blank so need to catch parseInt error and put a -1 for empty choice
                        try{
                            //add to 2d array of student ids and choices
                            choice.get(studentIndex).add(Integer.parseInt(splitStr[i]));
                            //Add to tally array for each session
                            tally[Integer.parseInt(splitStr[i])-1]++;
                        }
                        catch(NumberFormatException e){
                            //Account for empty choice students at the end of the csv
                            if(i == 4){
                                for(int j = 0; j < 4; j++){
                                    choice.get(studentIndex).add(-1);
                                }
                                break outer;
                            }
                            //If not one of the empty choice students just print out an error and place -1 (this catch should not run for this dataset since everything has been accounted for)
                            else{
                                System.out.println(e);
                                choice.get(studentIndex).add(-1);
                            }
                        }
                    }
                }
                //increment studentIndex to know which student we are on and to not be stuck in infinite loop
                studentIndex++;
            }
            scan.close();
        } 
        
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        //end of initial catch
        

        //Nested for loop to print out 
        for(int i = 0, size = choice.size(); i < size; i++){
            System.out.print(i + ": ");
            for(int j = 0; j < choice.get(i).size(); j++){
                System.out.print(choice.get(i).get(j) + "  ");
            }
            System.out.println();
        }

        //For loop to print out the tally
        for(int i = 0; i < tally.length; i++){
            System.out.println((i+1) + ": " + tally[i] + "  ");
        }

        //A second way to tally the amount of each session because directions said to check with loops and 2d array but I initially did it when reading file
        /*
        int[] tallyCheck = new int[18];
        for(int i = 0, size = choice.size(); i < size; i++){
            for(int j = 0; j < choice.get(i).size(); j++){
                if(choice.get(i).get(j) != -1) tallyCheck[choice.get(i).get(j)-1]++;
            }
        }

        for(int i = 0; i < tallyCheck.length; i++){
            System.out.println((i+1) + ": " + tallyCheck[i] + "  ");
        }
        */
    }
}