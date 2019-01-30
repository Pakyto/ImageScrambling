import java.awt.image.BufferedImage;

public class ImageBlock {
	
	private BufferedImage block;
	private int i;
	private int j;
	
	public ImageBlock(BufferedImage block) {
		super();
		this.block = block;
		//this.i = i;
		//this.j = j;
	}

	
	@Override
	public String toString() {
		return "ImageBlock [block=" + block + ", i=" + i + ", j=" + j + "]";
	}


	public BufferedImage getBlock() {
		return block;
	}

	public void setBlock(BufferedImage block) {
		this.block = block;
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
	
	

}
