/*
 * BÀI TẬP LỚN — HỆ THỐNG QUẢN LÝ BỆNH NHÂN — NHÓM 08
 * File: PatientManager.java
 * Thành viên phụ trách: Lương Minh Toàn
 * Ngày ghi chú: 17/11
 * Nhiệm vụ được phân công: Điều phối và tích hợp chương trình.
 *
 * NỘI DUNG CỦA FILE:
 * - Chứa phương thức main, là điểm bắt đầu chạy chương trình.
 * - Tạo và nạp danh sách khoa trước, sau đó tạo và nạp danh sách bệnh nhân.
 * - Truyền cùng danh sách khoa cho PatientList để thống nhất liên kết dữ liệu.
 * - Tạo menu 12 lựa chọn từ 0 đến 11 và điều hướng bằng switch-case.
 * - Lặp menu sau mỗi chức năng; chọn 0 để thoát và không tự động lưu.
 * - Tích hợp các lớp dữ liệu, lớp quản lý danh sách và lớp hỗ trợ nhập liệu.
 *
 * LUỒNG HOẠT ĐỘNG CHÍNH:
 * main
 * → tạo danh sách khoa
 * → nạp khoa
 * → tạo danh sách bệnh nhân dùng chung khoa
 * → nạp bệnh nhân
 * → hiển thị menu
 * → gọi chức năng 0–11
 * → lặp lại hoặc thoát.
 */

/** ĐIỂM BẮT ĐẦU CHƯƠNG TRÌNH. Luồng: nạp khoa → nạp bệnh nhân → lặp menu → xử lý lựa chọn.
 * Mục 5 lưu khoa, mục 11 lưu bệnh nhân; nên lưu khoa trước bệnh nhân. Mục 0 thoát không tự lưu. */
import java.util.ArrayList;

/**
 * Lớp điều khiển chương trình, kết nối danh sách khoa, danh sách bệnh nhân và menu.
 *
 * BẢN ĐỒ CÁC LỚP:
 * - Department, Patient: giữ dữ liệu một đối tượng; setter kiểm tra giá trị hợp lệ.
 * - DepartmentList, PatientList: quản lý nhiều đối tượng và xử lý các chức năng nghiệp vụ.
 * - Menu: đọc bàn phím, kiểm tra kiểu/giới hạn và hiển thị lựa chọn.
 * - PatientManager: khởi tạo các thành phần rồi gọi chức năng theo lựa chọn.
 *
 * VÍ DỤ LUỒNG THÊM KHOA RỒI THÊM BỆNH NHÂN:
 * 1. Chọn 2 → addDepartment → nhập mã, tên, tầng, phí → thêm Department vào departments.
 * 2. Chọn 8 → addPatient → nhập mã bệnh nhân → getUserChoice trên cùng departments.
 * 3. Chọn mã khoa hoặc số thứ tự → nhận đối tượng Department → nhập tên, chẩn đoán, tuổi.
 * 4. new Patient giữ tham chiếu khoa đó → add vào patients → quay lại menu.
 * 5. Chọn 5 rồi 11 để lưu cả hai danh sách; lần chạy sau nạp khoa trước, bệnh nhân sau.
 *
 * LUỒNG SỬA/XÓA/TÌM:
 * - Sửa: tìm mã cũ → kiểm tra mã mới không trùng → nhập đủ thông tin → gọi setter.
 * - Xóa bệnh nhân: tìm mã → lấy vị trí → remove; không xóa khoa của bệnh nhân đó.
 * - Tìm theo tên khoa: duyệt từng bệnh nhân, lấy tên khoa rồi kiểm tra chứa từ khóa.
 * - Liệt kê có sắp xếp: sao chép danh sách → sort gọi compareTo → in bản sao.
 *
 * PHÂN BIỆT BỘ NHỚ VÀ FILE:
 * - add/set/remove tác động lên đối tượng trong bộ nhớ của lần chạy hiện tại.
 * - saveToFile ghi đè file; loadFromFile thay danh sách bằng các dòng hợp lệ đọc được.
 * - File bệnh nhân lưu mã khoa, không lưu toàn bộ đối tượng khoa.
 * - Nếu chỉ lưu bệnh nhân mà chưa lưu khoa mới, lần sau bệnh nhân đó bị bỏ qua do thiếu khoa.
 * - Các phương thức đọc/ghi trả boolean; main hiện gọi để xử lý và in thông báo,
 *   không dùng giá trị này để chặn hiển thị menu khi file gặp lỗi.
 */
public final class PatientManager {
    // Lớp chỉ cung cấp main, không cần tạo đối tượng điều khiển riêng.
    private PatientManager() { }

