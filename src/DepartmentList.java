// PTIT INT1332 - DANH SACH KHOA.
// extends ArrayList<Department> = ke thua cac thao tac add, get, size, remove.
// searchID tra chi so hoac -1; load/save dung Java IO va try-with-resources.
// Khi sua khoa, cap nhat object cu thay vi tao object moi de giu lien ket Patient.
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/** Department repository with CRUD operations and safe text-file I/O. */
public class DepartmentList extends ArrayList<Department> {
    private static final long serialVersionUID = 1L;
    public DepartmentList() { super(); }

    /** Returns the matching index, or -1; case-insensitive String comparison. */
    public int searchID(String id) {
        if (id == null) return -1;
        for (int i = 0; i < size(); i++) {
            if (get(i).getDepartmentID().equalsIgnoreCase(id.trim())) return i;
        }
        return -1;
    }

    /** Reads departments, skipping bad lines and reporting the line numbers. */
    public boolean loadFromFile(String filename) {
        ArrayList<Department> parsed = new ArrayList<>();
        boolean allValid = true;
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filename), StandardCharsets.UTF_8)) {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (line.trim().isEmpty()) continue;
                try {
                    String[] cols = line.split(",", -1);
                    if (cols.length != 4) throw new IllegalArgumentException("expected 4 columns");
                    Department department = new Department(cols[0], cols[1], cols[2],
                        Double.parseDouble(cols[3].trim()));
                    boolean duplicate = parsed.stream().anyMatch(d -> d.getDepartmentID()
                        .equalsIgnoreCase(department.getDepartmentID()));
                    if (duplicate) throw new IllegalArgumentException("duplicate department ID");
                    parsed.add(department);
                } catch (IllegalArgumentException ex) {
                    allValid = false;
                    System.err.println(filename + " line " + lineNo + ": " + ex.getMessage());
                }
            }
            // A missing/unreadable file leaves the existing list unchanged.
            clear();
            addAll(parsed);
            System.out.println("Loaded " + size() + " departments.");
        } catch (IOException ex) {
            System.err.println("Cannot load " + filename + ": " + ex.getMessage());
            return false;
        }
        return allValid;
    }

    /** Writes valid data only on explicit menu option 5. */
    public boolean saveToFile(String filename) {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filename), StandardCharsets.UTF_8)) {
            for (Department department : this) {
                writer.write(department.toString());
                writer.newLine();
            }
            System.out.println("Departments saved to " + filename);
            return true;
        } catch (IOException ex) {
            System.err.println("Cannot save " + filename + ": " + ex.getMessage());
            return false;
        }
    }

    /** Selects a current department by ID or the displayed one-based number. */
    public Department getUserChoice() {
        if (isEmpty()) {
            System.out.println("No departments available. Add a department first.");
            return null;
        }
        System.out.println("No. | Department ID | Department | Floor | Service fee");
        for (int i = 0; i < size(); i++) {
            Department d = get(i);
            System.out.println((i + 1) + " | " + d.getDepartmentID() + " | "
                + d.getDepartmentName() + " | " + d.getFloor() + " | " + d.getServiceFee());
        }
        while (true) {
            String choice = Menu.readRequired("Department ID or number (1-" + size() + "): ");
            int position = searchID(choice);
            if (position >= 0) return get(position);
            try {
                int number = Integer.parseInt(choice);
                if (number >= 1 && number <= size()) return get(number - 1);
            } catch (NumberFormatException ignored) { }
            System.out.println("Department not found. Enter an existing ID or number 1-" + size() + ".");
        }
    }

    /** Creates a department after validating an unused ID and all fields. */
    public void addDepartment() {
        String id;
        while (true) {
            id = Menu.readRequired("New department ID: ");
            if (searchID(id) < 0) break;
            System.out.println("Department ID already exists. Retry.");
        }
        String name = Menu.readRequired("Department name: ");
        String floor = Menu.readRequired("Floor: ");
        double fee = Menu.readPositiveDouble("Service fee: ");
        add(new Department(id, name, floor, fee));
        System.out.println("Department added successfully.");
    }

    /** Updates a department atomically after all replacement values are valid. */
    public void updateDepartment() {
        String oldId = Menu.readRequired("Department ID to update: ");
        int position = searchID(oldId);
        if (position < 0) { System.out.println("Department not found."); return; }
        String id;
        while (true) {
            id = Menu.readRequired("New department ID: ");
            int conflict = searchID(id);
            if (conflict < 0 || conflict == position) break;
            System.out.println("Department ID already exists on another record.");
        }
        String name = Menu.readRequired("New department name: ");
        String floor = Menu.readRequired("New floor: ");
        double fee = Menu.readPositiveDouble("New service fee: ");
        // Keep object identity so Patient references stay linked after an update.
        Department target = get(position);
        target.setDepartmentID(id);
        target.setDepartmentName(name);
        target.setFloor(floor);
        target.setServiceFee(fee);
        System.out.println("Department updated successfully.");
    }

    /** Displays the department table without editing it. */
    public void listDepartments() {
        if (isEmpty()) { System.out.println("No departments found."); return; }
        System.out.println("ID | Department | Floor | Service fee");
        for (Department d : this)
            System.out.println(d.getDepartmentID() + " | " + d.getDepartmentName() + " | "
                + d.getFloor() + " | " + d.getServiceFee());
    }
}
