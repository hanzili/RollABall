import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.*;
import java.util.stream.Collectors;

class Mock_Remove extends Course {

    public Mock_Remove(String code){ super(code); }

    public Mock_Remove(String code, int capacity){
        super(code, capacity);
    }

    public boolean put(Student s) {
        if(s.courseCodes.size() >= s.COURSE_CAP || s.isRegisteredOrWaitlisted(this.code))
            return false;

        s.addCourse(this.code);

        int waitListCap = (int) (0.5*this.capacity);
        if (this.size == this.capacity && this.waitlist.size() == waitListCap) {
            expandStudentTable();
        }

        if (this.size < this.capacity){
            return addStudentToStudentTable(s);
        } else {
            if (this.waitlist.getIndexOf(s) == -1) {
                this.waitlist.addLast(s);
                return true;
            } else
                return false;
        }
    }

    private void expandStudentTable() {
        // triggered when wait list is full
        int waitlistCap = (int) (0.5*this.capacity);
        int newCapacity = (int) (1.5*this.capacity);
        int numToAdd = Math.min(waitlistCap, newCapacity - this.capacity);

        this.changeArrayLength(newCapacity);
        this.capacity = newCapacity;

        for (int i = 0; i < numToAdd; i++) {
            Student student = this.waitlist.removeFirst();
            addStudentToStudentTable(student);
        }
    }



    private boolean addStudentToStudentTable(Student s) {
        int bucket = s.id % this.studentTable.length;
        if (this.studentTable[bucket] == null)
            this.studentTable[bucket] = new SLinkedList<Student>();

        if (this.studentTable[bucket].getIndexOf(s) == -1) {
            this.studentTable[bucket].addLast(s);
            this.size++;
            return true;
        }
        return false;
    }
}



class Mock_Put extends Course {

    public Mock_Put(String code){ super(code); }

    public Mock_Put(String code, int capacity){
        super(code, capacity);
    }

    public void changeArrayLength(int m) {
        SLinkedList<Student>[] newListArray = new SLinkedList[m];
        for (SLinkedList<Student> list: this.studentTable
        ) {
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    Student student = list.get(i);
                    int bucket = student.id % m;
                    if (newListArray[bucket] != null)
                        newListArray[bucket].addLast(student);
                    else {
                        SLinkedList<Student> newList = new SLinkedList<Student>();
                        newList.addLast(student);
                        newListArray[bucket] = newList;
                    }
                }
            }
        }
        this.studentTable = newListArray;
    }
}


public class EdTests {
    Course c1; Course c2; Course c3; Course c4; Course c5; Course c6; Course c7; Course c8; Course c9;
    Student s1; Student s2; Student s3; Student s4; Student s5; Student s6; Student s7; Student s8; Student s12;

    @BeforeEach
    void setUp() {
        this.c1 = new Course("RUSS 224",3);
        this.c2 = new Course("COMP 230");
        this.c3 = new Course("COMP 302");
        this.c4 = new Course("MATH 235", 4);
        this.c5 = new Course("PHIL 242", 5);
        this.c6 = new Mock_Put("COMP 250", 4);
        this.c7 = new Mock_Remove("Math 338",4);
        this.c8 = new Mock_Remove("PHYS 224",3);
        this.c9 = new Mock_Remove("SOCI 377",2);
        this.s1 = new Student(1,"Phoebe");
        this.s2 = new Student(2,"Toledo");
        this.s3 = new Student(3,"Silberman");
        this.s4 = new Student(4,"Ponyo");
        this.s5 = new Student(5,"Adrianne");
        this.s6 = new Student(6,"Dorian");
        this.s7 = new Student(7,"Keeley");
        this.s8 = new Student(8,"Fonda");
        this.s12 = new Student(12,"Fleabag");
    }


    //Basic remove check
    @Test
    @Tag("score:3")
    @DisplayName("Remove Test 1")
    void testRemove1() {
        this.c7.put(s1);
        assertEquals(s1, this.c7.remove(1));
        assertEquals(0, this.c1.size);
    }

    //Student to remove not in course
    @Test
    @Tag("score:3")
    @DisplayName("Remove Test 2")
    void testRemove2() {
        this.c7.put(s1);
        assertNull(this.c7.remove(0));
        assertEquals(1, this.c7.size);
    }

