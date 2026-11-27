// PTIT INT1332 - DANH SACH BENH NHAN.
// extends ArrayList<Patient>; thuoc tinh departmentList luu doi tuong dung chung.
// Collections.sort goi compareTo cua Patient de sap xep.
// Them/sua phai chon khoa co san; tim theo ten khoa dung contains + lower case.
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/** Patient repository with a shared DepartmentList for referential integrity. */
public class PatientList extends ArrayList<Patient> {
    private static final long serialVersionUID = 1L;
    private final DepartmentList departmentList;

    public PatientList(DepartmentList departmentList) {
        if (departmentList == null) throw new IllegalArgumentException("departmentList is required.");
        this.departmentList = departmentList;
    }

    /** Returns the index matching an ID or -1. */
    public int searchID(String id) {
        if (id == null) return -1;
        for (int i = 0; i < size(); i++)
            if (get(i).getPatientID().equalsIgnoreCase(id.trim())) return i;
        return -1;
    }

    /** Parses patient text; rejects unknown department links and invalid rows. */
    public boolean loadFromFile(String filename) {
        ArrayList<Patient> parsed = new ArrayList<>();
        boolean allValid = true;
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filename), StandardCharsets.UTF_8)) {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (line.trim().isEmpty()) continue;
                try {
                    String[] cols = line.split(",", -1);
                    if (cols.length != 5) throw new IllegalArgumentException("expected 5 columns");
                    int deptPosition = departmentList.searchID(cols[1]);
                    if (deptPosition < 0) throw new IllegalArgumentException(
                        "unknown department reference: " + cols[1].trim());
                    Patient patient = new Patient(cols[0], departmentList.get(deptPosition),
                        cols[2], cols[3], Integer.parseInt(cols[4].trim()));
                    boolean duplicate = parsed.stream().anyMatch(p -> p.getPatientID()
                        .equalsIgnoreCase(patient.getPatientID()));
                    if (duplicate) throw new IllegalArgumentException("duplicate patient ID");
                    parsed.add(patient);
                } catch (IllegalArgumentException ex) {
                    allValid = false;
                    System.err.println(filename + " line " + lineNo + ": " + ex.getMessage());
                }
            }
            clear();
            addAll(parsed);
            System.out.println("Loaded " + size() + " patients.");
        } catch (IOException ex) {
            System.err.println("Cannot load " + filename + ": " + ex.getMessage());
            return false;
        }
        return allValid;
    }

    /** Saves patientID,departmentID,fullName,diagnosis,age on explicit request. */
    public boolean saveToFile(String filename) {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filename), StandardCharsets.UTF_8)) {
            for (Patient patient : this) {
                writer.write(patient.toString());
                writer.newLine();
            }
            System.out.println("Patients saved to " + filename);
            return true;
        } catch (IOException ex) {
            System.err.println("Cannot save " + filename + ": " + ex.getMessage());
            return false;
        }
    }

    /** Validates inputs before modifying the existing list. */
    public void addPatient() {
        if (departmentList.isEmpty()) {
            System.out.println("No department exists; add a department first."); return;
        }
        String id;
        while (true) {
            id = Menu.readRequired("New patient ID: ");
            if (searchID(id) < 0) break;
            System.out.println("Patient ID already exists. Retry.");
        }
        Department department = departmentList.getUserChoice();
        if (department == null) return;
        String name = Menu.readRequired("Full name: ");
        String diagnosis = Menu.readRequired("Diagnosis: ");
        int age = Menu.readInt("Age (>=0): ", 0, Integer.MAX_VALUE);
        add(new Patient(id, department, name, diagnosis, age));
        System.out.println("Patient added successfully.");
    }

    /** Removes exactly one patient, or reports that the ID was missing. */
    public boolean removePatient() {
        String id = Menu.readRequired("Patient ID to remove: ");
        int position = searchID(id);
        if (position < 0) { System.out.println("Patient not found."); return false; }
        remove(position);
        System.out.println("Patient removed successfully.");
        return true;
    }

    /** Updates only once all fields and new department selection are accepted. */
    public boolean updatePatient() {
        String oldId = Menu.readRequired("Patient ID to update: ");
        int position = searchID(oldId);
        if (position < 0) { System.out.println("Patient not found."); return false; }
        String id;
        while (true) {
            id = Menu.readRequired("New patient ID: ");
            int conflict = searchID(id);
            if (conflict < 0 || conflict == position) break;
            System.out.println("Patient ID already exists on another record.");
        }
        Department department = departmentList.getUserChoice();
        if (department == null) return false;
        String name = Menu.readRequired("New full name: ");
        String diagnosis = Menu.readRequired("New diagnosis: ");
        int age = Menu.readInt("New age (>=0): ", 0, Integer.MAX_VALUE);
        Patient target = get(position);
        target.setPatientID(id);
        target.setDepartment(department);
        target.setFullName(name);
        target.setDiagnosis(diagnosis);
        target.setAge(age);
        System.out.println("Patient updated successfully.");
        return true;
    }

    /** Prints patient records ordered by departmentName and then patientID. */
    public void listPatients() {
        if (isEmpty()) { System.out.println("No patients found."); return; }
        // Sort a copy: printing does not mutate list order or saved data order.
        ArrayList<Patient> sorted = new ArrayList<>(this);
        Collections.sort(sorted);
        printHeader();
        for (Patient patient : sorted) System.out.println(patient.screenString());
    }

    /** Case-insensitive substring filter over patients' department names. */
    public void printBasedDepartmentName() {
        String query = Menu.readRequired("Department name keyword: ")
            .toLowerCase(Locale.ROOT);
        int matches = 0;
        for (Patient patient : this) {
            if (patient.getDepartment().getDepartmentName()
                .toLowerCase(Locale.ROOT).contains(query)) {
                if (matches++ == 0) printHeader();
                System.out.println(patient.screenString());
            }
        }
        if (matches == 0) System.out.println("No patients matched department keyword.");
    }

    private static void printHeader() {
        System.out.println("ID       | Department      | Full name              | Diagnosis            | Age");
    }
}
