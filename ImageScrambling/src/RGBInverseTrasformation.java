
public class RGBInverseTrasformation {
	private int cols;
	private int rows;

	
	public RGBInverseTrasformation(int cols,int rows){
		this.cols = cols;
		this.rows = rows;
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


	@Override
	public String toString() {
		return "RGBInverseTrasformation [cols=" + cols + ", rows=" + rows + "]";
	}

	
}
	