    //Student to remove in bucket
    @Test
    @Tag("score:4")
    @DisplayName("Remove Test 3")
    void testRemove3() {
        this.c7.put(new Student(9, "a"));
        this.c7.put(s5);
        this.c7.put(s1);
        assertEquals(s5, this.c7.remove(5));
        assertEquals(2, this.c7.size);
    }

    @Test
    @Tag("private")
    @Tag("score:3")
    @DisplayName("Remove Test 4")
    void testRemove4() {
        this.c7.put(s1);
        this.c7.put(s2);
        this.c7.put(s3);
        this.c7.put(s4);
        this.c7.put(s5);
        this.c7.put(s6);
        this.c7.put(s7);
        this.c7.put(new Student(8, "a"));
        this.c7.put(new Student(9, "a"));
        this.c7.put(new Student(10, "a"));
        this.c7.put(new Student(11, "a"));
        assertNull(this.c7.remove(0));
        assertEquals(s5, this.c7.remove(5));
        assertEquals(s1, this.c7.remove(1));
        assertEquals(s3, this.c7.remove(3));
        assertEquals(s2, this.c7.remove(2));
        assertEquals(s4, this.c7.remove(4));
    }

    //Student course code updated
    @Test
    @Tag("private")
    @Tag("score:2")
    @DisplayName("Remove Test 5")
    void testRemove5() {
        this.c7.put(s1);
        this.c7.put(s2);
        this.c7.put(s3);
        assertEquals(s2, this.c7.remove(2));
        assertEquals(2, this.c7.size);
        assertEquals(0, this.c7.waitlist.size());
        assertEquals(0, s2.courseCodes.size());
    }

    //Student to remove in waitlist
    @Test
    @Tag("private")
    @Tag("score:2")
    @DisplayName("Remove Test 6")
    void testRemove6() {
        this.c7.put(s1);
        this.c7.put(s2);
        this.c7.put(s3);
        this.c7.put(s4);
        this.c7.put(s5);
        assertEquals(s5, this.c7.remove(5));
        assertEquals(4, this.c7.size);
        assertEquals(0, this.c7.waitlist.size());
        assertEquals(0, s5.courseCodes.size());
    }

    //Multiple students in waitlist
    @Test
    @Tag("private")
    @Tag("score:3")
    @DisplayName("Remove Test 7")
    void testRemove7() {
        this.c7.put(s1);
        this.c7.put(s2);
        this.c7.put(s3);
        this.c7.put(s4);
        this.c7.put(s5);
        this.c7.put(s6);
        assertEquals(s1, this.c7.remove(1));
        assertEquals(4, this.c7.size);
        assertEquals(1, this.c7.waitlist.size());
        assertEquals(0, s1.courseCodes.size());
        assertEquals(s5, this.c7.studentTable[1].getFirst());

        assertEquals(s2, this.c7.remove(2));
        assertEquals(4, this.c7.size);
        assertEquals(0, this.c7.waitlist.size());
        assertEquals(0, s2.courseCodes.size());
        assertEquals(s6, this.c7.studentTable[2].getFirst());
    }



    
    //Basic change check
    @Test
    @Tag("score:6")
    @DisplayName("ChangeArrayLength Test 1")
    void testChangeArrayLength1() {
        this.c9.put(s2);
        this.c9.put(s4);
        c9.changeArrayLength(3);
        assertEquals(3, this.c9.studentTable.length);
        assertNotNull(this.c9.studentTable[1]);
        assertEquals(s4, this.c9.studentTable[1].getFirst());
        assertNotNull(this.c9.studentTable[2]);
        assertEquals(s2, this.c9.studentTable[2].getFirst());
    }


    @Test
    @Tag("private")
    @Tag("score:3")
    @DisplayName("ChangeArrayLength Test 2")
    void testChangeArrayLength2() {
        this.c8.put(s4);
        this.c8.put(s8);
        this.c8.put(s12);
        c8.changeArrayLength(4);
        assertEquals(4, this.c8.studentTable.length);
        assertEquals(3, this.c8.size);
        assertNotNull(this.c8.studentTable[0]);
        assertEquals(3, this.c8.studentTable[0].size());
        assertNotEquals(-1, this.c8.studentTable[0].getIndexOf(s4));
        assertNotEquals(-1, this.c8.studentTable[0].getIndexOf(s8));
        assertNotEquals(-1, this.c8.studentTable[0].getIndexOf(s12));
    }

