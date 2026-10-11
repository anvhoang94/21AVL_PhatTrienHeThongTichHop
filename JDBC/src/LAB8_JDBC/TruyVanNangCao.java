package LAB8_JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TruyVanNangCao {
	public static void main(String[] args) {
		String url = "jdbc:sqlite:nhanvien_luong.db";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(url);
			stmt = conn.createStatement();

			// 1. Tạo mảng NhanVien có cột lượng
			stmt.executeUpdate("DROP TABLE IF EXISTS NhanVien;");
			String sqlCreate = "CREATE TABLE NhanVien (" + "id INTEGER PRIMARY KEY," + "ten TEXT NOT NULL, "
					+ "chuc_vu TEXT, " + "luong REAL" + ");";
			stmt.executeUpdate(sqlCreate);

			// 2. Thêm dữ liệu mẫu
			stmt.executeUpdate("INSERT INTO NhanVien VALUES (1, 'Nguyen Van A', 'IT', 15000000);");
			stmt.executeUpdate("INSERT INTO NhanVien VALUES (2, 'Tran Thi B', 'Ke toan', 12000000);");
			stmt.executeUpdate("INSERT INTO NhanVien VALUES (3, 'Le Van C', 'IT', 20000000);");
			stmt.executeUpdate("INSERT INTO NhanVien VALUES (4, 'Pham Thi D', 'Nhan su', 10000000);");
			stmt.executeUpdate("INSERT INTO NhanVien VALUES (5, 'Hoang Van E', 'IT', 18000000);");
			stmt.executeUpdate("INSERT INTO NhanVien VALUES (6, 'Vu Thi F', 'Ke toan', 14000000);");

			// a.Tính tổng lương (SUM) và lương trung bình (AVG)
			rs = stmt.executeQuery("SELECT SUM(luong) AS tong_luong, AVG(luong) AS luong_tb FROM NhanVien;");
			if (rs.next()) {
				System.out.printf("Tổng lương toàn công ty: %,.0f VNĐ\n", rs.getDouble("tong_luong"));
				System.out.printf("Lương trung bình: %,.0f VND\n", rs.getDouble("luong_tb"));
			}
			rs.close();
			// c. Đếm số lượng nhân viên theo chức vụ (COUNT và GROUP BY)
			System.out.println("\n--- SỐ LƯỢNG NHÂN VIÊN THEO CHỨC VỤ ---");
			String sqlGroupBy = "SELECT chuc_vu, COUNT(*) AS so_luong, AVG(luong) AS luong_tb " + "FROM NhanVien "
					+ "GROUP BY chuc_vu;";
			rs = stmt.executeQuery(sqlGroupBy);
			while (rs.next()) {
				System.out.printf("Chức vụ: %-10s | Số lượng: %d | Lương TB: %,.0f VND\n", rs.getString("chuc_vu"),
						rs.getInt("so_luong"), rs.getDouble("luong_tb"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}