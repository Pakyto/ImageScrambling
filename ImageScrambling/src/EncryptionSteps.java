import java.awt.RenderingHints.Key;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Random;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

public class EncryptionSteps {
	private static BufferedImage img = null;
	private static int width;
	private static int height;


	public static File initialize(File input) throws IOException{
		File out = input;
		
		Files.deleteIfExists(Paths.get("img/out.jpg"));
		
		if(input.getName().contains(".tif")){
			FileSeekableStream stream = null;

			stream = new FileSeekableStream(input);
			ImageDecoder dec = ImageCodec.createImageDecoder("tiff", stream,null);
			RenderedImage image =   dec.decodeAsRenderedImage(0);
			BufferedImage bi = PlanarImage.wrapRenderedImage(image).getAsBufferedImage();
			//JAI.create("filestore",image ,"img/out.jpg","JPEG");
			out = new File ("img/out.jpg");
			writeImage(out, bi);
		}
		img = ImageIO.read(out);

		//get width and height
		width = img.getWidth();
		height = img.getHeight();
		return out;
	}

	public static File initializeEval(File input, int index) throws IOException{
		File out = input;
		
		Files.deleteIfExists(Paths.get("img/out.jpg"));
		
		if(input.getName().contains(".tif")){
			FileSeekableStream stream = null;

			stream = new FileSeekableStream(input);
			ImageDecoder dec = ImageCodec.createImageDecoder("tiff", stream,null);
			RenderedImage image =   dec.decodeAsRenderedImage(0);
			BufferedImage bi = PlanarImage.wrapRenderedImage(image).getAsBufferedImage();
			//JAI.create("filestore",image ,"img/out.jpg","JPEG");
			out = new File ("img/out"+index+".jpg");
			writeImage(out, bi);
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
			
			writeImage(f, img);
			//ImageIO.write(img, "jpg", f);
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
			writeImage(f, img);

			//ImageIO.write(img, "jpg", f);
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
			writeImage(f, img);

			//ImageIO.write(img, "jpg", f);
		}catch(IOException e){
			System.out.println(e);
		}
		return f;

	}

	public static File rgb2ycbcr(File input) throws IOException{
		BufferedImage img = null; 
		img = ImageIO.read(input);
		// get width and height 
		int width = img.getWidth(); 
		int height = img.getHeight(); 

		BufferedImage ycb =  new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

		//Inizializzazioni delle matrici contenente i valori YCbCr per ogni colore

		if(input.getName().contains("red")){
			Keys.width = width;
			Keys.height = height;
			Keys.initializeRed();
		}else if(input.getName().contains("green")){
			Keys.width = width;
			Keys.height = height;
			Keys.initializeGreen();
		}else if(input.getName().contains("blue")){
			Keys.width = width;
			Keys.height = height;
			Keys.initializeBlue();
		}

		for (int y = 0; y < height; y++) { 
			for (int x = 0; x < width; x++){ 
				int p = img.getRGB(x,y); 

				int a = (p>>24)&0xff; 
				int b = p&0xff; 
				int g = (p>>8)&0xff; 
				int r = (p>>16)&0xff; 


				int Y = (int)(0.299*r+0.587*g+0.114*b);
				int Cb=(int)(-0.169*r - 0.331*g + 0.499*b)+128;
				int Cr =(int)(0.499*r-0.418*g-0.0813*b)+128;

				//int Y = (int)(0.299*r+0.587*g+0.114*b);
				//int Cb=(int)(-0.169*r - 0.331*g + 0.500*b)+128;
				//int Cr =(int)(0.500*r-0.419*g-0.081*b)+128;

				YCbCr value = new YCbCr(Y, Cb, Cr,a);

				//Vengono memorizzati i valori di YCbCr per ogni colore
				if(input.getName().contains("red")){
					Keys.addRed(y,x,value);
					//System.out.println("dsadad "+Keys.listValueYCbCrRed[y][x].toString());

				}else if(input.getName().contains("green")){
					Keys.addGreen(y,x,value);
				}else if(input.getName().contains("blue")){
					Keys.addBlue(y,x,value);
				}


				int val = (Y<<16) | (Cb<<8) | Cr;
				ycb.setRGB(x,y,val);
			} 
		}


		try {
			String color = input.getName();
			if(color.contains("blue")){
				input = new File("img/blueYCBR.jpg");
				writeImage(input, ycb);

				//ImageIO.write(ycb,"jpg", input);
			}else if(color.contains("red")){
				input = new File("img/redYCBR.jpg");
				writeImage(input, ycb);

				//ImageIO.write(ycb,"jpg", input);
			}else if(color.contains("green")){
				input = new File("img/greenYCBR.jpg");
				writeImage(input, ycb);

				//ImageIO.write(ycb,"jpg", input);
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
			//ImageIO.write(imgH, "jpeg", final_image); //if png is used, write "png" instead "jpeg"			
			writeImage(final_image, imgH);
			//ImageIO.write(imgH, "jpeg", new File("Decrypt/KEY.jpg"));  //Viene salvata l'immagine concatenata come chiave
			//writeImage(new File("Decrypt/KEY.jpg"), imgH);

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
			//ImageIO.write(imgV, "jpeg", final_image); //if png is used, write "png" instead "jpeg"
			writeImage(final_image, imgV);
			//ImageIO.write(imgV, "jpeg", new File("Decrypt/KEY.jpg"));  //Viene salvata l'immagine concatenata come chiave
			//writeImage(new File("Decrypt/KEY.jpg"), imgV);

		}

		return final_image;
	}

	public static File convertToGray(File input)throws IOException{
		BufferedImage img = ImageIO.read(input);
		File result = null;

		
		//get image width and height
		int width = img.getWidth();
		int height = img.getHeight();

		Keys.height = height;
		Keys.width = width;
		Keys.initializeRGB();
		
		//convert to grayscale
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				int p = img.getRGB(x,y);

				int a = (p>>24)&0xff;
				int r = (p>>16)&0xff;
				int g = (p>>8)&0xff;
				int b = p&0xff;

				RGB rgb = new RGB(r, g, b, a);
				Keys.addRGB(y, x, rgb);    //Vengono memorizzati i valori RGB delle immagine YCbCr
				
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
			//writeImage(result, img);
		}catch(IOException e){
			System.out.println(e);
		}

		//Files.deleteIfExists(Paths.get("img/out.jpg"));

		return result;
	}

	
	/**
	 * source https://apilevel.wordpress.com/2014/08/03/jpeg-quality-and-size-reduction-when-using-imageio-write/
	 * @param file
	 * @param img
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	
	public static void writeImage(File file,BufferedImage img) throws FileNotFoundException, IOException{
		FileImageOutputStream output = new FileImageOutputStream(file);

		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
		ImageWriter writer = iter.next();
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwp.setCompressionQuality(1.0f);
		writer.setOutput(output);
		writer.write(null, new IIOImage(img ,null,null),iwp);
		writer.dispose();
	}
	
}