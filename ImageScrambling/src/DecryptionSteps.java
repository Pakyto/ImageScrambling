import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class DecryptionSteps {

	private BufferedImage[][] imgKey = Keys.imageKey;
	private static BufferedImage img ;


	public static void separateImage() throws IOException{

		img = ImageIO.read(new File("Decrypt/KEY.jpg"));

		//Crop imageKey into 3 images
		
		if(Keys.splitMode==0){ //Horizontal split Mode

			int width = img.getWidth()/3;

			BufferedImage img1 = new BufferedImage(width, img.getHeight(), BufferedImage.TYPE_INT_RGB);

			img1.createGraphics().drawImage(img, 0, 0, null);

			ImageIO.write(img1, "jpeg", new File("Decrypt/img1.jpg"));


			BufferedImage crop = img.getSubimage(width, 0, width, img.getHeight());

			img1.createGraphics().drawImage(crop, 0, 0, null);

			ImageIO.write(img1, "jpeg", new File("Decrypt/img2.jpg"));

			BufferedImage crop2 = img.getSubimage(width+width, 0, width, img.getHeight());

			img1.createGraphics().drawImage(crop2, 0, 0, null);

			ImageIO.write(img1, "jpeg", new File("Decrypt/img3.jpg"));


		}else{ //Vertical mode
			int height = img.getHeight()/3;

			BufferedImage img1 = new BufferedImage(img.getWidth(), height, BufferedImage.TYPE_INT_RGB);

			img1.createGraphics().drawImage(img, 0, 0, null);

			ImageIO.write(img1, "jpeg", new File("Decrypt/img1.jpg"));


			BufferedImage crop = img.getSubimage(0, height, img.getWidth(), height);

			img1.createGraphics().drawImage(crop, 0, 0, null);

			ImageIO.write(img1, "jpeg", new File("Decrypt/img2.jpg"));


			BufferedImage crop2 = img.getSubimage(0, height+height, img.getWidth(), height);

			img1.createGraphics().drawImage(crop2, 0, 0, null);

			ImageIO.write(img1, "jpeg", new File("Decrypt/img3.jpg"));
		}
	}


	public static void gray2Ycbr() throws IOException {

		System.out.println("widthhhh "+Keys.width);

		int totalWidth = Keys.width;
		int totalHeight = Keys.height;
		int cols = Keys.cols;
		int rows = Keys.rows;
		int type = Keys.type;

		File result = null;

		//get image width and height
		int width = img.getWidth();
		int height = img.getHeight();


		//convert to ycbr
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				int p = img.getRGB(x,y);

				int a = (p>>24)&0xff;
				int r = (p>>16)&0xff;
				int g = (p>>8)&0xff;
				int b = p&0xff;

				//calculate average
				int avg = (r+g+b)/3;

				//replace RGB value with avg
				p = (a<<24) | (avg<<16) | (avg<<8) | avg;

				img.setRGB(x, y, p);
			}
		}

		//write image
		try{
			result = new File("img/DecrGrayToYcbr.jpg");
			ImageIO.write(img, "jpg", result);
		}catch(IOException e){
			System.out.println(e);
		}



		//return result;

		/*
		BufferedImage combineImage = new BufferedImage(totalWidth, totalHeight, type);
		int stackWidth = 0;
		int stackHeight = 0;
		for (int i = 0; i <= cols; i++) {
			for (int j = 0; j <= rows; j++) {
				combineImage.createGraphics().drawImage(imageRejoined[i][j], stackWidth, stackHeight, null);
				stackHeight += imageRejoined[i][j].getHeight();
			}
			stackWidth += imageRejoined[i][0].getWidth();
			stackHeight = 0;
		}

		ImageIO.write(combineImage, "jpg", new File("img/KEY.jpg"));
		System.out.println("Image rejoin done.");
		 */

	}
}
