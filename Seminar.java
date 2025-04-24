public class Seminar {
    String sessionName;
    int sessionID;
    String presenterName;

    int placability = 16;
    int numEnrolled = 0;
    Student[] students = new Student[16];
    int studentsIndex = 0;
    boolean duplicate = false;

    public Seminar(String setSessionName, int setSessionID, String setPresenterName){
        sessionName = setSessionName;
        sessionID = setSessionID;
        presenterName = setPresenterName;
    }
    public void setDuplicate(boolean val){
        duplicate = val;
    }
    public boolean getDuplicate(){
        return duplicate;
    }

    public int getStudentsIndex(){
        return studentsIndex;
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