    @Test
    @Tag("private")
    @Tag("score:3")
    @DisplayName("ChangeArrayLength Test 3")
    void testChangeArrayLength3() {
        this.c8.put(s3);
        this.c8.put(s5);
        c8.changeArrayLength(10);
        assertEquals(10, this.c8.studentTable.length);
        assertEquals(2, this.c8.size);
        assertNotNull(this.c8.studentTable[3]);
        assertNotNull(this.c8.studentTable[5]);
        assertEquals(s3, this.c8.studentTable[3].getFirst());
        assertEquals(s5, this.c8.studentTable[5].getFirst());
    }

    //With waitlist and shrink after expand
    @Test
    @Tag("private")
    @Tag("score:3")
    @DisplayName("ChangeArrayLength Test 4")
    void testChangeArrayLength4() {
        this.c8.put(s1);
        this.c8.put(s2);
        this.c8.put(s3);
        this.c8.put(s5);
        this.c8.put(s4);
        c8.changeArrayLength(10);
        assertEquals(10, this.c8.studentTable.length);
        assertEquals(4, this.c8.size);
        assertNotNull(this.c8.studentTable[1]);
        assertNotNull(this.c8.studentTable[2]);
        assertNotNull(this.c8.studentTable[3]);
        assertNotNull(this.c8.studentTable[5]);
        assertEquals(s1, this.c8.studentTable[1].getFirst());
        assertEquals(s2, this.c8.studentTable[2].getFirst());
        assertEquals(s3, this.c8.studentTable[3].getFirst());
        assertEquals(s5, this.c8.studentTable[5].getFirst());
        assertEquals(1, this.c8.waitlist.size());
        assertEquals(s4, this.c8.waitlist.getFirst());

        c8.changeArrayLength(2);
        assertEquals(2, this.c8.studentTable.length);
        assertEquals(4, this.c8.size);
        assertNotNull(this.c8.studentTable[0]);
        assertNotNull(this.c8.studentTable[1]);
        assertNotEquals(-1, this.c8.studentTable[0].getIndexOf(s2));
        assertEquals(-1, this.c8.studentTable[1].getIndexOf(s2));
        assertNotEquals(-1, this.c8.studentTable[1].getIndexOf(s1));
        assertNotEquals(-1, this.c8.studentTable[1].getIndexOf(s3));
        assertNotEquals(-1, this.c8.studentTable[1].getIndexOf(s5));
    }





    //Basic size check
    @Test
    @Tag("score:1")
    @DisplayName("GetCourseSize Test 1")
    void testGetCourseSize1() {
        assertEquals(0, this.c1.getCourseSize());
        assertTrue(this.c1.put(s1));
        assertEquals(1, this.c1.getCourseSize());
    }


    //With remove
    @Test
    @Tag("score:1")
    @DisplayName("GetCourseSize Test 2")
    void testGetCourseSize2() {
        assertEquals(0, this.c1.getCourseSize());
        assertTrue(this.c1.put(s1));
        assertEquals(1, this.c1.getCourseSize());
        this.c1.remove(1);
        assertEquals(0, this.c1.getCourseSize());
    }

    //Do not include waitlist
    @Test
    @Tag("score:1")
    @DisplayName("GetCourseSize Test 3")
    void testGetCourseSize3() {
        assertEquals(0, this.c4.getCourseSize());
        assertTrue(this.c4.put(s1));
        assertEquals(1, this.c4.getCourseSize());
        assertTrue(this.c4.put(s2));
        assertEquals(2, this.c4.getCourseSize());
        assertTrue(this.c4.put(s3));
        assertEquals(3, this.c4.getCourseSize());
        assertTrue(this.c4.put(s5));
        assertEquals(4, this.c4.getCourseSize());
        assertTrue(this.c4.put(s4));
        assertEquals(4, this.c4.getCourseSize());
    }

