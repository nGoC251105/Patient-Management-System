/*
 * BÀI TẬP LỚN — HỆ THỐNG QUẢN LÝ BỆNH NHÂN — NHÓM 08
 * File: PatientList.java
 * Thành viên phụ trách: Bùi Minh Ngọc
 * Ngày ghi chú: 17/11
 * Nhiệm vụ được phân công: Xử lý danh sách bệnh nhân; đồng thời đảm nhiệm công việc trưởng nhóm.
 *
 * NỘI DUNG CỦA FILE:
 * - Kế thừa ArrayList<Patient> và dùng chung DepartmentList với chương trình chính.
 * - Thực hiện chức năng 6: hiển thị có sắp xếp; 7: tìm theo một phần tên khoa.
 * - Thực hiện chức năng 8: thêm; 9: xóa; 10: cập nhật bệnh nhân.
 * - Thực hiện chức năng 11: lưu danh sách bệnh nhân vào patients.txt.
 * - Nạp bệnh nhân từ file, kiểm tra mã trùng và tra mã khoa để khôi phục liên kết.
 * - Vai trò trưởng nhóm: phối hợp công việc giữa các thành viên và theo dõi tích hợp các lớp.
 *
 * LUỒNG HOẠT ĐỘNG CHÍNH:
 * PatientManager gọi chức năng
 * → kiểm tra mã bệnh nhân
 * → chọn khoa khi thêm/sửa
 * → xử lý đối tượng Patient
 * → hiển thị hoặc lưu khi người dùng yêu cầu.
 */

/** DANH SÁCH BỆNH NHÂN. Kế thừa ArrayList<Patient>, dùng chung departmentList với main.
 * Khoa vừa thêm trong bộ nhớ có thể được chọn ngay mà không cần lưu rồi đọc lại file.
 * Thêm/sửa bệnh nhân chỉ chấp nhận khoa đang tồn tại trong danh sách khoa. */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/** Quản lý bệnh nhân và giữ tham chiếu đến danh sách khoa dùng chung. */
public class PatientList extends ArrayList<Patient> {
    private static final long serialVersionUID = 1L;
    // final giữ nguyên tham chiếu danh sách khoa; các phần tử bên trong vẫn có thể thêm/sửa.
    private final DepartmentList departmentList;

    // main truyền danh sách khoa đã nạp; null bị chặn để các thao tác sau không lỗi tham chiếu.
    public PatientList(DepartmentList departmentList) {
        if (departmentList == null) throw new IllegalArgumentException("departmentList is required.");
        // Lưu cùng tham chiếu, không sao chép: khoa mới xuất hiện ngay trong chức năng chọn khoa.
        this.departmentList = departmentList;
    }

    /** Tìm mã bệnh nhân không phân biệt hoa/thường; trả vị trí hoặc -1 nếu không thấy. */
    public int searchID(String id) {
        // Mã null được coi là không tìm thấy để tránh gọi phương thức trên null.
        if (id == null) return -1;
        // Tìm tuyến tính từ đầu đến cuối; chỉ số hợp lệ nằm trong 0 đến size()-1.
        for (int i = 0; i < size(); i++)
            if (get(i).getPatientID().equalsIgnoreCase(id.trim())) return i;
        // Đã duyệt hết mà không khớp mã: trả giá trị quy ước không tìm thấy.
        return -1;
    }

