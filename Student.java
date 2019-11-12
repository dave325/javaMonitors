import java.util.Vector;

public class Student extends Thread {

    private boolean canFormGroup = false;
    private static int orangeGroup = 0;
    private static int greenGroup = 0;
    private boolean isOrange = false;
    private static int numGroup = 0;
    private static int currentNotifiedGroup = 0;
    private static Object waitGroup = new Object();
    private int id;
    private int groupNum = -1;
    public static Vector<Object> formGroup = new Vector<Object>();
    public static Object lockGroup = new Object();
    public static Object groupTogether = new Object();

    public Student(int i, boolean isOrange) {
        this.id = i;
        this.isOrange = isOrange;
        if (isOrange) {
            System.out.println("Thread " + (i + 1) + " called: Student is from ORANGE school");
        } else {
            System.out.println("Thread " + (i + 1) + " called: Student is from GREEN school");
        }

    }

    public boolean getOrange() {
        return this.isOrange;
    }

    public synchronized void setGreenGroup() {
        synchronized (lockGroup) {
            Driver.arrivedStudents++;
            if (greenGroup < 1) {
                greenGroup++;
                System.out.println("Green Count (" + this.id + "). Current count: " + greenGroup);
                this.canFormGroup = true;
            } else {
                this.canFormGroup = false;
            }
        }
    }

    public synchronized void setOrangeGroup() {
        synchronized (lockGroup) {
            Driver.arrivedStudents++;
            if (orangeGroup < 2) {
                orangeGroup++;
                System.out.println("Orange Count (" + this.id + "). Current count: " + orangeGroup);
                System.out.println(numGroup);
                this.canFormGroup = true;
            } else {
                this.canFormGroup = false;
            }
        }
    }

    private void startMarch() {
        if (Driver.arrivedStudents == Driver.totalStudents && Driver.numOfOrangeStudents < 2
                && Driver.numOfGreenStudents < 1) {
            synchronized (formGroup.get(currentNotifiedGroup)) {
                formGroup.get(currentNotifiedGroup).notifyAll();
                currentNotifiedGroup++;
            }
        }
    }

    private synchronized void resetGroups() {
        synchronized (lockGroup) {
            System.out.println("Resetting");
            orangeGroup = 0;
            greenGroup = 0;
            numGroup++;
            formGroup.add(new Object());
            System.out.println("Group Size " + formGroup.size());
            System.out.println("Number of group " + numGroup);
            System.out.println("students " + Driver.arrivedStudents + ", " + Driver.totalStudents);
        }
        synchronized (waitGroup) {
            waitGroup.notifyAll();
        }
    }

    private synchronized void setGroup() {
        this.groupNum = numGroup;
    }

    public synchronized void runTask() {
        while (true) {
            // Check if basic requirement to form group is established
            if (Driver.numOfOrangeStudents >= 2 && Driver.numOfGreenStudents >= 1) {

                // Check if student is from orange school
                if (this.isOrange) {
                    setOrangeGroup();
                    while (!this.canFormGroup) {
                        System.out.println("Student " + id + " will wait for group to form(orange)");
                        startMarch();
                        synchronized (waitGroup) {
                            try {

                                waitGroup.wait();
                                synchronized (lockGroup) {
                                    orangeGroup++;
                                    this.canFormGroup = true;

                                    if (orangeGroup < 2 && this.canFormGroup) {
                                        System.out.println("Thread " + id + " breaking out of loop(orange).");
                                        break;
                                    }
                                }
                                this.canFormGroup = false;
                                System.out.println("Thread " + id + " continue loop(orange).");
                                continue;
                            } catch (InterruptedException e) {
                                // Race condition

                                System.out.println(e.getMessage());
                                continue;
                            }
                        }
                    }
                    // Check if there is room for orange student to enter group

                    // setOrangeGroup();
                    if (this.canFormGroup) {
                        synchronized(groupTogether){
                            // Set object specific group
                            setGroup();
                            System.out.println("Student " + id + " joined group  " + this.groupNum);
                            // Decrease amount of orange students
                            Driver.numOfOrangeStudents--;
                            // Increase Student orangeGroup count
                            // orangeGroup++;

                            System.out.println("Student " + id + " will wait for to leave. In orange  " + numGroup);
                            System.out.println("Group Count " + orangeGroup + ", " + greenGroup);
                            // Check if you need to reset information (only if current group is full)
                            if (orangeGroup >= 2 && greenGroup >= 1) {

                                resetGroups();
                            }
                            startMarch();
                        }
                        // Synchronize method used to block thread on specific group notification object
                        synchronized (formGroup.get(this.groupNum)) {
                           

                            // wait until all groups are established
                            try {
                                formGroup.get(this.groupNum).wait();
                                System.out.println("Student " + id + " woke up. Now sleeping");
                                Thread.sleep((long) Math.random() * 1000);
                            } catch (InterruptedException ie) {
                                System.out.println("Student " + id + " formgroup interrupted");
                            }
                        }
                    }
                } else {
                    setGreenGroup();
                    while (!this.canFormGroup) {
                        System.out.println("Student " + id + " will wait for group to form(green)");
                        startMarch();
                        synchronized (waitGroup) {
                            try {
                                waitGroup.wait();
                                synchronized (lockGroup) {
                                    greenGroup++;
                                    this.canFormGroup = true;
                                
                                // setGreenGroup();
                                if (greenGroup < 1 && this.canFormGroup) {
                                    System.out.println("Thread " + id + " breaking out of loop(green).");
                                    break;
                                }}
                                System.out.println("Thread " + id + " continue loop(green).");
                                this.canFormGroup = false;
                                continue;
                            } catch (InterruptedException e) {
                                // Race condition

                                System.out.println(e.getMessage());
                                continue;
                            }
                        }
                    }
                    if (this.canFormGroup) {
                        synchronized(groupTogether){
                            // Set object specific group
                            setGroup();
                            System.out.println("Student " + id + " joined group  " + this.groupNum);
                            // Decrease amount of orange students
                            Driver.numOfOrangeStudents--;
                            // Increase Student orangeGroup count
                            // orangeGroup++;

                            System.out.println("Student " + id + " will wait for to leave. In orange  " + numGroup);
                            System.out.println("Group Count " + orangeGroup + ", " + greenGroup);
                            // Check if you need to reset information (only if current group is full)
                            if (orangeGroup >= 2 && greenGroup >= 1) {

                                resetGroups();
                            }
                            startMarch();
                        }
                        synchronized (formGroup.get(this.groupNum)) {
                            // Check if you need to reset information (only if current group is full)
                            try {
                                formGroup.get(this.groupNum).wait();
                                System.out.println("Student " + id + " woke up. Now sleeping");
                                Thread.sleep((long) Math.random() * 1000);
                            } catch (InterruptedException ie) {
                                System.out.println("Student " + id + " formgroup interrupted");
                            }
                        }
                    }
                }
            } else {
                System.out.println(
                        "Driver is full at " + id + "( " + Driver.arrivedStudents + ", " + Driver.totalStudents + ").");

                if (Driver.arrivedStudents == Driver.totalStudents && formGroup.size() - 1 >= currentNotifiedGroup) {
                    synchronized (formGroup.get(currentNotifiedGroup)) {
                        formGroup.get(currentNotifiedGroup).notifyAll();
                    }

                    currentNotifiedGroup++;
                }
                break;
            }
            break;
        }
        System.out.println("Thread " + this.id + " is leaving");

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // while (true) {
        runTask();

    }
    // }

}