// PTIT INT1332 - LOP DOI TUONG Department.
// Kien thuc: private (dong goi), constructor, getter/setter, toString.
// Moi object Department bieu dien mot khoa; du lieu phai duoc kiem tra hop le.
/**
 * Department - model representing one hospital department.
 * Topic 03 / INT1332. No author attribution is implied by this source.
 */
public class Department {
    private String departmentID;
    private String departmentName;
    private String floor;
    private double serviceFee;

    /** Creates a default department for exercises / framework compatibility. */
    public Department() {
        this("UNASSIGNED", "Unassigned", "N/A", 1.0);
    }

    /** Constructs a validated department model. */
    public Department(String departmentID, String departmentName,
                      String floor, double serviceFee) {
        setDepartmentID(departmentID);
        setDepartmentName(departmentName);
        setFloor(floor);
        setServiceFee(serviceFee);
    }

    /** Rejects empty text and separators unsafe for this simple CSV format. */
    private static String requiredText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must not be empty.");
        }
        String clean = value.trim();
        if (clean.contains(",") || clean.contains("\n") || clean.contains("\r")) {
            throw new IllegalArgumentException(field + " must not contain a comma/newline.");
        }
        return clean;
    }

    public String getDepartmentID() { return departmentID; }
    public void setDepartmentID(String departmentID) {
        this.departmentID = requiredText(departmentID, "departmentID");
    }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) {
        this.departmentName = requiredText(departmentName, "departmentName");
    }
    public String getFloor() { return floor; }
    public void setFloor(String floor) {
        this.floor = requiredText(floor, "floor");
    }
    public double getServiceFee() { return serviceFee; }
    public void setServiceFee(double serviceFee) {
        if (!Double.isFinite(serviceFee) || serviceFee <= 0) {
            throw new IllegalArgumentException("serviceFee must be greater than 0.");
        }
        this.serviceFee = serviceFee;
    }

    /** File format: departmentID,departmentName,floor,serviceFee. */
    @Override public String toString() {
        return departmentID + "," + departmentName + "," + floor + "," + serviceFee;
    }
}
