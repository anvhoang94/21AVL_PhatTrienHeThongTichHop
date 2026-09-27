package Demo;

public class SanPham {
	String maSP;
	String tenSP;
	Double donGia;
	int soLuong;
	

	public SanPham(String maSP, String tenSP, Double donGia, int soLuong) {
		super();
		this.maSP = maSP;
		this.tenSP = tenSP;
		this.donGia = donGia;
		this.soLuong = soLuong;
	}
	
	void hienThiThongTin() {
		System.out.println("Mã sản phẩm: "+ maSP + " có tên là: " + tenSP + " có giá là: " + donGia + " số lượng : " + soLuong + " Thành tiền: " + tinhThanhTien());
	}
	double tinhThanhTien() {
		return donGia * soLuong;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SanPham sp1 = new SanPham("001", "Sửa tắm LifeBouy",13.000 , 12);
		sp1.hienThiThongTin();
	}

}
