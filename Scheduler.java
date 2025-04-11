import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class Scheduler{
    public Scheduler(){

        try {
            File studentChoice = new File("csv/studentChoice.csv");
            File presenter = new File("csv/presenter.csv");
            Scanner scan = new Scanner(studentChoice);
            while (scan.hasNextLine()) {
                String data = scan.nextLine();
                String[] array = data.split(",");
                int time = Integer.parseInt(array[0]);
                String name = array[1];
                String email = array[2];
                int[] choice;
                for(int i = 3; i < array.length; i++){
                    choice[i] = array[i];
                }
            }
            scan = new Scanner(presenter);
            scan.close();
        } 
        
        catch (FileNotFoundException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
        }
  
    }
}