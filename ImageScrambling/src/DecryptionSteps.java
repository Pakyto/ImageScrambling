import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
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

	public static void inverseNegativePositve() throws IOException{

		BufferedImage[][] imgs = Keys.imageKey;

		BufferedImage[][] finalsImg = new BufferedImage[Keys.totalwidth+1][Keys.height+1];

		BufferedImage combineImage = new BufferedImage(Keys.totalwidth, Keys.totalheight, Keys.type);

		int stackWidth = 0;
		int stackHeight = 0;


		for (int i = 0; i <= Keys.cols; i++) {
			for (int j = 0; j <= Keys.rows; j++) {

				int w = imgs[i][j].getWidth();  
				int h = imgs[i][j].getHeight(); 

				
				RGBInverseTrasformation rgb = Keys.listValueRGBInverse[j][i];
				//Inverse negative-positive transformation
				for(int y = 0; y < h; y++){
					for(int x = 0; x < w; x++){
						int p = imgs[i][j].getRGB(x, y);	
						int a = (p>>24)&0xff;
						int r = (p>>16)&0xff;
						int gr = (p>>8)&0xff;
						int b = p&0xff;

						if(rgb != null){
							if(rgb.getCols()==i && rgb.getRows()==j){

								r = 255 - r;
								gr = 255 - gr;
								b = 255 - b;
							}

						}
						p = (a<<24) | (r<<16) | (gr<<8) | b;

						imgs[i][j].setRGB(x, y, p);
					}
				}



				KeysInvRotationInversion key = Keys.keysInvRotationInverse[j][i];
				int rotation = key.getRotation();
				int inversion = key.getInversion();

				//System.out.println(key.toString());

				//Inversione blocchi
				switch (inversion) {
				case 0:{
					break;
				}
				case 1:{
					BufferedImage dimg = new BufferedImage(w, h, imgs[i][j].getType());  
					Graphics2D g = dimg.createGraphics();  
					g.drawImage(imgs[i][j], 0, 0, w, h, w, 0, 0, h, null); 
					g.dispose();

					imgs[i][j] = dimg;
					break;
				}
				case 2:{
					BufferedImage dimg = new BufferedImage(w, h, imgs[i][j].getType());  
					Graphics2D g = dimg.createGraphics();  
					g.drawImage(imgs[i][j],0, 0, w, h, 0, h, w, 0, null);  
					g.dispose();

					imgs[i][j] = dimg;
					break;
				}
				case 3:{
					BufferedImage dimg = new BufferedImage(w, h, imgs[i][j].getType());  
					Graphics2D g = dimg.createGraphics();  
					g.drawImage(imgs[i][j], 0, 0, w, h, w, 0, 0, h, null);  //Horizontally
					g.dispose();

					imgs[i][j] = dimg;

					BufferedImage dimg2 = new BufferedImage(w, h, imgs[i][j].getType());  
					Graphics2D g2 = dimg2.createGraphics();  
					g2.drawImage(imgs[i][j], 0, 0, w, h, 0, h, w, 0, null); 		//Vertically
					g2.dispose();

					imgs[i][j] = dimg2;
					break;
				}
				}

				//Inverse rotation
				switch(rotation){
				case 0:{
					break;
				}
				case 90:{
					double rads = Math.toRadians(270);
					double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));

					int newWidth = (int) Math.floor(w * cos + h * sin);
					int newHeight = (int) Math.floor(h * cos + w * sin);

					BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
					Graphics2D g2d = rotated.createGraphics();
					AffineTransform at = new AffineTransform();
					at.translate((newWidth - w) / 2, (newHeight - h) / 2);

					int x = w / 2;
					int y = h / 2;

					at.rotate(rads, x, y);
					g2d.setTransform(at);
					g2d.drawImage(imgs[i][j], 0, 0, null);
					imgs[i][j] = rotated;

					break;					
				}
				case 180:{

					double rads = Math.toRadians(180);
					double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));

					int newWidth = (int) Math.floor(w * cos + h * sin);
					int newHeight = (int) Math.floor(h * cos + w * sin);

					BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
					Graphics2D g2d = rotated.createGraphics();
					AffineTransform at = new AffineTransform();
					at.translate((newWidth - w) / 2, (newHeight - h) / 2);

					int x = w / 2;
					int y = h / 2;

					at.rotate(rads, x, y);
					g2d.setTransform(at);
					g2d.drawImage(imgs[i][j], 0, 0, null);
					imgs[i][j] = rotated;

					break;
				}
				case 270:{
					double rads = Math.toRadians(90);
					double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));

					int newWidth = (int) Math.floor(w * cos + h * sin);
					int newHeight = (int) Math.floor(h * cos + w * sin);

					BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
					Graphics2D g2d = rotated.createGraphics();
					AffineTransform at = new AffineTransform();
					at.translate((newWidth - w) / 2, (newHeight - h) / 2);

					int x = w / 2;
					int y = h / 2;

					at.rotate(rads, x, y);
					g2d.setTransform(at);
					g2d.drawImage(imgs[i][j], 0, 0, null);
					imgs[i][j] = rotated;
					break;
				}
				}
			}
		
		}

		//Block assembling
		BufferedImage image = null;
		for(int i=0; i<imgs.length; i++){
			for(int j=0; j<imgs[i].length; j++){
				for(CoordsImageBlock cords : Keys.listCoordsImage.keySet()){
					if(cords.getI() == i && cords.getJ() == j){
						image = Keys.listCoordsImage.get(cords);
					}
				}
				finalsImg[i][j] = image;
			}

		}
		
		for (int i = 0; i <= Keys.cols; i++) {
			for (int j = 0; j <= Keys.rows; j++) {	
				combineImage.createGraphics().drawImage(finalsImg[i][j], stackWidth, stackHeight, null);
				stackHeight += finalsImg[i][j].getHeight();
			}
			stackWidth += finalsImg[i][0].getWidth();
			stackHeight = 0;
		}


		writeImage(new File("Decrypt/finalInverse.jpg"), combineImage);
	}
	
	public static void inverseNegativePositveEval(int index) throws IOException{

		BufferedImage[][] imgs = Keys.imageKey;

		BufferedImage[][] finalsImg = new BufferedImage[Keys.totalwidth+1][Keys.height+1];

		BufferedImage combineImage = new BufferedImage(Keys.totalwidth, Keys.totalheight, Keys.type);

		int stackWidth = 0;
		int stackHeight = 0;


		for (int i = 0; i <= Keys.cols; i++) {
			for (int j = 0; j <= Keys.rows; j++) {

				int w = imgs[i][j].getWidth();  
				int h = imgs[i][j].getHeight(); 

				
				RGBInverseTrasformation rgb = Keys.listValueRGBInverse[j][i];
				//Inverse negative-positive transformation
				for(int y = 0; y < h; y++){
					for(int x = 0; x < w; x++){
						int p = imgs[i][j].getRGB(x, y);	
						int a = (p>>24)&0xff;
						int r = (p>>16)&0xff;
						int gr = (p>>8)&0xff;
						int b = p&0xff;

						if(rgb != null){
							if(rgb.getCols()==i && rgb.getRows()==j){

								r = 255 - r;
								gr = 255 - gr;
								b = 255 - b;
							}

						}
						p = (a<<24) | (r<<16) | (gr<<8) | b;

						imgs[i][j].setRGB(x, y, p);
					}
				}



				KeysInvRotationInversion key = Keys.keysInvRotationInverse[j][i];
				int rotation = key.getRotation();
				int inversion = key.getInversion();

				//System.out.println(key.toString());

				//Inversione blocchi
				switch (inversion) {
				case 0:{
					break;
				}
				case 1:{
					BufferedImage dimg = new BufferedImage(w, h, imgs[i][j].getType());  
					Graphics2D g = dimg.createGraphics();  
					g.drawImage(imgs[i][j], 0, 0, w, h, w, 0, 0, h, null); 
					g.dispose();

					imgs[i][j] = dimg;
					break;
				}
				case 2:{
					BufferedImage dimg = new BufferedImage(w, h, imgs[i][j].getType());  
					Graphics2D g = dimg.createGraphics();  
					g.drawImage(imgs[i][j],0, 0, w, h, 0, h, w, 0, null);  
					g.dispose();

					imgs[i][j] = dimg;
					break;
				}
				case 3:{
					BufferedImage dimg = new BufferedImage(w, h, imgs[i][j].getType());  
					Graphics2D g = dimg.createGraphics();  
					g.drawImage(imgs[i][j], 0, 0, w, h, w, 0, 0, h, null);  //Horizontally
					g.dispose();

					imgs[i][j] = dimg;

					BufferedImage dimg2 = new BufferedImage(w, h, imgs[i][j].getType());  
					Graphics2D g2 = dimg2.createGraphics();  
					g2.drawImage(imgs[i][j], 0, 0, w, h, 0, h, w, 0, null); 		//Vertically
					g2.dispose();

					imgs[i][j] = dimg2;
					break;
				}
				}

				//Inverse rotation
				switch(rotation){
				case 0:{
					break;
				}
				case 90:{
					double rads = Math.toRadians(270);
					double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));

					int newWidth = (int) Math.floor(w * cos + h * sin);
					int newHeight = (int) Math.floor(h * cos + w * sin);

					BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
					Graphics2D g2d = rotated.createGraphics();
					AffineTransform at = new AffineTransform();
					at.translate((newWidth - w) / 2, (newHeight - h) / 2);

					int x = w / 2;
					int y = h / 2;

					at.rotate(rads, x, y);
					g2d.setTransform(at);
					g2d.drawImage(imgs[i][j], 0, 0, null);
					imgs[i][j] = rotated;

					break;					
				}
				case 180:{

					double rads = Math.toRadians(180);
					double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));

					int newWidth = (int) Math.floor(w * cos + h * sin);
					int newHeight = (int) Math.floor(h * cos + w * sin);

					BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
					Graphics2D g2d = rotated.createGraphics();
					AffineTransform at = new AffineTransform();
					at.translate((newWidth - w) / 2, (newHeight - h) / 2);

					int x = w / 2;
					int y = h / 2;

					at.rotate(rads, x, y);
					g2d.setTransform(at);
					g2d.drawImage(imgs[i][j], 0, 0, null);
					imgs[i][j] = rotated;

					break;
				}
				case 270:{
					double rads = Math.toRadians(90);
					double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));

					int newWidth = (int) Math.floor(w * cos + h * sin);
					int newHeight = (int) Math.floor(h * cos + w * sin);

					BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
					Graphics2D g2d = rotated.createGraphics();
					AffineTransform at = new AffineTransform();
					at.translate((newWidth - w) / 2, (newHeight - h) / 2);

					int x = w / 2;
					int y = h / 2;

					at.rotate(rads, x, y);
					g2d.setTransform(at);
					g2d.drawImage(imgs[i][j], 0, 0, null);
					imgs[i][j] = rotated;
					break;
				}
				}
			}
		
		}

		//Block assembling
		BufferedImage image = null;
		for(int i=0; i<imgs.length; i++){
			for(int j=0; j<imgs[i].length; j++){
				for(CoordsImageBlock cords : Keys.listCoordsImage.keySet()){
					if(cords.getI() == i && cords.getJ() == j){
						image = Keys.listCoordsImage.get(cords);
					}
				}
				finalsImg[i][j] = image;
			}

		}
		
		for (int i = 0; i <= Keys.cols; i++) {
			for (int j = 0; j <= Keys.rows; j++) {	
				combineImage.createGraphics().drawImage(finalsImg[i][j], stackWidth, stackHeight, null);
				stackHeight += finalsImg[i][j].getHeight();
			}
			stackWidth += finalsImg[i][0].getWidth();
			stackHeight = 0;
		}


		writeImage(new File("Decrypt/finalInverse"+index+".jpg"), combineImage);
	}

	public static void gray2YCbCr() throws IOException{
		img = ImageIO.read(new File("Decrypt/finalInverse.jpg"));

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

		

		writeImage(outfile, base);

	}
	
	public static void mergeRGBEval(File r, File g, File b, int index) throws IOException{
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
		File outfile = new File("Decrypt/final"+index+".jpg");

		
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
