package LAB8_JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QuanLySanPham {
	private static final String URL = "jdbc:sqlite:sanpham.db";
	
	public static Connection getConnection() throws SQLException, ClassNotFoundException {
		Class.class.forName("org.sqlite.JDBC");
		return DriverManager.getConnection(URL);
	}
	
	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			
			//1. Tạo bảng SanPham
			stmt = conn.createStatement();
			String sqlCreate = "CREATE TABLE IF NOT EXISTS SanPham ("
					+ "maSP TEXT PRIMARY KEY, "
					+ "tenSP TEXT NOT NULL, "
					+ "gia REAL, "
					+ "soLuong INTEGER"
					+ ");";
			stmt.executeUpdate(sqlCreate);
			
			//2. CREATE: Thêm ít nhất 3 sản phẩm PreparedStatement và Transaction
			conn.setAutoCommit(false); //Bắt đầu Transaction
			String sqlInsert = "INSERT OR REPLACE INTO SanPham (maSP, tenSP, gia, soLuong) VALUES (?, ?, ?, ?);";
			pstmt = conn.prepareStatement(sqlInsert);
			
			String[][] dsSP = {
					{"SP01", "Chuột Logitech", "45000", "20"},
					{"SP02", "Bàn phím cơ", "120000", "15"},
					{"SP03", "Tai nghe gaming", "850000", "10"}
			};
			
			for (String[] sp : dsSP) {
				pstmt.setString(1, sp[0]);
				pstmt.setString(2, sp[1]);
				pstmt.setDouble(3, Double.parseDouble(sp[2]));
				pstmt.setInt(4, Integer.parseInt(sp[3]));
				pstmt.executeUpdate();
			}
			conn.commit(); // Xác nhận Transaction
			System.out.println("-> Thêm 3 sản phẩm thành công!");
			
			//3. READ: Liệt kê tất cả sản phẩm
			System.out.println("\n--- TẤT CẢ SẢN PHẨM ---");
			rs = stmt.executeQuery("SELECT * FROM SanPham;");
			while (rs.next()) {
				System.out.println("Tìm thấy: " + rs.getString("tenSP") + "-" + rs.getDouble("giá") + " VNĐ");
			}
			rs.close();
			
			//4. UPDATE: Cập nhật giá và số lượng theo maSP
			pstmt.close();
			String sqlUpdate = "UPDATE SanPham SET gia = ?, soLuong = ? WHERE maSP = ?;";
			pstmt = conn.prepareStatement(sqlUpdate);
			pstmt.setDouble(1, 400000);  // Giảm giá
            pstmt.setInt(2, 25);         // Tăng số lượng kho
            pstmt.setString(3, "SP01");
            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("\n-> Đã cập nhật SP01 (số dòng ảnh hưởng: " + rowsUpdated + ")");
            
            //5. DELETE: Xóa 1 sản phẩm theo maSP
            pstmt.close();
            String slqDeletee = "DELETE FROM SanPham WHERE maSP = ?;";
            pstmt = conn.prepareStatement(sqlUpdate);
            pstmt.setString(1, "SP03");
            int rowsDeleted = pstmt.executeUpdate();
            System.out.println("-> Đã xóa SP03 (số dòng ảnh hưởng: " + rowsDeleted + ")");
            
		} catch (ClassNotFoundException e) {
			System.out.println("Driver Error: " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
			if(conn !=null) {
				try {
					conn.rollback();
					System.out.println("Đã rollback transaction!");
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		} finally {
			// Đảm bảo đóng tất cả tài nguyên triệt để trong finally
			try {
				if (rs != null && !rs.isClosed()) rs.close();
				if (pstmt != null && !pstmt.isClosed()) pstmt.close();
				if (stmt != null && !stmt.isClosed()) stmt.close();
				if (conn != null && !conn.isClosed()) conn.close();
				System.out.println("\n-> Đã đóng tài nguyên SanPham thành công.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
