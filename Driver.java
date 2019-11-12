import java.util.Random;
import java.util.Vector;

public class Driver{

    public static  int numOfOrangeStudents = 7;
    public static int numOfGreenStudents = 14;
    public static int totalStudents = numOfGreenStudents + numOfOrangeStudents;
    private static Student[] students = new Student[totalStudents];
    private static Student[] greenStudents = new Student[numOfGreenStudents];
    private static Student[] orangeStudents = new Student[numOfOrangeStudents];
    private static int numSeats = 6;
    public static Object canFormGroup = new Object();
    public static Object group = new Object();
    public static int arrivedStudents = 0;
    public static void main(String[] args) {
        int gStudents = 0;
        int Ostudents = 0;
        Random r = new Random();
        int i = 0;
        Student.formGroup.add(new Object());
        // Generate students randomly 
        while(numOfGreenStudents > gStudents || numOfOrangeStudents > Ostudents){
            if(r.nextInt(100) < 50 && numOfOrangeStudents > Ostudents){
                students[i] = new Student(i,true);
                Ostudents++;
            }else{
                students[i] = new Student(i,false);
                gStudents++;
            }
            i++;
        }
        
        for(int j = 0;j < students.length; j++){
            students[j].start();
        }
        
        
    }
}