    @Test
    @Tag("score:2")
    @DisplayName("GetCourseSize Test 4")
    void testGetCourseSize4() {
        assertEquals(0, this.c4.getCourseSize());
        assertTrue(this.c4.put(s1));
        assertEquals(1, this.c4.getCourseSize());
        assertTrue(this.c4.put(s2));
        assertEquals(2, this.c4.getCourseSize());
        assertTrue(this.c4.put(s3));
        assertEquals(3, this.c4.getCourseSize());
        assertTrue(this.c4.put(s5));
        assertEquals(4, this.c4.getCourseSize());
        assertTrue(this.c4.put(s4));
        assertEquals(4, this.c4.getCourseSize());

        c4.remove(4);
        assertEquals(4, this.c4.getCourseSize());
        c4.remove(3); c4.remove(2); c4.remove(1); c4.remove(5);
        assertEquals(0, this.c4.getCourseSize());
        c4.remove(5);
        assertEquals(0, this.c4.getCourseSize());
    }



    //Basic
    @Test
    @Tag("score:3")
    @DisplayName("GetWaitlistedIDs Test 1")
    void testGetWaitlistedIDs1() {
        this.c7.put(s2);
        this.c7.put(s1);
        this.c7.put(s3);
        this.c7.put(s4);
        this.c7.put(s6);
        this.c7.put(s7);
        Set<Integer> expected = Set.of(6,7);
        Set<Integer> actual = Arrays.stream(this.c7.getWaitlistedIDs()).boxed().collect(Collectors.toSet());
        assertEquals(expected, actual);
    }

    //no students
    @Test
    @Tag("private")
    @Tag("score:1")
    @DisplayName("GetWaitlistedIDs Test 2")
    void testGetWaitlistedIDs2() {
        this.c7.put(s2);
        this.c7.put(s1);
        this.c7.put(s3);
        this.c7.put(s4);
        Set<Integer> expected = Set.of();
        Set<Integer> actual = Arrays.stream(this.c7.getWaitlistedIDs()).boxed().collect(Collectors.toSet());
        try {
            assertEquals(expected, actual);
        }
        catch(AssertionError e){assertNull(actual);}
    }

    @Test
    @Tag("private")
    @Tag("score:2")
    @DisplayName("GetWaitlistedIDs Test 3")
    void testGetWaitlistedIDs3() {
        this.c7.put(s1);
        this.c7.put(s2);
        this.c7.put(s3);
        this.c7.put(s4);
        this.c7.put(s12);
        this.c7.put(s6);
        this.c7.put(s7);
        this.c7.put(s8);
        Set<Integer> expected = Set.of(7, 8);
        Set<Integer> actual = Arrays.stream(this.c7.getWaitlistedIDs()).boxed().collect(Collectors.toSet());
        assertEquals(expected, actual);
    }



    //Basic add check
    @Test
    @Tag("score:2")
    @DisplayName("Put Test 1")
    void testPut1() {
        assertTrue(this.c1.put(s1));
        assertEquals(1, this.c1.size);
        assertNotNull(this.c1.studentTable[1]);
        assertEquals(s1, this.c1.studentTable[1].getFirst());
    }

    //Adding student twice
    @Test
    @Tag("score:2")
    @DisplayName("Put Test 2")
    void testPut2() {
        assertTrue(this.c1.put(s1));
        assertFalse(this.c1.put(s1));
        assertEquals(1, this.c1.size);
    }

    //Adding student to waitlist
    @Test
    @Tag("score:3")
    @DisplayName("Put Test 3")
    void testPut3() {
        assertTrue(this.c1.put(s1));
        assertTrue(this.c1.put(s2));
        assertTrue(this.c1.put(s3));
        assertTrue(this.c1.put(s4));
        assertEquals(3, this.c1.size);
        assertEquals(1, this.c1.waitlist.size());
        assertEquals(s4, this.c1.waitlist.getFirst());
    }

