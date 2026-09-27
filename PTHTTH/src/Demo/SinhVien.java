package Demo;

public class SinhVien {
	String maSV;
	String hoTen;
	double diemTB;
	
	//contructor
	public SinhVien(String maSV, String hoTen, double diemTB) {
		super();
		this.maSV = maSV;
		this.hoTen = hoTen;
		this.diemTB = diemTB;
	}
	
	void hienThiThongTin() {
		System.out.println("MSSV: "+ maSV + " co ten la: " + hoTen);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SinhVien sv1 = new SinhVien("001", "Hoang Vu An",10);
		sv1.hienThiThongTin();
	}

}
