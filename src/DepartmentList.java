/*
 * BÀI TẬP LỚN — HỆ THỐNG QUẢN LÝ BỆNH NHÂN — NHÓM 08
 * File: DepartmentList.java
 * Thành viên phụ trách: Lê Thị Ngọc Hương
 * Ngày ghi chú: 16/11
 * Nhiệm vụ được phân công: Xử lý danh sách khoa phòng.
 *
 * NỘI DUNG CỦA FILE:
 * - Kế thừa ArrayList<Department> để quản lý nhiều khoa trong bộ nhớ.
 * - Thực hiện chức năng 1: hiển thị khoa; 2: thêm khoa; 3: tìm khoa theo mã.
 * - Thực hiện chức năng 4: cập nhật khoa; 5: lưu khoa vào departments.txt.
 * - Nạp khoa từ file lúc khởi động; kiểm tra số cột, dữ liệu và mã trùng.
 * - Hỗ trợ chọn khoa bằng mã hoặc số thứ tự cho chức năng thêm/sửa bệnh nhân.
 * - Cập nhật trực tiếp đối tượng khoa cũ để giữ liên kết với bệnh nhân.
 *
 * LUỒNG HOẠT ĐỘNG CHÍNH:
 * PatientManager gọi chức năng
 * → nhập hoặc đọc dữ liệu
 * → tìm và kiểm tra khoa
 * → thêm/sửa/hiển thị trong bộ nhớ
 * → ghi file khi chọn chức năng 5.
 */

/** DANH SÁCH KHOA. Kế thừa ArrayList<Department> để dùng add, get, size, remove.
 * Luồng: nhập/đọc file → kiểm tra → lưu trong bộ nhớ → ghi file khi chọn lưu.
 * Khi sửa khoa, giữ đối tượng cũ để các bệnh nhân vẫn liên kết đúng khoa. */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/** Quản lý thao tác thêm, tìm, sửa, hiển thị và đọc/ghi danh sách khoa. */
public class DepartmentList extends ArrayList<Department> {
    private static final long serialVersionUID = 1L;
    // Khởi tạo ArrayList rỗng bằng constructor của lớp cha.
    public DepartmentList() { super(); }

    /** Tìm mã không phân biệt hoa/thường; trả vị trí tìm được hoặc -1 nếu không có. */
    // ============================================================
    // CHỨC NĂNG 3 — TÌM KHOA THEO MÃ. case 3 gọi hàm này; thêm/sửa và đọc bệnh nhân cũng dùng lại.
    // ============================================================
    public int searchID(String id) {
        // Mã null được coi là không tìm thấy để tránh gọi phương thức trên null.
        if (id == null) return -1;
        // Tìm tuyến tính từ đầu đến cuối; chỉ số hợp lệ nằm trong 0 đến size()-1.
        for (int i = 0; i < size(); i++) {
            if (get(i).getDepartmentID().equalsIgnoreCase(id.trim())) return i;
        }
        // Đã duyệt hết mà không khớp mã: trả giá trị quy ước không tìm thấy.
        return -1;
    }