    //Adding student to same bucket
    @Test
    @Tag("score:3")
    @DisplayName("Put Test 4")
    void testPut4() {
        assertTrue(this.c1.put(s1));
        assertTrue(this.c1.put(s4));
        assertEquals(2, this.c1.size);
        assertNotNull(this.c1.studentTable[1]);
        assertEquals(2, this.c1.studentTable[1].size());
        assertEquals(0, this.c1.waitlist.size());
        assertNotEquals(-1, this.c1.studentTable[1].getIndexOf(s1));
        assertNotEquals(-1, this.c1.studentTable[1].getIndexOf(s4));
    }

    //Waitlist order
    @Test
    @Tag("private")
    @Tag("score:2")
    @DisplayName("Put Test 5")
    void testPut5() {
        assertTrue(this.c4.put(s1));
        assertTrue(this.c4.put(s2));
        assertTrue(this.c4.put(s3));
        assertTrue(this.c4.put(s4));
        assertTrue(this.c4.put(s5));
        assertTrue(this.c4.put(s6));
        assertEquals(4, this.c4.size);
        assertEquals(2, this.c4.waitlist.size());
        assertEquals(s5, this.c4.waitlist.getFirst());
        assertEquals(s6, this.c4.waitlist.get(1));
    }

    //Waitlist and table full -> expand
    @Test
    @Tag("private")
    @Tag("score:3")
    @DisplayName("Put Test 6")
    void testPut6() {
        assertTrue(this.c6.put(s1));
        assertTrue(this.c6.put(s2));
        assertTrue(this.c6.put(s3));
        assertTrue(this.c6.put(s4));
        assertTrue(this.c6.put(s5));
        assertTrue(this.c6.put(s6));
        assertEquals(4, this.c6.size);
        assertEquals(2, this.c6.waitlist.size());
        assertEquals(s5, this.c6.waitlist.getFirst());
        assertEquals(s6, this.c6.waitlist.get(1));

        assertTrue(this.c6.put(s7));

        assertEquals(1, this.c6.waitlist.size());
        assertEquals(s7, this.c6.waitlist.getFirst());
        assertEquals(6, this.c6.size);
    }


    @Test
    @Tag("private")
    @Tag("score:1")
    @DisplayName("Put Test 7")
    void testPut7() {
        assertTrue(this.c6.put(s1));
        assertTrue(this.c6.put(new Student(9, "a")));
        assertTrue(this.c6.put(new Student(13, "a")));
        assertTrue(this.c6.put(new Student(17, "a")));
        assertTrue(this.c6.put(s5));
        assertTrue(this.c6.put(s6));
        assertEquals(4, this.c6.size);
        assertEquals(2, this.c6.waitlist.size());
        assertEquals(s5, this.c6.waitlist.getFirst());
        assertEquals(s6, this.c6.waitlist.get(1));
        assertEquals(4, this.c6.studentTable[1].size());

        assertTrue(this.c6.put(s7));

        assertEquals(1, this.c6.waitlist.size());
        assertEquals(s7, this.c6.waitlist.getFirst());
        assertEquals(6, this.c6.size);
        assertEquals(2, this.c6.studentTable[1].size());
    }

    //Student course code updated
    @Test
    @Tag("private")
    @Tag("score:1")
    @DisplayName("Put Test 8")
    void testPut8() {
        assertEquals(0, s1.courseCodes.size());
        assertTrue(this.c1.put(s1));
        assertTrue(this.c2.put(s1));
        assertTrue(this.c3.put(s1));

        assertEquals(3, s1.courseCodes.size());
    }

    //Adding student to 4 courses
    @Test
    @Tag("private")
    @Tag("score:2")
    @DisplayName("Put Test 9")
    void testPut9() {
        assertTrue(this.c1.put(s1));
        assertTrue(this.c2.put(s1));
        assertTrue(this.c3.put(s1));
        assertFalse(this.c4.put(s1));
        assertEquals(1, this.c1.size);
        assertEquals(1, this.c2.size);
        assertEquals(1, this.c3.size);
        assertEquals(0, this.c4.size);
    }