    /** Đọc 5 cột mỗi dòng → tra mã khoa → tạo bệnh nhân → kiểm tra trùng mã; bỏ qua và báo dòng sai. */
    // ============================================================
    // KHỞI ĐỘNG — NẠP BỆNH NHÂN. main gọi sau khi đã nạp danh sách khoa.
    // ============================================================
    public boolean loadFromFile(String filename) {
        // Đọc vào danh sách tạm; chưa sửa danh sách bệnh nhân cho đến khi đọc xong file.
        ArrayList<Patient> parsed = new ArrayList<>();
        // Giá trị trả về cho biết có dòng lỗi hay không; các dòng hợp lệ vẫn được nạp.
        boolean allValid = true;
        // try-with-resources tự đóng file; BufferedReader đọc theo dòng với bảng mã UTF-8.
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filename), StandardCharsets.UTF_8)) {
            String line;
            int lineNo = 0;
            // Đọc đến cuối file; null nghĩa là không còn dòng để xử lý.
            while ((line = reader.readLine()) != null) {
                // Đếm cả dòng trống để số dòng trong thông báo khớp với file gốc.
                lineNo++;
                // Bỏ dòng chỉ có khoảng trắng; tiếp tục vòng lặp với dòng kế tiếp.
                if (line.trim().isEmpty()) continue;
                try {
                    // Tham số -1 giữ cả cột rỗng cuối dòng, giúp phát hiện dữ liệu thiếu.
                    String[] cols = line.split(",", -1);
                    if (cols.length != 5) throw new IllegalArgumentException("expected 5 columns");
                    // File chỉ chứa mã khoa: tra danh sách đã nạp để lấy đúng đối tượng.
                    int deptPosition = departmentList.searchID(cols[1]);
                    if (deptPosition < 0) throw new IllegalArgumentException(
                        "unknown department reference: " + cols[1].trim());
                    // Tạo bệnh nhân với đối tượng khoa đã tìm được; parseInt chuyển tuổi thành số nguyên.
                    Patient patient = new Patient(cols[0], departmentList.get(deptPosition),
                        cols[2], cols[3], Integer.parseInt(cols[4].trim()));
                    // Chỉ so với bệnh nhân hợp lệ đã nạp; không cho hai dòng cùng mã, kể cả khác hoa/thường.
                    boolean duplicate = parsed.stream().anyMatch(p -> p.getPatientID()
                        .equalsIgnoreCase(patient.getPatientID()));
                    if (duplicate) throw new IllegalArgumentException("duplicate patient ID");
                    // Dòng hợp lệ mới được đưa vào danh sách tạm.
                    parsed.add(patient);
                // Lỗi định dạng số hoặc kiểm tra dữ liệu chỉ làm bỏ dòng hiện tại, không dừng cả file.
                } catch (IllegalArgumentException ex) {
                    // Đánh dấu đã gặp lỗi dù các dòng khác vẫn có thể hợp lệ.
                    allValid = false;
                    System.err.println(filename + " line " + lineNo + ": " + ex.getMessage());
                }
            }
            // Đọc file thành công: thay dữ liệu cũ bằng các dòng hợp lệ vừa phân tích.
            clear();
            // Nạp toàn bộ danh sách tạm; hàm này thay danh sách hiện tại, không nối tiếp dữ liệu cũ.
            addAll(parsed);
            System.out.println("Loaded " + size() + " patients.");
        // Lỗi mở, đọc, ghi hoặc đóng file: báo lỗi và trả false cho bên gọi.
        } catch (IOException ex) {
            System.err.println("Cannot load " + filename + ": " + ex.getMessage());
            return false;
        }
        // true nếu không có dòng sai; false nếu có dòng bị bỏ qua.
        return allValid;
    }

    /** Ghi lần lượt mã bệnh nhân,mã khoa,họ tên,chẩn đoán,tuổi khi người dùng chọn lưu. */
    // ============================================================
    // CHỨC NĂNG 11 — LƯU DANH SÁCH BỆNH NHÂN. Được gọi từ case 11.
    // ============================================================
    public boolean saveToFile(String filename) {
        // Mở file UTF-8 để ghi mới/ghi đè nội dung; tự đóng khi ra khỏi khối try.
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filename), StandardCharsets.UTF_8)) {
            for (Patient patient : this) {
                // toString tạo một bản ghi phân cách bằng dấu phẩy để lần sau đọc lại.
                writer.write(patient.toString());
                // Mỗi đối tượng chiếm một dòng trong file.
                writer.newLine();
            }
            System.out.println("Patients saved to " + filename);
            return true;
        // Lỗi mở, đọc, ghi hoặc đóng file: báo lỗi và trả false cho bên gọi.
        } catch (IOException ex) {
            System.err.println("Cannot save " + filename + ": " + ex.getMessage());
            return false;
        }
    }

    /** Luồng thêm: kiểm tra có khoa → nhập mã chưa trùng → chọn khoa → nhập thông tin → thêm bệnh nhân. */
    // ============================================================
    // CHỨC NĂNG 8 — THÊM BỆNH NHÂN. Được gọi từ case 8.
    // ============================================================
    public void addPatient() {
        // Không thể thêm bệnh nhân khi chưa có khoa để liên kết.
        if (departmentList.isEmpty()) {
            System.out.println("No department exists; add a department first."); return;
        }
        String id;
        // Lặp nhập cho đến khi dữ liệu đạt yêu cầu; return hoặc break mới kết thúc bước nhập.
        while (true) {
            id = Menu.readRequired("New patient ID: ");
            if (searchID(id) < 0) break;
            System.out.println("Patient ID already exists. Retry.");
        }
        // Dùng bảng chọn chung: nhận mã khoa hoặc số thứ tự và trả khoa đang tồn tại.
        Department department = departmentList.getUserChoice();
        if (department == null) return;
        String name = Menu.readRequired("Full name: ");
        String diagnosis = Menu.readRequired("Diagnosis: ");
        int age = Menu.readInt("Age (>=0): ", 0, Integer.MAX_VALUE);
        // Chỉ tạo và thêm khi mọi đầu vào hợp lệ; thay đổi hiện chỉ nằm trong bộ nhớ.
        add(new Patient(id, department, name, diagnosis, age));
        System.out.println("Patient added successfully.");
    }

    /** Tìm theo mã và xóa đúng một bệnh nhân; báo nếu không tìm thấy. */
    // ============================================================
    // CHỨC NĂNG 9 — XÓA BỆNH NHÂN. Được gọi từ case 9.
    // ============================================================
    public boolean removePatient() {
        String id = Menu.readRequired("Patient ID to remove: ");
        int position = searchID(id);
        if (position < 0) { System.out.println("Patient not found."); return false; }
        // Xóa theo chỉ số tìm được; không dùng remove(id) vì phần tử danh sách là Patient.
        remove(position);
        System.out.println("Patient removed successfully.");
        return true;
    }

    /** Luồng sửa: tìm mã cũ → kiểm tra mã mới → chọn khoa → nhập thông tin → cập nhật sau khi hợp lệ. */
    // ============================================================
    // CHỨC NĂNG 10 — CẬP NHẬT BỆNH NHÂN. Được gọi từ case 10.
    // ============================================================
    public boolean updatePatient() {
        String oldId = Menu.readRequired("Patient ID to update: ");
        int position = searchID(oldId);
        if (position < 0) { System.out.println("Patient not found."); return false; }
        String id;
        // Lặp nhập cho đến khi dữ liệu đạt yêu cầu; return hoặc break mới kết thúc bước nhập.
        while (true) {
            id = Menu.readRequired("New patient ID: ");
            int conflict = searchID(id);
            // Cho phép mã chưa dùng hoặc mã của chính bệnh nhân đang sửa.
            if (conflict < 0 || conflict == position) break;
            System.out.println("Patient ID already exists on another record.");
        }
        // Dùng bảng chọn chung: nhận mã khoa hoặc số thứ tự và trả khoa đang tồn tại.
        Department department = departmentList.getUserChoice();
        if (department == null) return false;
        String name = Menu.readRequired("New full name: ");
        String diagnosis = Menu.readRequired("New diagnosis: ");
        int age = Menu.readInt("New age (>=0): ", 0, Integer.MAX_VALUE);
        // Lấy bệnh nhân cũ sau khi nhập xong để tránh sửa dở dang do lỗi nhập liệu.
        Patient target = get(position);
        target.setPatientID(id);
        target.setDepartment(department);
        target.setFullName(name);
        target.setDiagnosis(diagnosis);
        target.setAge(age);
        System.out.println("Patient updated successfully.");
        return true;
    }

    /** Hiển thị bệnh nhân theo tên khoa tăng dần, sau đó theo mã bệnh nhân. */
    // ============================================================
    // CHỨC NĂNG 6 — LIỆT KÊ BỆNH NHÂN CÓ SẮP XẾP. Được gọi từ case 6.
    // ============================================================
    public void listPatients() {
        if (isEmpty()) { System.out.println("No patients found."); return; }
        /** Sắp xếp bản sao để việc hiển thị không đổi thứ tự danh sách gốc và thứ tự lưu file. */
        ArrayList<Patient> sorted = new ArrayList<>(this);
        // Collections.sort gọi Patient.compareTo: tên khoa trước, mã bệnh nhân sau.
        Collections.sort(sorted);
        printHeader();
        for (Patient patient : sorted) System.out.println(patient.screenString());
    }

    /** Lọc theo một phần tên khoa, chuyển cả từ khóa và tên khoa sang chữ thường để so sánh. */
    // ============================================================
    // CHỨC NĂNG 7 — TÌM BỆNH NHÂN THEO TÊN KHOA. Được gọi từ case 7.
    // ============================================================
    public void printBasedDepartmentName() {
        // Locale.ROOT giúp chuyển chữ thường theo quy tắc ổn định, không phụ thuộc ngôn ngữ máy.
        String query = Menu.readRequired("Department name keyword: ")
            .toLowerCase(Locale.ROOT);
        // Biến đếm vừa điều khiển in tiêu đề một lần, vừa nhận biết trường hợp không có kết quả.
        int matches = 0;
        for (Patient patient : this) {
            if (patient.getDepartment().getDepartmentName()
                .toLowerCase(Locale.ROOT).contains(query)) {
                // Chỉ in tiêu đề ở kết quả đầu tiên; đếm để báo nếu không có kết quả.
                if (matches++ == 0) printHeader();
                System.out.println(patient.screenString());
            }
        }
        if (matches == 0) System.out.println("No patients matched department keyword.");
    }

    // Hàm hỗ trợ nội bộ: in cùng tiêu đề cho danh sách đầy đủ và kết quả tìm kiếm.
    private static void printHeader() {
        System.out.println("ID       | Department      | Full name              | Diagnosis            | Age");
    }
}
