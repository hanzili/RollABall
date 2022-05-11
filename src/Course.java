import java.util.Arrays;
public class Course {
    public String code;
    public int capacity;
    public SLinkedList<Student>[] studentTable;
    public int size;
    public SLinkedList<Student> waitlist;

    public Course(String code) {
        this.code = code;
        this.studentTable = new SLinkedList[10];
        this.size = 0;
        this.waitlist = new SLinkedList<Student>();
        this.capacity = 10;
    }

    public Course(String code, int capacity) {
        this.code = code;
        this.studentTable = new SLinkedList[capacity];
        this.size = 0;
        this.waitlist = new SLinkedList<>();
        this.capacity = capacity;
    }

    // go over each slot of the table and create a SLinkedList object there
    public static void initialize(SLinkedList[] studentTable) {
        for (int i = 0; i < studentTable.length; i++) {
            if (studentTable[i] == null) {
                studentTable[i] = new SLinkedList();
            }
        }
    }
    
    public void changeArrayLength(int m) {
        // create of new list of length m
        SLinkedList[] new_arr = new SLinkedList[m];
        // go over each student in the old array and add it to the new array
        for (SLinkedList<Student> student_list : this.studentTable) {
            int studentNum = 0;
            if (student_list != null) {
                studentNum = student_list.size();
            }
            for (int i = 0; i < studentNum; i++) {
                int index = student_list.get(i).id % m;
                if (new_arr[index] == null) {
                    new_arr[index] = new SLinkedList();
                }
                new_arr[index].addLast(student_list.get(i));
            }
        }
        // update studentTable
        this.studentTable = new_arr;
    }

    public boolean put(Student s) {
        // Initialize the linkedlist in case it is empty
        initialize(this.studentTable);
        // check if the student is allowed to register
        SLinkedList<String> course_codes = s.courseCodes;
        for (int i = 0; i < course_codes.size(); i++) {
            if (s.isRegisteredOrWaitlisted(this.code) || course_codes.get(i) == this.code
                    || course_codes.size() == s.COURSE_CAP) {
                return false;
            }
        }
        // if three cases are not true
        int index = s.id % this.capacity;
        int waitlist_capacity = (int) (this.capacity * 0.5);
        // add student object to studentTable directly if capacity is enough
        if (this.size < this.capacity) {
            this.studentTable[index].addLast(s);
            // update the size if adding successfully
            this.size++;
        }
        // add it to waitlist if the registered list is full
        else {
            if (waitlist.size() < waitlist_capacity) {
                this.waitlist.addLast(s);
            } else {
                // if the waitlist is already full, expand the registered list
                changeArrayLength((int) (capacity * 1.5));
                // upadate the capacity
                this.capacity = (int) (capacity * 1.5);
                //update the waitlist capacity
                waitlist_capacity = (int) (this.capacity * 0.5);
                int waitlist_size = this.waitlist.size();
                // Initialize the linkedlist in case it is empty
                initialize(this.studentTable);
                // move the waitist student to the registertable after expanding
                for (int i = 0; i < waitlist_capacity && this.size < this.capacity; i++) {
                    Student waitlist_student = waitlist.getFirst();
                    this.studentTable[waitlist_student.id % this.capacity].addLast(waitlist_student);
                    this.size++;
                    this.waitlist.removeFirst();
                }
                // after all, add this student to the course finally
                if (this.size < this.capacity) {
                    this.studentTable[index].addLast(s);
                    this.size++;
                }
                else {
                    this.waitlist.addLast(s);
                }
            }
        }
        // after adding the student to the table, also add the coursecode to the student
        s.addCourse(this.code);
        return true;
    }


    public Student get(int id) {
        // Initialize the linkedlist in case it is empty
        initialize(this.studentTable);
        // find id in studentTable
        for (SLinkedList<Student> student_list : this.studentTable) {
            for (int i = 0; i < student_list.size(); i++) {
                if (student_list.get(i).id == id) {
                    return student_list.get(i);
                }
            }
        }
        // find id in waitlist
        for (int i = 0; i < waitlist.size(); i++) {
            if (waitlist.get(i).id == id) {
                return waitlist.get(i);
            }
        }
        return null;
    }