    //Adding student twice who was in waitlist
    @Test
    @Tag("private")
    @Tag("score:1")
    @DisplayName("Put Test 10")
    void testPut10() {
        assertTrue(this.c5.put(s1));
        assertTrue(this.c5.put(s2));
        assertTrue(this.c5.put(s3));
        assertTrue(this.c5.put(s4));
        assertTrue(this.c5.put(s5));
        assertTrue(this.c5.put(s6));
        assertFalse(this.c5.put(s6));
        assertEquals(5, this.c5.size);
        assertEquals(1, this.c5.waitlist.size());
        assertEquals(s6, this.c5.waitlist.getFirst());
    }



    //Basic get check
    @Test
    @Tag("score:2")
    @DisplayName("Get Test 1")
    void testGet1() {
        this.c7.put(s1);
        this.c7.put(s2);
        assertEquals(s1, this.c7.get(1));
    }

    //Student not in table
    @Test
    @Tag("score:2")
    @DisplayName("Get Test 2")
    void testGet2() {
        this.c7.put(s1);
        assertNull(this.c7.get(2));
    }

    //Student in bucket
    @Test
    @Tag("score:3")
    @DisplayName("Get Test 3")
    void testGet3() {
        this.c7.put(s1);
        this.c7.put(s4);
        this.c7.put(s8);
        this.c7.put(s12);
        assertEquals(s4, this.c7.get(4));
        assertEquals(s8, this.c7.get(8));
        assertEquals(s12, this.c7.get(12));
    }

    //Student in waitlist
    @Test
    @Tag("private")
    @Tag("score:3")
    @DisplayName("Get Test 4")
    void testGet4() {
        this.c7.put(s1);
        this.c7.put(s3);
        this.c7.put(s8);
        this.c7.put(s5);
        this.c7.put(s6);
        assertEquals(s1, this.c7.get(1));
        assertEquals(s3, this.c7.get(3));
        assertEquals(s8, this.c7.get(8));
        assertEquals(s5, this.c7.get(5));
        assertEquals(s6, this.c7.get(6));
    }

    //Student not in table and bucket not null
    @Test
    @Tag("private")
    @Tag("score:3")
    @DisplayName("Get Test 5")
    void testGet5() {
        this.c7.put(s2);
        this.c7.put(s4);
        this.c7.put(s3);
        this.c7.put(s12);
        assertEquals(s4, this.c7.get(4));
        assertEquals(s2, this.c7.get(2));
        assertEquals(s12, this.c7.get(12));
        assertNull(this.c7.get(16));
    }

    @Test
    @Tag("private")
    @Tag("score:2")
    @DisplayName("Get Test 6")
    void testGet6() {
        this.c7.put(s1);
        this.c7.put(s2);
        this.c7.put(s3);
        this.c7.put(s4);
        this.c7.put(s5);
        this.c7.put(s6);
        this.c7.put(s7);
        this.c7.put(s8);
        assertEquals(s1, this.c7.get(1));
        assertEquals(s2, this.c7.get(2));
        assertEquals(s3, this.c7.get(3));
        assertEquals(s4, this.c7.get(4));
        assertEquals(s5, this.c7.get(5));
        assertEquals(s6, this.c7.get(6));
        assertEquals(s7, this.c7.get(7));
        assertEquals(s8, this.c7.get(8));
    }



    //Basic
    @Test
    @Tag("score:3")
    @DisplayName("GetRegisteredStudents Test 1")
    void testGetRegisteredStudents1() {
        this.c7.put(s2);
        this.c7.put(s1);
        this.c7.put(s3);
        this.c7.put(s4);
        Set<Student> expected = Set.of(s1,s2,s3,s4);
        Set<Student> actual = new HashSet<>(Arrays.asList(this.c7.getRegisteredStudents()));
        assertEquals(expected, actual);
    }

    //Buckets full
    @Test
    @Tag("private")
    @Tag("score:1")
    @DisplayName("GetRegisteredStudents Test 2")
    void testGetRegisteredStudents2() {
        this.c7.put(s4);
        this.c7.put(s12);
        this.c7.put(s8);
        this.c7.put(s3);
        this.c7.put(s1);
        this.c7.put(s2);
        Set<Student> expected = Set.of(s12,s4,s3,s8);
        Set<Student> actual = new HashSet<>(Arrays.asList(this.c7.getRegisteredStudents()));
        assertEquals(expected, actual);
    }


