/*
 * BÀI TẬP LỚN — HỆ THỐNG QUẢN LÝ BỆNH NHÂN — NHÓM 08
 * File: Patient.java
 * Thành viên phụ trách: Trần Quang Huy
 * Ngày ghi chú: 16/11
 * Nhiệm vụ được phân công: Quản lý thông tin bệnh nhân.
 *
 * NỘI DUNG CỦA FILE:
 * - Lưu mã bệnh nhân, khoa, họ tên, chẩn đoán và tuổi.
 * - Giữ tham chiếu Department để thể hiện bệnh nhân thuộc một khoa.
 * - Kiểm tra thông tin qua setter; tuổi không âm và khoa không được null.
 * - Cài đặt Comparable để sắp xếp theo tên khoa rồi theo mã bệnh nhân.
 * - Cung cấp toString để ghi file và screenString để hiển thị bảng.
 *
 * LUỒNG HOẠT ĐỘNG CHÍNH:
 * Nhận dữ liệu từ PatientList
 * → constructor gọi setter kiểm tra
 * → giữ liên kết khoa
 * → cung cấp dữ liệu cho hiển thị, so sánh hoặc lưu file.
 */

/** LỚP BỆNH NHÂN. Mỗi Patient giữ tham chiếu đến một Department.
 * Comparable<Patient> cung cấp quy tắc sắp xếp cho Collections.sort. */
/** Mô hình bệnh nhân gồm mã, khoa, họ tên, chẩn đoán và tuổi. */
public class Patient implements Comparable<Patient> {
    // Mã định danh bệnh nhân; PatientList chịu trách nhiệm kiểm tra trùng mã.
    private String patientID;
    // Giữ đối tượng khoa, không chỉ giữ mã: sửa khoa sẽ được phản ánh ở bệnh nhân.
    private Department department;
    // Họ tên bệnh nhân, bắt buộc có dữ liệu.
    private String fullName;
    // Chẩn đoán; không chứa dấu phẩy vì file chưa hỗ trợ chuỗi CSV có dấu nháy.
    private String diagnosis;
    // Tuổi là số nguyên không âm.
    private int age;

    // Constructor mặc định tạo dữ liệu mẫu; luồng thêm thực tế dùng constructor đủ tham số.
    public Patient() {
        this("UNASSIGNED", new Department(), "Unassigned", "Unassigned", 0);
    }

    /** Khởi tạo đầy đủ thông tin; kiểm tra dữ liệu thông qua các setter. */
    public Patient(String patientID, Department department,
                   String fullName, String diagnosis, int age) {
        setPatientID(patientID);
        setDepartment(department);
        setFullName(fullName);
        setDiagnosis(diagnosis);
        setAge(age);
    }

    /** Kiểm tra chuỗi không rỗng, không chứa dấu phẩy hoặc xuống dòng gây sai cột dữ liệu. */
    private static String requiredText(String value, String field) {
        // Kiểm tra null trước; toán tử || dừng sớm nên không gọi trim trên null.
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must not be empty.");
        }
        // Loại khoảng trắng hai đầu trước khi kiểm tra và lưu.
        String clean = value.trim();
        if (clean.contains(",") || clean.contains("\r") || clean.contains("\n")) {
            throw new IllegalArgumentException(field + " must not contain a comma/newline.");
        }
        // Trả chuỗi đã chuẩn hóa cho setter gán vào thuộc tính.
        return clean;
    }

    // Lấy mã bệnh nhân để tìm kiếm, kiểm tra trùng và ghi file.
    public String getPatientID() { return patientID; }
    // Kiểm tra chuỗi mã; quy tắc không trùng được thực hiện trong danh sách.
    public void setPatientID(String patientID) {
        this.patientID = requiredText(patientID, "patientID");
    }
    // Trả tham chiếu khoa để truy cập mã hoặc tên khoa.
    public Department getDepartment() { return department; }
    // Không cho liên kết null; luồng thêm/sửa chỉ truyền khoa đã chọn từ danh sách.
    public void setDepartment(Department department) {
        if (department == null) throw new IllegalArgumentException("department is required.");
        this.department = department;
    }
    // Trả họ tên hiện tại.
    public String getFullName() { return fullName; }
    // Chuẩn hóa và kiểm tra họ tên trước khi gán.
    public void setFullName(String fullName) { this.fullName = requiredText(fullName, "fullName"); }
    // Trả chẩn đoán hiện tại.
    public String getDiagnosis() { return diagnosis; }
    // Chuẩn hóa và kiểm tra chẩn đoán trước khi gán.
    public void setDiagnosis(String diagnosis) { this.diagnosis = requiredText(diagnosis, "diagnosis"); }
    // Trả tuổi hiện tại.
    public int getAge() { return age; }
    // Tuổi âm gây ngoại lệ, nên dữ liệu sai không được gán vào đối tượng.
    public void setAge(int age) {
        if (age < 0) throw new IllegalArgumentException("age must not be negative.");
        this.age = age;
    }

    /** Ghi mã khoa vào file; khi đọc lại sẽ tra mã để nối bệnh nhân với đối tượng khoa. */
    @Override public String toString() {
        return patientID + "," + department.getDepartmentID() + "," + fullName
            + "," + diagnosis + "," + age;
    }

    /** Định dạng các cột để hiển thị một bệnh nhân trên màn hình. */
    public String screenString() {
        // %-8s, %-15s,... căn trái chuỗi theo độ rộng tối thiểu; %d in tuổi dạng số nguyên.
        return String.format("%-8s | %-15s | %-22s | %-20s | %d",
            patientID, department.getDepartmentName(), fullName, diagnosis, age);
    }

    /** So tên khoa trước; nếu bằng nhau thì so mã bệnh nhân, không phân biệt hoa/thường. */
    @Override public int compareTo(Patient other) {
        // Kết quả so sánh âm/0/dương nghĩa là đứng trước/bằng/đứng sau đối tượng other.
        int byDepartment = department.getDepartmentName()
            .compareToIgnoreCase(other.department.getDepartmentName());
        // Nếu tên khoa khác nhau, dùng ngay kết quả đó; chỉ so mã bệnh nhân khi tên khoa bằng nhau.
        return byDepartment != 0 ? byDepartment
            : patientID.compareToIgnoreCase(other.patientID);
    }
}
