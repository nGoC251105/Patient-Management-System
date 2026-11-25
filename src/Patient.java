// PTIT INT1332 - LOP DOI TUONG Patient.
// Kien thuc: quan he Patient -> Department, implements Comparable<Patient>.
// compareTo so sanh departmentName truoc, neu trung thi so sanh patientID.
// toString de ghi file; screenString de hien thi Console.
/** Patient model with an object reference to exactly one Department. */
public class Patient implements Comparable<Patient> {
    private String patientID;
    private Department department;
    private String fullName;
    private String diagnosis;
    private int age;

    public Patient() {
        this("UNASSIGNED", new Department(), "Unassigned", "Unassigned", 0);
    }

    /** Constructs a complete validated patient record. */
    public Patient(String patientID, Department department,
                   String fullName, String diagnosis, int age) {
        setPatientID(patientID);
        setDepartment(department);
        setFullName(fullName);
        setDiagnosis(diagnosis);
        setAge(age);
    }

    /** Enforces nonblank values and the delimiter rules of our data files. */
    private static String requiredText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must not be empty.");
        }
        String clean = value.trim();
        if (clean.contains(",") || clean.contains("\r") || clean.contains("\n")) {
            throw new IllegalArgumentException(field + " must not contain a comma/newline.");
        }
        return clean;
    }

    public String getPatientID() { return patientID; }
    public void setPatientID(String patientID) {
        this.patientID = requiredText(patientID, "patientID");
    }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) {
        if (department == null) throw new IllegalArgumentException("department is required.");
        this.department = department;
    }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = requiredText(fullName, "fullName"); }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = requiredText(diagnosis, "diagnosis"); }
    public int getAge() { return age; }
    public void setAge(int age) {
        if (age < 0) throw new IllegalArgumentException("age must not be negative.");
        this.age = age;
    }

    /** Serializes the department ID, never a Java object representation. */
    @Override public String toString() {
        return patientID + "," + department.getDepartmentID() + "," + fullName
            + "," + diagnosis + "," + age;
    }

    /** Friendly text for displaying one patient on the Console. */
    public String screenString() {
        return String.format("%-8s | %-15s | %-22s | %-20s | %d",
            patientID, department.getDepartmentName(), fullName, diagnosis, age);
    }

    /** Natural order: department name (ascending), then patient ID. */
    @Override public int compareTo(Patient other) {
        int byDepartment = department.getDepartmentName()
            .compareToIgnoreCase(other.department.getDepartmentName());
        return byDepartment != 0 ? byDepartment
            : patientID.compareToIgnoreCase(other.patientID);
    }
}
