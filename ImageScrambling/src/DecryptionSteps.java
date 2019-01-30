import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBufferByte;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class DecryptionSteps {

	private static BufferedImage img;

	public static void gray2YCbCr() throws IOException{
		img = ImageIO.read(new File("Decrypt/KEY.jpg"));
		
		int width = img.getWidth();
		int height = img.getHeight();
		
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				int p;
				
				RGB rgb = Keys.listValueRGB[y][x];
				
				int a = rgb.getLum();
				int r = rgb.getR();
				int g = rgb.getG();
				int b = rgb.getB();

				p = (a<<24) | (r<<16) | (g<<8) | b;

				img.setRGB(x, y, p);
			}
		}
		
		writeImage(new File("Decrypt/YCbCr.jpg"), img);
	}

	public static void separateImage() throws IOException{

		img = ImageIO.read(new File("Decrypt/YCbCr.jpg"));

		//Crop imageKey into 3 images

		if(Keys.splitMode==0){ //Horizontal split Mode
			int width = img.getWidth()/3;
			BufferedImage img1 = new BufferedImage(width, img.getHeight(), BufferedImage.TYPE_INT_RGB);
			img1.createGraphics().drawImage(img, 0, 0, null);
			writeImage(new File("Decrypt/redYCBR.jpg"), img1);
			//ImageIO.write(img1, "jpeg", new File("Decrypt/redYCBR.jpg"));
			BufferedImage crop = img.getSubimage(width, 0, width, img.getHeight());
			img1.createGraphics().drawImage(crop, 0, 0, null);
			writeImage(new File("Decrypt/greenYCBR.jpg"), img1);

			//ImageIO.write(img1, "jpeg", new File("Decrypt/greenYCBR.jpg"));
			BufferedImage crop2 = img.getSubimage(width+width, 0, width, img.getHeight());
			img1.createGraphics().drawImage(crop2, 0, 0, null);
			writeImage(new File("Decrypt/blueYCBR.jpg"), img1);

			//ImageIO.write(img1, "jpeg", new File("Decrypt/blueYCBR.jpg"));

		}else{ //Vertical mode
			int height = img.getHeight()/3;
			BufferedImage img1 = new BufferedImage(img.getWidth(), height, BufferedImage.TYPE_INT_RGB);
			img1.createGraphics().drawImage(img, 0, 0, null);
			writeImage(new File("Decrypt/redYCBR.jpg"), img1);

			//ImageIO.write(img1, "jpeg", new File("Decrypt/redYCBR.jpg"));
			BufferedImage crop = img.getSubimage(0, height, img.getWidth(), height);
			img1.createGraphics().drawImage(crop, 0, 0, null);
			writeImage(new File("Decrypt/greenYCBR.jpg"), img1);

			//ImageIO.write(img1, "jpeg", new File("Decrypt/greenYCBR.jpg"));
			BufferedImage crop2 = img.getSubimage(0, height+height, img.getWidth(), height);
			img1.createGraphics().drawImage(crop2, 0, 0, null);
			writeImage(new File("Decrypt/blueYCBR.jpg"), img1);

			//ImageIO.write(img1, "jpeg", new File("Decrypt/blueYCBR.jpg"));
		}
	}


	public static void Ycbr2Rgb(File input) throws IOException {
		img = ImageIO.read(input);
		//get image width and height
		int width = img.getWidth();
		int height = img.getHeight();

		BufferedImage rgb = ImageIO.read(input);

		for (int y = 0; y < height; y++) { 
			for (int x = 0; x < width; x++){ 

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
			if(color.contains("blue")){
				input = new File("Decrypt/blue.jpg");
				writeImage(input, rgb);
				//ImageIO.write(rgb,"jpg", input);
			}else if(color.contains("red")){
				input = new File("Decrypt/red.jpg");
				writeImage(input, rgb);

				//ImageIO.write(rgb,"jpg", input);
			}else if(color.contains("green")){
				input = new File("Decrypt/green.jpg");
				writeImage(input, rgb);

				//ImageIO.write(rgb,"jpg", input);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void mergeRGB(File r, File g, File b) throws IOException{
		BufferedImage red = ImageIO.read(r);
		BufferedImage green = ImageIO.read(g);
		BufferedImage blue = ImageIO.read(b);

		BufferedImage base = new BufferedImage(red.getWidth(), red.getHeight(), BufferedImage.TYPE_INT_RGB);


		for(int y = 0; y< red.getHeight(); y++){
			for(int x = 0; x < red.getWidth(); x++){

				int rgb = (red.getRGB(x, y) & 0x00FF0000) | (green.getRGB(x, y) & 0x0000FF00) | (blue.getRGB(x, y) & 0x000000FF);
		        
		        base.setRGB(x, y, (rgb | 0xFF000000));
			}
		}

		//ImageIO.write(base,"jpg", new File("Decrypt/final.jpg"));
		File outfile = new File("Decrypt/final.jpg");
		
		Kernel kernel = new Kernel(3, 3, new float[] {
													  0.0f ,-0.3f,  0.0f,
													  -0.3f , 2.2f, -0.3f,
												       0.0f ,-0.3f,  0.0f
												       });
	    BufferedImageOp op = new ConvolveOp(kernel);
	    base = op.filter(base, null);
		
		writeImage(outfile, base);

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
