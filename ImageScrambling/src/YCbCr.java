
public class YCbCr {
	private int Y;
	private int Cb;
	private int Cr;
	private int lum;
	

	public YCbCr(int y, int cb, int cr,int lum) {
		super();
		Y = y;
		Cb = cb;
		Cr = cr;
		this.lum = lum;
	}

	
	public int getLum() {
		return lum;
	}


	public void setLum(int lum) {
		this.lum = lum;
	}


	public int getY() {
		return Y;
	}

	public void setY(int y) {
		Y = y;
	}

	public int getCb() {
		return Cb;
	}

	public void setCb(int cb) {
		Cb = cb;
	}

	public int getCr() {
		return Cr;
	}

	public void setCr(int cr) {
		Cr = cr;
	}

	@Override
	public String toString() {
		return "YCbCr [Y=" + Y + ", Cb=" + Cb + ", Cr=" + Cr + "]";
	}
	
	

}
