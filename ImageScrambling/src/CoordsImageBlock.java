
public class CoordsImageBlock {
	private int i;
	private int j;
	private int ref;
	
	
	public CoordsImageBlock(){}
	
	public CoordsImageBlock(int ref,int i, int j) {
		super();
		this.i = i;
		this.j = j;
		this.ref = ref;
	}

	public CoordsImageBlock(int i, int j) {
		super();
		this.i = i;
		this.j = j;
	}

	
	public int getRef() {
		return ref;
	}

	public void setRef(int ref) {
		this.ref = ref;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getJ() {
		return j;
	}

	public void setJ(int j) {
		this.j = j;
	}

	@Override
	public String toString() {
		return "CoordsImageBlock [i=" + i + ", j=" + j + ", ref=" + ref + "]";
	}

	
	
	
	
}
