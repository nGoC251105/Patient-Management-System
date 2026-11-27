/*
 * BÀI TẬP LỚN — HỆ THỐNG QUẢN LÝ BỆNH NHÂN — NHÓM 08
 * File: Department.java
 * Thành viên phụ trách: Nguyễn Thành Đạt
 * Ngày ghi chú: 16/11
 * Nhiệm vụ được phân công: Quản lý dữ liệu khoa phòng.
 *
 * NỘI DUNG CỦA FILE:
 * - Lưu mã khoa, tên khoa, tầng và phí dịch vụ của một khoa phòng.
 * - Đóng gói các thuộc tính bằng private; cung cấp constructor, getter và setter.
 * - Kiểm tra chuỗi bắt buộc và phí dịch vụ hữu hạn, lớn hơn 0.
 * - Chuyển dữ liệu khoa thành một dòng văn bản bằng toString để lưu file.
 *
 * LUỒNG HOẠT ĐỘNG CHÍNH:
 * Nhận dữ liệu từ DepartmentList
 * → constructor gọi setter kiểm tra
 * → lưu thuộc tính
 * → getter cung cấp dữ liệu hoặc toString tạo bản ghi.
 */

/** LỚP KHOA. Đóng gói dữ liệu bằng private; constructor và setter kiểm tra dữ liệu đầu vào.
 * Các bệnh nhân cùng khoa tham chiếu đến cùng một đối tượng Department. */
/** Biểu diễn một khoa gồm mã khoa, tên khoa, tầng và phí dịch vụ. */
public class Department {
    // Mã định danh khoa; tính duy nhất được kiểm tra ở DepartmentList.
    private String departmentID;
    // Tên khoa dùng khi hiển thị, tìm kiếm và sắp xếp bệnh nhân.
    private String departmentName;
    // Tầng dạng chuỗi để chấp nhận cả ký hiệu tầng, ví dụ G hoặc B1.
    private String floor;
    // Phí dịch vụ phải là số hữu hạn và lớn hơn 0.
    private double serviceFee;

    /** Tạo khoa với thông tin mặc định khi không truyền tham số. */
    public Department() {
        // this(...) gọi constructor có tham số để tái sử dụng toàn bộ bước kiểm tra.
        this("UNASSIGNED", "Unassigned", "N/A", 1.0);
    }

    /** Khởi tạo khoa; gọi setter để kiểm tra từng thuộc tính trước khi gán. */
    public Department(String departmentID, String departmentName,
                      String floor, double serviceFee) {
        setDepartmentID(departmentID);
        setDepartmentName(departmentName);
        setFloor(floor);
        setServiceFee(serviceFee);
    }

    /** Loại chuỗi rỗng, dấu phẩy và xuống dòng để giữ đúng định dạng file. */
    private static String requiredText(String value, String field) {
        // Kiểm tra null trước; toán tử || dừng sớm nên không gọi trim trên null.
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must not be empty.");
        }
        // Loại khoảng trắng hai đầu trước khi kiểm tra và lưu.
        String clean = value.trim();
        if (clean.contains(",") || clean.contains("\n") || clean.contains("\r")) {
            throw new IllegalArgumentException(field + " must not contain a comma/newline.");
        }
        // Trả chuỗi đã chuẩn hóa cho setter gán vào thuộc tính.
        return clean;
    }

    // Getter trả mã khoa; không thay đổi đối tượng.
    public String getDepartmentID() { return departmentID; }
    // Setter chuẩn hóa và kiểm tra mã trước khi gán; kiểm tra trùng thuộc trách nhiệm danh sách.
    public void setDepartmentID(String departmentID) {
        this.departmentID = requiredText(departmentID, "departmentID");
    }
    // Trả tên khoa hiện tại cho hiển thị, lọc và so sánh.
    public String getDepartmentName() { return departmentName; }
    // Tên mới phải vượt qua kiểm tra chuỗi bắt buộc.
    public void setDepartmentName(String departmentName) {
        this.departmentName = requiredText(departmentName, "departmentName");
    }
    // Trả thông tin tầng đang lưu.
    public String getFloor() { return floor; }
    // Chỉ gán tầng khi chuỗi hợp lệ.
    public void setFloor(String floor) {
        this.floor = requiredText(floor, "floor");
    }
    // Trả phí dịch vụ hiện tại.
    public double getServiceFee() { return serviceFee; }
    // Ngăn phí âm, bằng 0, NaN và vô cực ngay ở tầng đối tượng.
    public void setServiceFee(double serviceFee) {
        if (!Double.isFinite(serviceFee) || serviceFee <= 0) {
            throw new IllegalArgumentException("serviceFee must be greater than 0.");
        }
        this.serviceFee = serviceFee;
    }

    /** Định dạng ghi file: mã khoa,tên khoa,tầng,phí dịch vụ. */
    @Override public String toString() {
        return departmentID + "," + departmentName + "," + floor + "," + serviceFee;
    }
}
