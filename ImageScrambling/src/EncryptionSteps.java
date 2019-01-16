import java.awt.RenderingHints.Key;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;

public class EncryptionSteps {
	private static BufferedImage img = null;
	private static int width;
	private static int height;


	public static File initialize(File input) throws IOException{
		File out = input;
		if(input.getName().contains(".tif")){
			FileSeekableStream stream = null;

			stream = new FileSeekableStream(input);
			ImageDecoder dec = ImageCodec.createImageDecoder("tiff", stream,null);
			RenderedImage image =   dec.decodeAsRenderedImage(0);
			JAI.create("filestore",image ,"img/out.jpg","JPEG");
			out = new File ("img/out.jpg");
		}
		img = ImageIO.read(out);

		//get width and height
		width = img.getWidth();
		height = img.getHeight();
		return out;
	}

	public static File extractBlue(File f) throws IOException{
		img = ImageIO.read(f);

		//convert to blue image
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				int p = img.getRGB(x,y);

				int a = (p>>24)&0xff;
				int b = p&0xff;

				//set new RGB
				p = (a<<24) | (0<<16) | (0<<8) | b;

				img.setRGB(x, y, p);
			}
		}

		//write image
		try{
			f = new File("img/blue.jpg");
			ImageIO.write(img, "jpg", f);
		}catch(IOException e){
			System.out.println(e);
		}
		return f;
	}

	public static File extractGreen(File f) throws IOException{
		img = ImageIO.read(f);

		//convert to green image
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				int p = img.getRGB(x,y);

				int a = (p>>24)&0xff;
				int g = (p>>8)&0xff;

				//set new RGB
				p = (a<<24) | (0<<16) | (g<<8) | 0;

				img.setRGB(x, y, p);
			}
		}

		//write image
		try{
			f = new File("img/green.jpg");
			ImageIO.write(img, "jpg", f);
		}catch(IOException e){
			System.out.println(e);
		}
		return f;
	}

	public static File extractRed(File f) throws IOException{
		img = ImageIO.read(f);

		//convert to red image
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				int p = img.getRGB(x,y);

				int a = (p>>24)&0xff;
				int r = (p>>16)&0xff;

				//set new RGB
				p = (a<<24) | (r<<16) | (0<<8) | 0;

				img.setRGB(x, y, p);
			}
		}

		//write image
		try{
			f = new File("img/red.jpg");
			ImageIO.write(img, "jpg", f);
		}catch(IOException e){
			System.out.println(e);
		}
		return f;

	}

	public static File rgb2ybcr(File input) throws IOException{
		BufferedImage img = null; 
		img = ImageIO.read(input);
		// get width and height 
		int width = img.getWidth(); 
		int height = img.getHeight(); 

		BufferedImage ycb =  new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

		// convert to blue image 
		for (int y = 0; y < height; y++) 
		{ 
			for (int x = 0; x < width; x++) 
			{ 
				int p = img.getRGB(x,y); 

				int b = p&0xff; 
				int g = (p>>8)&0xff; 
				int r = (p>>16)&0xff; 

				int Y = (int)(0.299*r+0.587*g+0.114*b);
				int Cb=(int)(-0.169*r - 0.331*g + 0.500*b)+128;
				int Cr =(int)(0.500*r-0.419*g-0.081*b)+128;

				int val = (Y<<16) | (Cb<<8) | Cr;
				ycb.setRGB(x,y,val);
			} 
		} 

		try {
			String color = input.getName();
			if(color.contains("blue")){
				input = new File("img/blueYCBR.jpg");
				ImageIO.write(ycb,"jpg", input);
			}else if(color.contains("red")){
				input = new File("img/redYCBR.jpg");
				ImageIO.write(ycb,"jpg", input);
			}else if(color.contains("green")){
				input = new File("img/greenYCBR.jpg");
				ImageIO.write(ycb,"jpg", input);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return input; 

	}

	public static File concatenateImage(File file1, File file2,File file3) throws IOException{
		BufferedImage img1 = ImageIO.read(file1);
		BufferedImage img2 = ImageIO.read(file2);
		BufferedImage img3 = ImageIO.read(file3);

		File final_image = null;

		int widthImg1 = img1.getWidth();
		int heightImg1 = img1.getHeight();

		Random rand=new Random();
		Keys.splitMode = rand.nextInt(2);
		System.out.println(Keys.splitMode);
		
		if(Keys.splitMode == 0){
			// horizontally
			System.out.println("HORIZONTALLY");
			BufferedImage imgH = new BufferedImage(
					widthImg1+widthImg1+widthImg1, // Final image will have width and height as
					heightImg1, // addition of widths and heights of the images we already have
					BufferedImage.TYPE_INT_RGB);

			imgH.createGraphics().drawImage(img1, 0, 0, null); // 0, 0 are the x and y positions

			imgH.createGraphics().drawImage(img2, widthImg1, 0, null); // here width is mentioned as width of

			imgH.createGraphics().drawImage(img3, widthImg1+widthImg1, 0, null); // here width is mentioned as width of

			final_image = new File("img/Final.jpg"); //png can also be used here
			ImageIO.write(imgH, "jpeg", final_image); //if png is used, write "png" instead "jpeg"
		}else{
			//Vertically
			System.out.println("VERTICALLY");
			BufferedImage imgV = new BufferedImage(
					widthImg1, // Final image will have width and height as
					heightImg1+heightImg1+heightImg1, // addition of widths and heights of the images we already have
					BufferedImage.TYPE_INT_RGB);

			imgV.createGraphics().drawImage(img1, 0, 0, null); // 0, 0 are the x and y positions

			imgV.createGraphics().drawImage(img2, 0, heightImg1, null); // here width is mentioned as width of

			imgV.createGraphics().drawImage(img3, 0, heightImg1+heightImg1, null); // here width is mentioned as width of

			final_image = new File("img/Final.jpg"); //png can also be used here
			ImageIO.write(imgV, "jpeg", final_image); //if png is used, write "png" instead "jpeg"
		}

		return final_image;
	}

	public static File convertToGray(File input)throws IOException{
		BufferedImage img = ImageIO.read(input);
		File result = null;

		//get image width and height
		int width = img.getWidth();
		int height = img.getHeight();

		//convert to grayscale
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
			result = new File("img/grayscale.jpg");
			ImageIO.write(img, "jpg", result);
		}catch(IOException e){
			System.out.println(e);
		}
		
		Files.deleteIfExists(Paths.get("img/out.jpg"));

		return result;
	}

}