    /** Đọc từng dòng; kiểm tra đủ 4 cột, dữ liệu hợp lệ và mã không trùng; bỏ dòng sai và báo số dòng. */
    // ============================================================
    // KHỞI ĐỘNG — NẠP KHOA. main gọi trước khi nạp bệnh nhân.
    // ============================================================
    public boolean loadFromFile(String filename) {
        // Danh sách tạm chỉ nhận các dòng hợp lệ, chưa thay đổi danh sách đang dùng.
        ArrayList<Department> parsed = new ArrayList<>();
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
                    if (cols.length != 4) throw new IllegalArgumentException("expected 4 columns");
                    // Constructor tiếp tục kiểm tra các trường; parseDouble chuyển phí từ chuỗi sang số.
                    Department department = new Department(cols[0], cols[1], cols[2],
                        Double.parseDouble(cols[3].trim()));
                    // stream duyệt các khoa đã đọc; anyMatch trả true khi có ít nhất một mã trùng.
                    boolean duplicate = parsed.stream().anyMatch(d -> d.getDepartmentID()
                        .equalsIgnoreCase(department.getDepartmentID()));
                    if (duplicate) throw new IllegalArgumentException("duplicate department ID");
                    // Chỉ thêm vào danh sách tạm khi toàn bộ kiểm tra của dòng đã thành công.
                    parsed.add(department);
                // Lỗi định dạng số hoặc kiểm tra dữ liệu chỉ làm bỏ dòng hiện tại, không dừng cả file.
                } catch (IllegalArgumentException ex) {
                    // Đánh dấu đã gặp lỗi dù các dòng khác vẫn có thể hợp lệ.
                    allValid = false;
                    System.err.println(filename + " line " + lineNo + ": " + ex.getMessage());
                }
            }
            /** Chỉ thay danh sách sau khi đọc xong; nếu không mở/đọc được file thì giữ danh sách cũ. */
            // Đọc file thành công: thay dữ liệu cũ bằng các dòng hợp lệ vừa phân tích.
            clear();
            // Nạp toàn bộ danh sách tạm; hàm này thay danh sách hiện tại, không nối tiếp dữ liệu cũ.
            addAll(parsed);
            System.out.println("Loaded " + size() + " departments.");
        // Lỗi mở, đọc, ghi hoặc đóng file: báo lỗi và trả false cho bên gọi.
        } catch (IOException ex) {
            System.err.println("Cannot load " + filename + ": " + ex.getMessage());
            return false;
        }
        // true nếu không có dòng sai; false nếu có dòng bị bỏ qua.
        return allValid;
    }

    /** Ghi danh sách khoa vào file khi người dùng chọn lưu khoa ở menu số 5. */
    // ============================================================
    // CHỨC NĂNG 5 — LƯU DANH SÁCH KHOA. Được gọi từ case 5 trong main.
    // ============================================================
    public boolean saveToFile(String filename) {
        // Mở file UTF-8 để ghi mới/ghi đè nội dung; tự đóng khi ra khỏi khối try.
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filename), StandardCharsets.UTF_8)) {
            for (Department department : this) {
                // toString tạo một bản ghi phân cách bằng dấu phẩy để lần sau đọc lại.
                writer.write(department.toString());
                // Mỗi đối tượng chiếm một dòng trong file.
                writer.newLine();
            }
            System.out.println("Departments saved to " + filename);
            return true;
        // Lỗi mở, đọc, ghi hoặc đóng file: báo lỗi và trả false cho bên gọi.
        } catch (IOException ex) {
            System.err.println("Cannot save " + filename + ": " + ex.getMessage());
            return false;
        }
    }

    /** In số thứ tự và mã khoa; nhận mã hoặc số từ 1, trả chính đối tượng khoa trong danh sách. */
    // ============================================================
    // HỖ TRỢ CHỨC NĂNG 8 VÀ 10 — CHỌN KHOA khi thêm hoặc sửa bệnh nhân.
    // ============================================================
    public Department getUserChoice() {
        if (isEmpty()) {
            System.out.println("No departments available. Add a department first.");
            return null;
        }
        // Bảng chọn dùng số từ 1; vị trí lưu trong ArrayList bắt đầu từ 0.
        System.out.println("No. | Department ID | Department | Floor | Service fee");
        // Tìm tuyến tính từ đầu đến cuối; chỉ số hợp lệ nằm trong 0 đến size()-1.
        for (int i = 0; i < size(); i++) {
            Department d = get(i);
            System.out.println((i + 1) + " | " + d.getDepartmentID() + " | "
                + d.getDepartmentName() + " | " + d.getFloor() + " | " + d.getServiceFee());
        }
        // Lặp nhập cho đến khi dữ liệu đạt yêu cầu; return hoặc break mới kết thúc bước nhập.
        while (true) {
            String choice = Menu.readRequired("Department ID or number (1-" + size() + "): ");
            // Ưu tiên khớp mã khoa; nếu không có mới thử hiểu là số thứ tự.
            int position = searchID(choice);
            if (position >= 0) return get(position);
            try {
                int number = Integer.parseInt(choice);
                // Ví dụ chọn 2 sẽ lấy get(1); giới hạn theo size hiện tại nên nhận cả khoa vừa thêm.
                if (number >= 1 && number <= size()) return get(number - 1);
            } catch (NumberFormatException ignored) { }
            System.out.println("Department not found. Enter an existing ID or number 1-" + size() + ".");
        }
    }

    /** Nhập mã chưa trùng → nhập tên, tầng, phí hợp lệ → thêm khoa vào danh sách dùng chung. */
    // ============================================================
    // CHỨC NĂNG 2 — THÊM KHOA. Được gọi từ case 2 trong main.
    // ============================================================
    public void addDepartment() {
        String id;
        // Lặp nhập cho đến khi dữ liệu đạt yêu cầu; return hoặc break mới kết thúc bước nhập.
        while (true) {
            id = Menu.readRequired("New department ID: ");
            if (searchID(id) < 0) break;
            System.out.println("Department ID already exists. Retry.");
        }
        String name = Menu.readRequired("Department name: ");
        String floor = Menu.readRequired("Floor: ");
        double fee = Menu.readPositiveDouble("Service fee: ");
        // Tạo khoa hợp lệ và thêm vào chính danh sách đang được PatientList dùng chung.
        add(new Department(id, name, floor, fee));
        System.out.println("Department added successfully.");
    }

    /** Tìm khoa → kiểm tra mã mới không trùng khoa khác → nhập đủ dữ liệu → cập nhật đối tượng cũ. */
    // ============================================================
    // CHỨC NĂNG 4 — CẬP NHẬT KHOA. Được gọi từ case 4 trong main.
    // ============================================================
    public void updateDepartment() {
        String oldId = Menu.readRequired("Department ID to update: ");
        int position = searchID(oldId);
        if (position < 0) { System.out.println("Department not found."); return; }
        String id;
        // Lặp nhập cho đến khi dữ liệu đạt yêu cầu; return hoặc break mới kết thúc bước nhập.
        while (true) {
            id = Menu.readRequired("New department ID: ");
            int conflict = searchID(id);
            // Chấp nhận mã chưa dùng hoặc giữ nguyên mã của chính khoa đang sửa.
            if (conflict < 0 || conflict == position) break;
            System.out.println("Department ID already exists on another record.");
        }
        String name = Menu.readRequired("New department name: ");
        String floor = Menu.readRequired("New floor: ");
        double fee = Menu.readPositiveDouble("New service fee: ");
        /** Giữ nguyên đối tượng để mọi bệnh nhân tham chiếu khoa này thấy thông tin vừa cập nhật. */
        Department target = get(position);
        target.setDepartmentID(id);
        target.setDepartmentName(name);
        target.setFloor(floor);
        target.setServiceFee(fee);
        System.out.println("Department updated successfully.");
    }

    /** Hiển thị danh sách khoa, không làm thay đổi dữ liệu. */
    // ============================================================
    // CHỨC NĂNG 1 — LIỆT KÊ KHOA. Được gọi từ case 1 trong main.
    // ============================================================
    public void listDepartments() {
        if (isEmpty()) { System.out.println("No departments found."); return; }
        System.out.println("ID | Department | Floor | Service fee");
        // Vòng for-each lần lượt đọc từng khoa trong danh sách hiện tại.
        for (Department d : this)
            System.out.println(d.getDepartmentID() + " | " + d.getDepartmentName() + " | "
                + d.getFloor() + " | " + d.getServiceFee());
    }
}
