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

		BufferedImage rgb = img;
		
		Color myWhite = new Color(255, 255, 255);
		int r = myWhite.getRGB();
		
		for (int x = 0; x < width; x++) { 
			for (int y = 0; y < height; y++){ 
								
				int p = img.getRGB(x, y);

				
				int a = (p>>24)&0xff;
				int red = (p>>16)&0xff;

				p = (a<<24) | (red<<16) | (0<<8) | 0;
				rgb.setRGB(x,y,p);

				/*
				int y = p&0xff;
				int u = (p>>8)&0xff;
				int v = (p>>16)&0xff; 

				
				int Y = (int) (y + 1.140f * v);
				int Cb=(int)(y - 0.394f * u - 0.581f * v);
				int Cr =(int)(y + 2.028f * u);
				*/
			
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
		
		
		
		/*
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 

		System.out.println("inputt "+input.getName());
        byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();

        Mat mat = new Mat(height,width, CvType.CV_8UC1);		
        mat.put(0, 0, data);
        */
        
        //Mat destination = new Mat(); 

		//Imgproc.cvtColor(source, destination, Imgproc.COLOR_GRAY2RGB); 
		
		//Imgcodecs.imwrite("Decrypt/test.jpg", destination); 
		 


		/*
		//convert to ycbr
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				
				int grey = img.getRGB(x, y); 
               
				int rgb = grey << 16 | grey << 8 | grey;
				
				ycb.setRGB(x, y, rgb);

			}
		}



		try {
			String color = input.getName();
			System.out.println("colorrr "+color);
			if(color.contains("Blue")){
				input = new File("Decrypt/blueYCBR.jpg");
				ImageIO.write(ycb,"jpg", input);
			}else if(color.contains("Red")){
				input = new File("Decrypt/redYCBR.jpg");
				ImageIO.write(ycb,"jpg", input);
			}else if(color.contains("Green")){
				input = new File("Decrypt/greenYCBR.jpg");
				ImageIO.write(ycb,"jpg", input);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		*/
		
		

	}
}
