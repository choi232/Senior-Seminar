import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SeniorSeminar {

    ArrayList<Student> students = new ArrayList<Student>();

    public SeniorSeminar(){

    }
    loadCSV();
}

public void loadCSV(){

    try {
        File studentChoice = new File("studentChoice.csv");
        Scanner scan = new Scanner(myObj);
        while (scan.hasNextLine()) {
            String data = scan.nextLine();
            System.out.println(data);
        }
        scan.close();
    } 
    catch (FileNotFoundException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
    }

}

public void runSeniorSeminar(){

}

public void unweighted(){

}

public void weighted(){

}
