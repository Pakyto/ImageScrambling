import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class DecryptionSteps {

	private static BufferedImage img;

	public static void separateImage() throws IOException{

		img = ImageIO.read(new File("Decrypt/KEY.jpg"));

		//Crop imageKey into 3 images

		if(Keys.splitMode==0){ //Horizontal split Mode
			int width = img.getWidth()/3;
			BufferedImage img1 = new BufferedImage(width, img.getHeight(), BufferedImage.TYPE_INT_RGB);
			img1.createGraphics().drawImage(img, 0, 0, null);
			ImageIO.write(img1, "jpeg", new File("Decrypt/grayRed.jpg"));
			BufferedImage crop = img.getSubimage(width, 0, width, img.getHeight());
			img1.createGraphics().drawImage(crop, 0, 0, null);
			ImageIO.write(img1, "jpeg", new File("Decrypt/grayGreen.jpg"));
			BufferedImage crop2 = img.getSubimage(width+width, 0, width, img.getHeight());
			img1.createGraphics().drawImage(crop2, 0, 0, null);
			ImageIO.write(img1, "jpeg", new File("Decrypt/grayBlue.jpg"));

		}else{ //Vertical mode
			int height = img.getHeight()/3;
			BufferedImage img1 = new BufferedImage(img.getWidth(), height, BufferedImage.TYPE_INT_RGB);
			img1.createGraphics().drawImage(img, 0, 0, null);
			ImageIO.write(img1, "jpeg", new File("Decrypt/grayRed.jpg"));
			BufferedImage crop = img.getSubimage(0, height, img.getWidth(), height);
			img1.createGraphics().drawImage(crop, 0, 0, null);
			ImageIO.write(img1, "jpeg", new File("Decrypt/grayGreen.jpg"));
			BufferedImage crop2 = img.getSubimage(0, height+height, img.getWidth(), height);
			img1.createGraphics().drawImage(crop2, 0, 0, null);
			ImageIO.write(img1, "jpeg", new File("Decrypt/grayBlue.jpg"));
		}
	}


	public static void gray2Ycbr(File input) throws IOException {

		img = ImageIO.read(input);

		//get image width and height
		int width = img.getWidth();
		int height = img.getHeight();

		BufferedImage rgb = ImageIO.read(input);

		for (int x = 0; x < width; x++) { 
			for (int y = 0; y < height; y++){ 

				int red,green,blue;
				
				int Y=0,Cb=0,Cr=0,lum = 0;
	    
				//Si prelevano i valori YCbCr per ogni colore per ottenere i valori Y Cb, Cr e la luminanza per ogni colore
				
	            if(input.getName().contains("red")){
	            	YCbCr val = Keys.listValueYCbCrRed[y][x];
					Y = val.getY();
					Cb = val.getCb();
					Cr = val.getCr();
					lum = val.getLum();
									
				}else if(input.getName().contains("green")){
					YCbCr val = Keys.listValueYCbCrGreen[y][x];
					Y = val.getY();
					Cb = val.getCb();
					Cr = val.getCr();
					lum = val.getLum();
					
				}else if(input.getName().contains("blue")){
					YCbCr val = Keys.listValueYCbCrBlue[y][x];
					Y = val.getY();
					Cb = val.getCb();
					Cr = val.getCr();
					lum = val.getLum();
				}
	            
	            //Si effettua il clamping per ottenere valori che sono compresi tra 0 e 255
	            red = Math.max(0, Math.min(255,(int)(Y + 1.402*(Cr - 128))));
	            green = Math.max(0, Math.min(255,(int)(Y - 0.344*(Cb - 128)-0.714*(Cr - 128))));
	            blue = Math.max(0, Math.min(255,(int)(Y + 1.772*(Cb - 128))));
	        	

				int val = (lum<<24)|(red<<16) | (green<<8) | blue;
				
				rgb.setRGB(x, y, val);

			} 
		} 

		try {
			String color = input.getName();
			System.out.println("colorrr "+color);
			if(color.contains("blue")){
				input = new File("Decrypt/blueYCBR.jpg");
				ImageIO.write(rgb,"jpg", input);
			}else if(color.contains("red")){
				input = new File("Decrypt/redYCBR.jpg");
				ImageIO.write(rgb,"jpg", input);
			}else if(color.contains("green")){
				input = new File("Decrypt/greenYCBR.jpg");
				ImageIO.write(rgb,"jpg", input);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		
	}
}