    public Student remove(int id) {
        // Initialize the linkedlist in case it is empty
        initialize(this.studentTable);
        // find id in studentTable
        Student removed_student = null;
        for (SLinkedList<Student> student_list : this.studentTable) {
            for (int i = 0; i < student_list.size(); i++) {
                if (student_list.get(i).id == id) {
                    // remove the student from the original table
                    removed_student = student_list.remove(i);
                    // drop this course in student's course list
                    removed_student.dropCourse(this.code);
                    // if removed successfully, update the size
                    this.size--;
                }
                // if we find a student, remove it from course list
                // remove the first student from waitlist and add it to course list
                if (removed_student != null && !waitlist.isEmpty()) {
                    student_list.add(i, waitlist.removeFirst());
                    // upadate the size
                    this.size++;
                    // update the removed student
                    removed_student = null;
                }
            }
        }
        // find id in waitlist
        for (int i = 0; i < waitlist.size(); i++) {
            if (waitlist.get(i).id == id) {
                removed_student = waitlist.remove(i);
                // drop this course in student's course list
                removed_student.dropCourse(this.code);
                // remove this student from the list and return it
                return remove(i);
            }
        }
        // return the student if it is not null
        if (removed_student != null) {
            return removed_student;
        }
        return null;
    }

    public int getCourseSize() {
        // return the size of the course
        return this.size;
    }

    public int[] getRegisteredIDs() {
        initialize(this.studentTable);
        int registeredSize = this.getCourseSize();
        // create a list that has same size as the studentTable
        int[] registedId = new int[registeredSize];
        if (registeredSize > 0) {
            // variable d count the number of slot
            int d = 0;
            for (SLinkedList<Student> student_list : this.studentTable) {
                // variable i count the number of student
                for (int i = 0; i < student_list.size(); i++) {
                    registedId[d] = student_list.get(i).id;
                    d++;
                }
            }
            return registedId;
        }
        return null;
    }

    public Student[] getRegisteredStudents() {
        int registeredSize = this.getCourseSize();
        // create a list that has same size as the studentTable
        Student[] registeredStudent = new Student[registeredSize];
        if (registeredSize > 0) {
            int d = 0;
            for (SLinkedList<Student> student_list : this.studentTable) {
                for (int i = 0; i < student_list.size(); i++) {
                    registeredStudent[d] = student_list.get(i);
                    d++;
                }
            }
            return registeredStudent;
        }
        return null;
    }

    public int[] getWaitlistedIDs() {
        int wailistSize = this.waitlist.size();
        // create a list that has same size as the waitlist
        if (wailistSize > 0) {
            int[] waitListId = new int[wailistSize];
            for (int i = 0; i < wailistSize; i++) {
                waitListId[i] = this.waitlist.get(i).id;
            }
            return waitListId;
        }
        return null;
    }

    public Student[] getWaitlistedStudents() {
        int wailistSize = this.waitlist.size();
        // create a list that has same size as the waitlist
        if (wailistSize > 0) {
            Student[] waitListstudent = new Student[wailistSize];
            for (int i = 0; i < wailistSize; i++) {
                waitListstudent[i] = this.waitlist.get(i);
            }
            return waitListstudent;
        }
        return null;
    }

    public String toString() {
        String s = "Course: " + this.code + "\n";
        s += "--------\n";
        for (int i = 0; i < this.studentTable.length; i++) {
            s += "|" + i + "     |\n";
            s += "|  ------> ";
            SLinkedList<Student> list = this.studentTable[i];
            if (list != null) {
                for (int j = 0; j < list.size(); j++) {
                    Student student = list.get(j);
                    s += student.id + ": " + student.name + " --> ";
                }
            }
            s += "\n--------\n\n";
        }

        return s;
    }

}

