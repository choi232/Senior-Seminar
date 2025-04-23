public class Seminar {
    String sessionName;
    int sessionID;
    String presenterName;
    int spots;
    int placability;
    Student[] unweightedStudents = new Student[5];
    Student[] weightedStudents = new Student[5];
    public Seminar(String setSessionName, int setSessionID, String setPresenterName, int setSpots){
        sessionName = setSessionName;
        sessionID = setSessionID;
        presenterName = setPresenterName;
        spots = setSpots;
    }


    public void incrementPlacability(){
        placability++;
    }
    public void setPlacability(int setPlacability){
        placability = setPlacability;
    }
    public int getPlacability(){
        return placability;
    }
    public int getSpots(){
        return spots;
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

    public Student getWeightedStudent(int index){
        return weightedStudents[index];
    }

    public void setWeightedStudent(int index, Student student){
        weightedStudents[index] = student;
    }

    public Student getUnweightedStudent(int index){
        return unweightedStudents[index];
    }

    public void setUnweightedStudent(int index, Student student){
        unweightedStudents[index] = student;
    }
}