    //Buckets full
    @Test
    @Tag("private")
    @Tag("score:2")
    @DisplayName("GetRegisteredStudents Test 3")
    void testGetRegisteredStudents3() {
        this.c7.put(s1);
        this.c7.put(s2);
        this.c7.put(s3);
        this.c7.put(s4);
        this.c7.put(s12);
        this.c7.put(s6);
        this.c7.put(s7);
        this.c7.put(s8);
        Set<Student> expected = Set.of(s1,s2,s3,s4,s12,s6);
        Set<Student> actual = new HashSet<>(Arrays.asList(this.c7.getRegisteredStudents()));
        assertEquals(expected, actual);
    }



    //Basic
    @Test
    @Tag("score:3")
    @DisplayName("GetRegisteredIDs Test 1")
    void testGetRegisteredIDs1() {
        this.c7.put(s2);
        this.c7.put(s1);
        this.c7.put(s3);
        this.c7.put(s4);
        Set<Integer> expected = Set.of(1,2,3,4);
        Set<Integer> actual = Arrays.stream(this.c7.getRegisteredIDs()).boxed().collect(Collectors.toSet());
        assertEquals(expected, actual);
    }

    //Buckets full
    @Test
    @Tag("private")
    @Tag("score:2")
    @DisplayName("GetRegisteredIDs Test 2")
    void testGetRegisteredIDs2() {
        this.c7.put(s4);
        this.c7.put(s12);
        this.c7.put(s8);
        this.c7.put(s3);
        this.c7.put(s1);
        this.c7.put(s2);
        Set<Integer> expected = Set.of(12,4,3,8);
        Set<Integer> actual = Arrays.stream(this.c7.getRegisteredIDs()).boxed().collect(Collectors.toSet());
        assertEquals(expected, actual);
    }

    //Buckets full
    @Test
    @Tag("private")
    @Tag("score:2")
    @DisplayName("GetRegisteredIDs Test 3")
    void testGetRegisteredIDs3() {
        this.c7.put(s1);
        this.c7.put(s2);
        this.c7.put(s3);
        this.c7.put(s4);
        this.c7.put(s12);
        this.c7.put(s6);
        this.c7.put(s7);
        this.c7.put(s8);
        Set<Integer> expected = Set.of(1,2,3,4,12,6);
        Set<Integer> actual = Arrays.stream(this.c7.getRegisteredIDs()).boxed().collect(Collectors.toSet());
        assertEquals(expected, actual);
    }



    //Basic
    @Test
    @Tag("score:3")
    @DisplayName("GetWaitlistedStudents Test 1")
    void testGetWaitlistedStudents1() {
        this.c7.put(s2);
        this.c7.put(s1);
        this.c7.put(s3);
        this.c7.put(s4);
        this.c7.put(s6);
        this.c7.put(s7);
        Set<Student> expected = Set.of(s6,s7);
        Set<Student> actual = new HashSet<>(Arrays.asList(this.c7.getWaitlistedStudents()));
        assertEquals(expected, actual);
    }

    //no students
    @Test
    @Tag("private")
    @Tag("score:1")
    @DisplayName("GetWaitlistedStudents Test 2")
    void testGetWaitlistedStudents2() {
        this.c7.put(s2);
        this.c7.put(s1);
        this.c7.put(s3);
        this.c7.put(s4);
        Set<Student> expected = Set.of();
        Set<Student> actual = new HashSet<>(Arrays.asList(this.c7.getWaitlistedStudents()));
        try {
            assertEquals(expected, actual);
        }
        catch(AssertionError e){assertNull(actual);}
    }

    @Test
    @Tag("private")
    @Tag("score:2")
    @DisplayName("GetWaitlistedStudents Test 3")
    void testGetWaitlistedStudents3() {
        this.c7.put(s1);
        this.c7.put(s2);
        this.c7.put(s3);
        this.c7.put(s4);
        this.c7.put(s12);
        this.c7.put(s6);
        this.c7.put(s7);
        this.c7.put(s8);
        Set<Student> expected = Set.of(s7, s8);
        Set<Student> actual = new HashSet<>(Arrays.asList(this.c7.getWaitlistedStudents()));
        assertEquals(expected, actual);
    }

}