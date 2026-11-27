/*
 * BÀI TẬP LỚN — HỆ THỐNG QUẢN LÝ BỆNH NHÂN — NHÓM 08
 * File: Menu.java
 * Thành viên phụ trách: Hàn Chí Toàn
 * Ngày ghi chú: 17/11
 * Nhiệm vụ được phân công: Xử lý các lựa chọn của người dùng và hỗ trợ nhập liệu.
 *
 * NỘI DUNG CỦA FILE:
 * - Dùng chung một Scanner đọc dữ liệu bàn phím theo từng dòng với bảng mã UTF-8.
 * - Hiển thị menu và nhận lựa chọn trong phạm vi hợp lệ.
 * - Cung cấp hàm đọc số nguyên, phí dịch vụ dương và chuỗi bắt buộc.
 * - Dùng vòng lặp và xử lý ngoại lệ để yêu cầu nhập lại khi sai dữ liệu.
 * - Cung cấp các hàm menu tổng quát với tham số kiểu <E>.
 *
 * LUỒNG HOẠT ĐỘNG CHÍNH:
 * Nhận lời nhắc hoặc danh sách lựa chọn từ lớp gọi
 * → hiển thị
 * → đọc dòng
 * → kiểm tra dữ liệu
 * → nhập lại nếu sai
 * → trả giá trị hợp lệ.
 */

/** NHẬP DỮ LIỆU VÀ MENU. Một Scanner đọc từng dòng; các hàm kiểm tra sẽ yêu cầu nhập lại khi sai.
 * Kiểu tổng quát <E> cho phép dùng chung các hàm menu với nhiều kiểu phần tử. */
import java.util.ArrayList;
import java.util.Scanner;

/** Dùng chung một Scanner để tránh nhiều bộ đọc tranh nhau dữ liệu bàn phím. */
public final class Menu {
    // static tạo một bộ đọc chung; UTF-8 hỗ trợ dữ liệu tiếng Việt nhập vào.
    private static final Scanner INPUT = new Scanner(System.in, "UTF-8");
    // Không cần tạo đối tượng Menu vì tất cả chức năng đều là phương thức static.
    private Menu() { }

    /** In lời nhắc, đọc cả dòng và bỏ khoảng trắng hai đầu; báo lỗi khi hết dữ liệu nhập. */
    public static String readLine(String prompt) {
        System.out.print(prompt);
        // Nếu luồng nhập kết thúc thì ném ngoại lệ để main thoát vòng lặp có thông báo.
        if (!INPUT.hasNextLine()) throw new IllegalStateException("End of input.");
        // Luôn đọc cả dòng nên không bị sót ký tự xuống dòng như khi trộn nextInt và nextLine.
        return INPUT.nextLine().trim();
    }

    /** Chuyển chuỗi thành số nguyên trong đoạn [min, max]; lặp lại nếu nhập sai. */
    public static int readInt(String prompt, int min, int max) {
        // Lặp nhập cho đến khi dữ liệu đạt yêu cầu; return hoặc break mới kết thúc bước nhập.
        while (true) {
            // Giữ dữ liệu dạng chuỗi trước khi thử chuyển thành số.
            String raw = readLine(prompt);
            try {
                // Chuỗi không biểu diễn được số nguyên sẽ chuyển sang nhánh catch.
                int number = Integer.parseInt(raw);
                // Chỉ return khi đúng cả kiểu số và giới hạn; nếu sai thì vòng while tiếp tục.
                if (number >= min && number <= max) return number;
            } catch (NumberFormatException ignored) {
                /** Dữ liệu không phải số nguyên hoặc ngoài phạm vi đều cần nhập lại. */
            }
            System.out.println("Invalid integer. Enter " + min + " to " + max + ".");
        }
    }

    /** Đọc phí dịch vụ lớn hơn 0; không nhận NaN hoặc giá trị vô cực. */
    public static double readPositiveDouble(String prompt) {
        // Lặp nhập cho đến khi dữ liệu đạt yêu cầu; return hoặc break mới kết thúc bước nhập.
        while (true) {
            try {
                // Đọc dòng và chuyển thành double; nhập chữ sẽ gây NumberFormatException.
                double number = Double.parseDouble(readLine(prompt));
                if (Double.isFinite(number) && number > 0) return number;
            } catch (NumberFormatException ignored) { }
            System.out.println("Invalid service fee. Enter a number greater than 0.");
        }
    }

    /** Đọc chuỗi bắt buộc, không chứa dấu phẩy vì file dùng dấu phẩy để tách cột. */
    public static String readRequired(String prompt) {
        // Lặp nhập cho đến khi dữ liệu đạt yêu cầu; return hoặc break mới kết thúc bước nhập.
        while (true) {
            String value = readLine(prompt);
            // Trả chuỗi khi có nội dung và không chứa dấu phân cách cột.
            if (!value.isEmpty() && value.indexOf(',') < 0) return value;
            System.out.println("Value must not be empty or contain a comma.");
        }
    }

    /** In menu từ số 0; số được chọn tương ứng với chỉ số phần tử trong ArrayList. */
    public static <E> int int_getChoice(ArrayList<E> options) {
        // Chặn danh sách null/rỗng trước khi tính giới hạn hoặc truy cập phần tử.
        if (options == null || options.isEmpty()) throw new IllegalArgumentException("No menu options.");
        // Duyệt từng nhãn lựa chọn để người dùng biết các giá trị có thể nhập.
        for (int i = 0; i < options.size(); i++)
            System.out.println(i + ". " + options.get(i));
        return readInt("Your choice: ", 0, options.size() - 1);
    }

    /** In lựa chọn từ số 1; trừ 1 để lấy và trả về đối tượng trong danh sách. */
    public static <E> E ref_getChoice(ArrayList<E> options) {
        // Chặn danh sách null/rỗng trước khi tính giới hạn hoặc truy cập phần tử.
        if (options == null || options.isEmpty()) throw new IllegalArgumentException("No options to select.");
        // Duyệt từng nhãn lựa chọn để người dùng biết các giá trị có thể nhập.
        for (int i = 0; i < options.size(); i++)
            System.out.println((i + 1) + ". " + options.get(i));
        return options.get(readInt("Choose 1-" + options.size() + ": ", 1, options.size()) - 1);
    }
}
