
public class RGB {
	private int r;
	private int g;
	private int b;
	private int lum;
	
	public RGB(int r, int g, int b, int lum) {
		super();
		this.r = r;
		this.g = g;
		this.b = b;
		this.lum = lum;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

	public int getLum() {
		return lum;
	}

	public void setLum(int lum) {
		this.lum = lum;
	}

	@Override
	public String toString() {
		return "RGB [r=" + r + ", g=" + g + ", b=" + b + ", lum=" + lum + "]";
	}
	

	
	
	

}
