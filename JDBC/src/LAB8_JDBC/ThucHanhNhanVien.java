package LAB8_JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ThucHanhNhanVien {
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        String url = "jdbc:sqlite:nhanvien.db"; // SQLite tạo file nhanvien.db tại thư mục gốc dự án

        try {
            // B1. Đăng ký driver và thiết lập kết nối
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
            System.out.println("Kết nối CSDL thành công!");

            // B2. Tạo đối tượng Statement
            stmt = conn.createStatement();

            // B3.1. Tạo bảng NhanVien (id, ten, chuc_vu)
            String sqlCreateTable = "CREATE TABLE IF NOT EXISTS NhanVien ("
                    + "id INTEGER PRIMARY KEY, "
                    + "ten TEXT NOT NULL, "
                    + "chuc_vu TEXT"
                    + ");";
            stmt.executeUpdate(sqlCreateTable);
            System.out.println("Tạo bảng NhanVien thành công!");

            // Quản lý giao dịch (Transaction): Tắt auto-commit
            conn.setAutoCommit(false);

            // B3.2. Viết ít nhất 3 câu lệnh INSERT
            String insert1 = "INSERT OR REPLACE INTO NhanVien (id, ten, chuc_vu) VALUES (1, 'Nguyen Van A', 'Truong phong');";
            String insert2 = "INSERT OR REPLACE INTO NhanVien (id, ten, chuc_vu) VALUES (2, 'Tran Thi B', 'Ke toan');";
            String insert3 = "INSERT OR REPLACE INTO NhanVien (id, ten, chuc_vu) VALUES (3, 'Le Van C', 'Lap trinh vien');";

            stmt.executeUpdate(insert1);
            stmt.executeUpdate(insert2);
            stmt.executeUpdate(insert3);

            // Xác nhận giao dịch nếu không có lỗi
            conn.commit();
            System.out.println("Thêm 3 nhân viên thành công (Transaction committed)!");

            // B4. Truy vấn và hiển thị dữ liệu
            String sqlSelect = "SELECT id, ten, chuc_vu FROM NhanVien;";
            rs = stmt.executeQuery(sqlSelect);

            System.out.println("\n--- DANH SÁCH NHÂN VIÊN ---");
            while (rs.next()) {
                int id = rs.getInt("id");
                String ten = rs.getString("ten");
                String chucVu = rs.getString("chuc_vu");
                System.out.println("ID: " + id + " | Tên: " + ten + " | Chức vụ: " + chucVu);
            }

        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy Driver SQLite: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Lỗi thao tác SQL: " + e.getMessage());
            // Rollback nếu có lỗi xảy ra trong quá trình thực thi Transaction
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Đã hủy bỏ thay đổi (Transaction rollback)!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            // B5 & Xử lý ngoại lệ: Đóng tài nguyên theo thứ tự ngược lại trong finally
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                System.out.println("\nĐã đóng tất cả kết nối an toàn.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}