    /** Nạp khoa trước để khi đọc bệnh nhân có thể tìm đối tượng khoa theo mã; lặp menu đến khi chọn 0. */
    public static void main(String[] args) {
        // Bước 1: nạp khoa làm dữ liệu gốc cho các liên kết của bệnh nhân.
        // LUỒNG KHỞI ĐỘNG — BƯỚC 1: Tạo danh sách khoa rỗng trong bộ nhớ.
        DepartmentList departments = new DepartmentList();
        // File được tìm tương đối với thư mục chạy; run.bat chuyển về thư mục chứa chương trình.
        // LUỒNG KHỞI ĐỘNG — BƯỚC 2: Đọc departments.txt và đưa các khoa hợp lệ vào danh sách.
        departments.loadFromFile("departments.txt");
        // Bước 2: truyền cùng danh sách khoa, không tạo một bản sao riêng.
        // LUỒNG KHỞI ĐỘNG — BƯỚC 3: Tạo danh sách bệnh nhân, dùng chung danh sách khoa vừa tạo.
        PatientList patients = new PatientList(departments);
        // Mã khoa trong từng dòng bệnh nhân được nối với khoa đã nạp ở bước trước.
        // LUỒNG KHỞI ĐỘNG — BƯỚC 4: Đọc patients.txt, tra mã khoa để nối mỗi bệnh nhân với khoa tương ứng.
        patients.loadFromFile("patients.txt");

        // Bước 3: vị trí trong danh sách lựa chọn tương ứng với số case bên dưới.
        // LUỒNG KHỞI ĐỘNG — BƯỚC 5: Tạo danh sách các chức năng của menu, từ 0 đến 11.
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
            // do-while bảo đảm menu hiển thị ít nhất một lần.
            do {
                System.out.println("\n--- MAIN MENU ---");
                // Hàm này chỉ trả số 0–11 hợp lệ; nhập sai sẽ được yêu cầu nhập lại.
                // LUỒNG MENU — BƯỚC 6: Hiển thị menu và nhận số chức năng người dùng chọn.
                choice = Menu.int_getChoice(options);
                // Bước 4: gọi chức năng tương ứng; làm xong quay lại menu.
                // Thêm/sửa/xóa chỉ đổi bộ nhớ; chỉ mục 5 và 11 mới ghi ra file.
                // LUỒNG MENU — BƯỚC 7: Dựa vào số đã chọn để chạy đúng chức năng bên dưới.
                switch (choice) {
                    // CHỨC NĂNG 1 — Liệt kê khoa: gọi DepartmentList.listDepartments() để in danh sách hiện tại.
                    case 1: departments.listDepartments(); break;
                    // CHỨC NĂNG 2 — Thêm khoa: nhập thông tin → kiểm tra → tạo khoa → thêm vào danh sách.
                    case 2: departments.addDepartment(); break;
                    // CHỨC NĂNG 3 — Tìm khoa theo mã: nhập mã → tìm vị trí → in khoa hoặc báo không tìm thấy.
                    case 3:
                        String id = Menu.readRequired("Department ID to search: ");
                        // searchID trả chỉ số hoặc -1; kiểm tra trước get(index) để tránh truy cập ngoài danh sách.
                        int index = departments.searchID(id);
                        if (index >= 0) System.out.println("Found: " + departments.get(index));
                        else System.out.println("Department not found.");
                        break;
                    // CHỨC NĂNG 4 — Sửa khoa: tìm mã cũ → nhập thông tin mới → cập nhật khoa đó.
                    case 4: departments.updateDepartment(); break;
                    // CHỨC NĂNG 5 — Lưu khoa: ghi danh sách khoa trong bộ nhớ xuống departments.txt.
                    case 5: departments.saveToFile("departments.txt"); break;
                    // CHỨC NĂNG 6 — Liệt kê bệnh nhân: sắp xếp bản sao theo tên khoa, mã bệnh nhân rồi in.
                    case 6: patients.listPatients(); break;
                    // CHỨC NĂNG 7 — Tìm bệnh nhân theo một phần tên khoa: nhập từ khóa → lọc → in kết quả.
                    case 7: patients.printBasedDepartmentName(); break;
                    // CHỨC NĂNG 8 — Thêm bệnh nhân: nhập mã → chọn khoa → nhập thông tin → thêm vào danh sách.
                    case 8: patients.addPatient(); break;
                    // CHỨC NĂNG 9 — Xóa bệnh nhân: nhập mã → tìm vị trí → xóa nếu có.
                    case 9: patients.removePatient(); break;
                    // CHỨC NĂNG 10 — Sửa bệnh nhân: tìm mã cũ → nhập mã, khoa và thông tin mới → cập nhật.
                    case 10: patients.updatePatient(); break;
                    // CHỨC NĂNG 11 — Lưu bệnh nhân: ghi danh sách hiện tại xuống patients.txt.
                    case 11: patients.saveToFile("patients.txt"); break;
                    // CHỨC NĂNG 0 — Thoát: kết thúc vòng menu, không tự động lưu dữ liệu.
                    case 0: System.out.println("Goodbye. No automatic save performed."); break;
                    default: System.out.println("Unsupported option.");
                }
            // break trong switch chỉ kết thúc nhánh xử lý; điều kiện này mới quyết định dừng vòng menu.
            // LUỒNG MENU — BƯỚC 8: Chưa chọn 0 thì quay lại bước 6; chọn 0 thì kết thúc chương trình.
            } while (choice != 0);
        // Bắt trường hợp hết dữ liệu bàn phím/đầu vào chuyển hướng, kết thúc có thông báo.
        } catch (IllegalStateException ex) {
            System.err.println("Input ended: " + ex.getMessage());
        }
    }
}
