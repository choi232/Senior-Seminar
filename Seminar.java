public class Seminar {
    String sessionName;
    int sessionID;
    String presenterName;

    int placability = 16;
    int numEnrolled = 0;
    Student[] students = new Student[16];
    int studentsIndex = 0;
    //SessionID of other duplicate session
    int duplicate = -1;
    //Boolean flag to tell if the class has been cut or not
    boolean isCut = false;
    boolean isFull = false;

    int random;

    public Seminar(String setSessionName, int setSessionID, String setPresenterName){
        sessionName = setSessionName;
        sessionID = setSessionID;
        presenterName = setPresenterName;
    }

    public void setRandom(){
        random = (int)(Math.random()*100);
    }
    public int getRandom(){
        return random;
    }

    public void setIsFull(boolean val){
        isFull = val;
    }
    public boolean getIsFull(){
        return isFull;
    }
    public void setIsCut(boolean val){
        isCut = val;
    }
    public boolean getIsCut(){
        return isCut;
    }
    public void setDuplicate(int val){
        duplicate = val;
    }
    public int getDuplicate(){
        return duplicate;
    }

    public int getStudentsIndex(){
        return studentsIndex;
    }
    public void incrementStudentsIndex(){
        studentsIndex++;
    }

    public Student getStudent(int index){
        return students[index];
    }

    public void addStudent(Student student){
        students[studentsIndex] = student;
        studentsIndex++;
    }

    public void incrementNumEnrolled(int step){
        numEnrolled += step;
    }

    public int getNumEnrolled(){
        return numEnrolled;
    }

    public void setPlacability(int setPlacability){
        placability = setPlacability;
    }

    //Overloaded method of setPlacability to just increment/decrement by set value
    public void incrementPlacability(int step){
        placability += step;
    }

    public int getPlacability(){
        return placability;
    }

    public String getSessionName(){
        return sessionName;
    }

    public int getSessionID(){
        return sessionID;
    }

    public String getPresenterName(){
        return presenterName;
    }


}
