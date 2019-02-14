
public class KeysInvRotationInversion {
	
	private int cols;
	private int rows;
	private int rotation;
	private int inversion;
	
	public KeysInvRotationInversion(int cols, int rows){
		this.cols = cols;
		this.rows = rows;
	}
	
	public KeysInvRotationInversion(int cols, int rows, int rotation, int inversion) {
		super();
		this.cols = cols;
		this.rows = rows;
		this.rotation = rotation;
		this.inversion = inversion;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public int getInversion() {
		return inversion;
	}

	public void setInversion(int inversion) {
		this.inversion = inversion;
	}

	@Override
	public String toString() {
		return "KeysInvRotationInversion [cols=" + cols + ", rows=" + rows + ", rotation=" + rotation + ", inversion="
				+ inversion + "]";
	}
	
	
	
	

}
