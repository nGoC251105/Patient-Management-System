// PTIT INT1332 - DIEM BAT DAU CHUONG TRINH.
// main tao danh sach khoa truoc, nap departments.txt, sau do tao danh sach benh nhan.
// switch-case dieu huong 11 chuc nang; lua chon 0 thoat KHONG tu dong luu.
import java.util.ArrayList;

/** Main application controller for Topic 03: Patient Management System. */
public final class PatientManager {
    private PatientManager() { }

    /** Loads departments before patients; menu 0 exits without auto-saving. */
    public static void main(String[] args) {
        DepartmentList departments = new DepartmentList();
        departments.loadFromFile("departments.txt");
        PatientList patients = new PatientList(departments);
        patients.loadFromFile("patients.txt");

        ArrayList<String> options = new ArrayList<>();
        options.add("Exit (without automatic save)");
        options.add("List all Departments");
        options.add("Add a new Department");
        options.add("Search Department by ID");
        options.add("Update Department");
        options.add("Save Departments to departments.txt");
        options.add("List all Patients sorted by departmentName");
        options.add("Search Patients by partial departmentName");
        options.add("Add a new Patient");
        options.add("Remove Patient by ID");
        options.add("Update Patient by ID");
        options.add("Save Patients to patients.txt");

        System.out.println("=== PATIENT MANAGEMENT SYSTEM ===");
        try {
            int choice;
            do {
                System.out.println("\n--- MAIN MENU ---");
                choice = Menu.int_getChoice(options);
                switch (choice) {
                    case 1: departments.listDepartments(); break;
                    case 2: departments.addDepartment(); break;
                    case 3:
                        String id = Menu.readRequired("Department ID to search: ");
                        int index = departments.searchID(id);
                        if (index >= 0) System.out.println("Found: " + departments.get(index));
                        else System.out.println("Department not found.");
                        break;
                    case 4: departments.updateDepartment(); break;
                    case 5: departments.saveToFile("departments.txt"); break;
                    case 6: patients.listPatients(); break;
                    case 7: patients.printBasedDepartmentName(); break;
                    case 8: patients.addPatient(); break;
                    case 9: patients.removePatient(); break;
                    case 10: patients.updatePatient(); break;
                    case 11: patients.saveToFile("patients.txt"); break;
                    case 0: System.out.println("Goodbye. No automatic save performed."); break;
                    default: System.out.println("Unsupported option.");
                }
            } while (choice != 0);
        } catch (IllegalStateException ex) {
            System.err.println("Input ended: " + ex.getMessage());
        }
    }
}
