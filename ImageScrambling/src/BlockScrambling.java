import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.io.FileUtils;

public class BlockScrambling {

	private static int totalCols,totalRows;
	private static int rowsOfSquare,colsOfSquare;
	static List<String>list = new ArrayList<>();


	public static void splitImage(File input,int blockSize)throws IOException{

		BufferedImage originalImg = ImageIO.read(input);

		int squareLength = blockSize;

		int originalWidth = originalImg.getWidth();
		int originalHeight = originalImg.getHeight();

		rowsOfSquare = originalHeight / squareLength;
		colsOfSquare = originalWidth / squareLength;
		int squaresCount = colsOfSquare * rowsOfSquare;
		System.out.println("total numblock "+squaresCount);



		totalCols = colsOfSquare;    
		totalRows = rowsOfSquare;

		System.out.println("cols "+totalCols+ " rows "+totalRows);

		/**
		 *  Regular square image chunks crop and output.
		 */
		BufferedImage imgs[] = new BufferedImage[squaresCount];
		int count = 0;
		System.out.println("Splitting image....");

		for (int x = 0; x < colsOfSquare; x++) {
			for (int y = 0; y < rowsOfSquare; y++) {
				//Initialize the image array with image chunks
				imgs[count] = new BufferedImage(squareLength, squareLength, originalImg.getType());

				// draws the image chunk
				Graphics2D gr = imgs[count].createGraphics();
				gr.drawImage(originalImg, 0, 0, squareLength, squareLength, squareLength * x, squareLength * y, squareLength * x + squareLength, squareLength * y + squareLength, null);
				gr.dispose();

				// output chunk
				writeToFile(imgs[count], x, y);
				count++;
			}
		}


		/**
		 *  Remainder chunks crop and output.
		 */
		// Right end column width remainder image chunks crop and output.
		/*
        if (hasWidthRemainder) {
            BufferedImage rightRemainderChunks[] = new BufferedImage[rowsOfSquare];
            for (int i = 0; i < rowsOfSquare; i++) {
                rightRemainderChunks[i] = new BufferedImage(widthOutOfSquaresRemainder, squareLength, originalImg.getType());

                Graphics2D gr = rightRemainderChunks[i].createGraphics();
                gr.drawImage(originalImg, 0, 0, widthOutOfSquaresRemainder, squareLength, squareLength * colsOfSquare, squareLength * i, squareLength * colsOfSquare + widthOutOfSquaresRemainder, squareLength * i + squareLength, null);
                gr.dispose();

                writeToFile(rightRemainderChunks[i], totalCols - 1, i);
            }
        }
		 */
		/*
        //  Bottom row height remainder image chunks crop and output.
        if (hasHeightRemainder) {
            BufferedImage bottomRemainderChunks[] = new BufferedImage[colsOfSquare];
            for (int i = 0; i < colsOfSquare; i++) {
                bottomRemainderChunks[i] = new BufferedImage(squareLength, heightOutOfSquaresRemainder, originalImg.getType());

                Graphics2D gr = bottomRemainderChunks[i].createGraphics();
                gr.drawImage(originalImg, 0, 0, squareLength, heightOutOfSquaresRemainder, squareLength * i, squareLength * rowsOfSquare, squareLength * i + squareLength, squareLength * rowsOfSquare + heightOutOfSquaresRemainder, null);
                gr.dispose();

                writeToFile(bottomRemainderChunks[i], i, totalRows - 1);
            }
        }

        //  Bottom right corner remainder image chunk crops and outputs.
        if (hasWidthRemainder && hasHeightRemainder) {
            BufferedImage cornerRemainderImage = new BufferedImage(widthOutOfSquaresRemainder, heightOutOfSquaresRemainder, originalImg.getType());

            Graphics2D gr = cornerRemainderImage.createGraphics();
            gr.drawImage(originalImg, 0, 0, widthOutOfSquaresRemainder, heightOutOfSquaresRemainder, squareLength * colsOfSquare, squareLength * rowsOfSquare, originalWidth, originalHeight, null);
            gr.dispose();

            writeToFile(cornerRemainderImage, totalCols - 1, totalRows - 1);
        }
		 */
	}

	private static void writeToFile(BufferedImage image, int colNum, int rowNum) throws IOException {
		File file = new File("split/img_" + colNum + "_" + (totalCols - 1) + "_" + rowNum + "_" + (totalRows - 1) + ".jpg");
		ImageIO.write(image, "jpg", file);
	}

	public static void join() throws IOException{
		int totalWidth = 0;
		int totalHeight = 0;
		int cols;   // 0 base
		int rows;   // 0 base
		int type;

		/**
		 *  Load images from output folder.
		 */
		File folder = new File("split");
		File[] listOfFiles = folder.listFiles();

		if (listOfFiles.length == 0) {
			throw new IOException("No files are found in the output folder.");
		}

		/**
		 *  Get data from first file.
		 */
		String[] firstFileName = listOfFiles[0].getName().split("_");
		cols = Integer.parseInt(firstFileName[2]);
		rows = Integer.parseInt(firstFileName[4].split("\\.")[0]); // remove .bmp
		BufferedImage firstImage = ImageIO.read(listOfFiles[0]);
		type = firstImage.getType();

		/**
		 *  Set up 2d array holding image chunks.
		 */

		System.out.println("Rejoining image....");
		BufferedImage[][] imageChunks = new BufferedImage[cols + 1][rows + 1];
		for (File file : listOfFiles) {
			if (file.isFile()) {
				String[] name = file.getName().split("_");
				//System.out.println("NAME1 "+name[1]+ " NAME3 "+name[3]);
				int colNum = Integer.parseInt(name[1]);
				int rowNum = Integer.parseInt(name[3]);


				BufferedImage image = ImageIO.read(file);

				if (colNum == 0) {
					totalHeight += image.getHeight();
				}
				if (rowNum == 0) {
					totalWidth += image.getWidth();
				}

				imageChunks[colNum][rowNum] = image;
			}
		}

		writeKey(imageChunks, totalWidth, totalHeight, type, cols, rows);

		/*SHUFFLING BLOCCHI*/

		Keys.initializeCoords();

		Random rand = new Random();

		for(int i=0; i<imageChunks.length; i++){
			for(int j=0; j<imageChunks[i].length; j++){
				CoordsImageBlock coordsReal = new CoordsImageBlock(i, j);
				Keys.addCoords(coordsReal,imageChunks[i][j]);        //Salvataggio blocchi originali
			}
		}


		for(int i=0; i<imageChunks.length; i++){
			for(int j=0; j<imageChunks[i].length; j++){
				
				int i1 = rand.nextInt(imageChunks.length);
				int j1 = rand.nextInt(imageChunks[i].length);
		
				BufferedImage tmp = imageChunks[i][j];
				
				imageChunks[i][j] = imageChunks[i1][j1];
				imageChunks[i1][j1] = tmp;

			}

		}

		/**
		 *  Assign image chunks from 2d array to original one image.
		 */

		System.out.println("TOTAL WIDTH "+totalWidth + " totalHeight "+totalHeight);

		BufferedImage combineImage = new BufferedImage(totalWidth, totalHeight, type);
		int stackWidth = 0;
		int stackHeight = 0;

		Keys.height = rows;
		Keys.width = cols;
		Keys.totalheight = totalHeight;
		Keys.totalwidth = totalWidth;
		Keys.type = type;
		Keys.initializeRGBInverse(rows+1,cols+1);
		Keys.initializeInvRotInv(rows+1, cols+1);

		//Processing each block
		for (int i = 0; i <= cols; i++) {
			for (int j = 0; j <= rows; j++) {
				Keys.inversionMode = new Random().nextInt(4);
				Keys.angleRotation = new Random().nextInt(4);
				Keys.negativePositiveTranform = new Random().nextInt(2);

				//Graphics2D g = imageChunks[i][j].createGraphics();
				int w = imageChunks[i][j].getWidth();  
				int h = imageChunks[i][j].getHeight();



				KeysInvRotationInversion key = new KeysInvRotationInversion(i, j);


				//Rotation of each block
				switch(Keys.angleRotation){
				case 0:{
					System.out.println("0");
					key.setRotation(0);
					//No rotation
					break;
				}
				case 1:{
					System.out.println("90");
					key.setRotation(90);

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
					g2d.drawImage(imageChunks[i][j], 0, 0, null);
					imageChunks[i][j] = rotated;

					break;
				}
				case 2:{
					System.out.println("180");
					key.setRotation(180);

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
					g2d.drawImage(imageChunks[i][j], 0, 0, null);
					imageChunks[i][j] = rotated;

					break;
				}
				case 3:{
					System.out.println("270");
					key.setRotation(270);

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
					g2d.drawImage(imageChunks[i][j], 0, 0, null);
					imageChunks[i][j] = rotated;

					break;
				}
				}


				//Inversion of each block
				switch(Keys.inversionMode){
				case 0:{
					//No inversion
					System.out.println("no inversion");
					key.setInversion(0);
					break;
				}
				case 1:{
					//Block inversion horizontally 
					System.out.println("horizont");

					BufferedImage dimg = new BufferedImage(w, h, imageChunks[i][j].getType());  
					Graphics2D g = dimg.createGraphics();  
					g.drawImage(imageChunks[i][j], 0, 0, w, h, w, 0, 0, h, null); 
					g.dispose();

					imageChunks[i][j] = dimg;

					key.setInversion(1);
					break;
				}
				case 2:{
					//Block inversion vertically 
					System.out.println("vertical");

					BufferedImage dimg = new BufferedImage(w, h, imageChunks[i][j].getType());  
					Graphics2D g = dimg.createGraphics();  
					g.drawImage(imageChunks[i][j],0, 0, w, h, 0, h, w, 0, null);  
					g.dispose();

					imageChunks[i][j] = dimg;
					key.setInversion(2);
					break;
				}
				case 3:{
					//Block inversion horizontally & vertically
					System.out.println("horizont & vertical");

					BufferedImage dimg = new BufferedImage(w, h, imageChunks[i][j].getType());  
					Graphics2D g = dimg.createGraphics();  
					g.drawImage(imageChunks[i][j], 0, 0, w, h, w, 0, 0, h, null);  //Horizontally
					g.dispose();

					imageChunks[i][j] = dimg;

					BufferedImage dimg2 = new BufferedImage(w, h, imageChunks[i][j].getType());  
					Graphics2D g2 = dimg2.createGraphics();  
					g2.drawImage(imageChunks[i][j], 0, 0, w, h, 0, h, w, 0, null); 		//Vertically
					g2.dispose();

					imageChunks[i][j] = dimg2;

					key.setInversion(3);
					break;
				}
				}

				Keys.addInverseRotate(j, i, key);



				//Negative trasformation for each pixel of block
				if(Keys.negativePositiveTranform == 1){
					System.out.println("negative");

					RGBInverseTrasformation rgb = new RGBInverseTrasformation(i,j);
					Keys.addRGBInverse(j, i, rgb);


					for(int y = 0; y < h; y++){
						for(int x = 0; x < w; x++){
							int p = imageChunks[i][j].getRGB(x, y);	
							int a = (p>>24)&0xff;
							int r = (p>>16)&0xff;
							int gr = (p>>8)&0xff;
							int b = p&0xff;

							//subtract RGB from 255
							r = 255 - r;
							gr = 255 - gr;
							b = 255 - b;
							//set new RGB value
							p = (a<<24) | (r<<16) | (gr<<8) | b;

							imageChunks[i][j].setRGB(x, y, p);
						}
					}
				}else{//Else positive trasformation is performed
					System.out.println("positive");
				}



				/*Shuffling color components for each block*/

				Keys.randColor = new Random().nextInt(6);  
				for(int y = 0; y < h; y++){
					for(int x = 0; x < w; x++){
						int p = imageChunks[i][j].getRGB(x, y);	
						int a = (p>>24)&0xff;

						int r=0,gr=0,b=0;
						r = (p>>16)&0xff;
						gr = (p>>8)&0xff;
						b = p&0xff;

						switch(Keys.randColor){
						case 0:{
							p = (a<<24) | (r<<16) | (gr<<8) | b;
							break;
						}
						case 1:{
							p = (a<<24) | (gr<<8) |(r<<16) | b;
							break;
						}

						case 2:{
							p = (a<<24) | b |(r<<16) |(gr<<8);
							break;
						}

						case 3:{
							p = (a<<24) | b | (gr<<8) |(r<<16);
							break;
						}
						case 4:{
							p = (a<<24) | (r<<16) | b | (gr<<8);
							break;

						}
						case 5:{
							p = (a<<24) | (gr<<8) | b | (r<<16);
							break;
						}
						}

						imageChunks[i][j].setRGB(x, y, p);


					}
				}



				combineImage.createGraphics().drawImage(imageChunks[i][j], stackWidth, stackHeight, null);
				stackHeight += imageChunks[i][j].getHeight();
			}

			stackWidth += imageChunks[i][0].getWidth();
			stackHeight = 0;
		}

		Keys.imageKey = imageChunks;

		ImageIO.write(combineImage, "jpg", new File("img/join.jpg"));
		System.out.println("Image rejoin done.");

		FileUtils.forceDelete(new File("split")); 

	}
	
	public static void join(int index) throws IOException{
		int totalWidth = 0;
		int totalHeight = 0;
		int cols;   // 0 base
		int rows;   // 0 base
		int type;

		/**
		 *  Load images from output folder.
		 */
		File folder = new File("split");
		File[] listOfFiles = folder.listFiles();

		if (listOfFiles.length == 0) {
			throw new IOException("No files are found in the output folder.");
		}

		/**
		 *  Get data from first file.
		 */
		String[] firstFileName = listOfFiles[0].getName().split("_");
		cols = Integer.parseInt(firstFileName[2]);
		rows = Integer.parseInt(firstFileName[4].split("\\.")[0]); // remove .bmp
		BufferedImage firstImage = ImageIO.read(listOfFiles[0]);
		type = firstImage.getType();

		/**
		 *  Set up 2d array holding image chunks.
		 */

		System.out.println("Rejoining image....");
		BufferedImage[][] imageChunks = new BufferedImage[cols + 1][rows + 1];
		for (File file : listOfFiles) {
			if (file.isFile()) {
				String[] name = file.getName().split("_");
				//System.out.println("NAME1 "+name[1]+ " NAME3 "+name[3]);
				int colNum = Integer.parseInt(name[1]);
				int rowNum = Integer.parseInt(name[3]);


				BufferedImage image = ImageIO.read(file);

				if (colNum == 0) {
					totalHeight += image.getHeight();
				}
				if (rowNum == 0) {
					totalWidth += image.getWidth();
				}

				imageChunks[colNum][rowNum] = image;
			}
		}

		writeKey(imageChunks, totalWidth, totalHeight, type, cols, rows,index);

		/*SHUFFLING BLOCCHI*/

		Keys.initializeCoords();

		Random rand = new Random();

		for(int i=0; i<imageChunks.length; i++){
			for(int j=0; j<imageChunks[i].length; j++){
				CoordsImageBlock coordsReal = new CoordsImageBlock(i, j);
				Keys.addCoords(coordsReal,imageChunks[i][j]);        //Salvataggio blocchi originali
			}
		}


		for(int i=0; i<imageChunks.length; i++){
			for(int j=0; j<imageChunks[i].length; j++){
				
				int i1 = rand.nextInt(imageChunks.length);
				int j1 = rand.nextInt(imageChunks[i].length);
		
				BufferedImage tmp = imageChunks[i][j];
				
				imageChunks[i][j] = imageChunks[i1][j1];
				imageChunks[i1][j1] = tmp;

			}

		}

		/**
		 *  Assign image chunks from 2d array to original one image.
		 */

		System.out.println("TOTAL WIDTH "+totalWidth + " totalHeight "+totalHeight);

		BufferedImage combineImage = new BufferedImage(totalWidth, totalHeight, type);
		int stackWidth = 0;
		int stackHeight = 0;

		Keys.height = rows;
		Keys.width = cols;
		Keys.totalheight = totalHeight;
		Keys.totalwidth = totalWidth;
		Keys.type = type;
		Keys.initializeRGBInverse(rows+1,cols+1);
		Keys.initializeInvRotInv(rows+1, cols+1);

		//Processing each block
		for (int i = 0; i <= cols; i++) {
			for (int j = 0; j <= rows; j++) {
				Keys.inversionMode = new Random().nextInt(4);
				Keys.angleRotation = new Random().nextInt(4);
				Keys.negativePositiveTranform = new Random().nextInt(2);

				//Graphics2D g = imageChunks[i][j].createGraphics();
				int w = imageChunks[i][j].getWidth();  
				int h = imageChunks[i][j].getHeight();



				KeysInvRotationInversion key = new KeysInvRotationInversion(i, j);


				//Rotation of each block
				switch(Keys.angleRotation){
				case 0:{
					System.out.println("0");
					key.setRotation(0);
					//No rotation
					break;
				}
				case 1:{
					System.out.println("90");
					key.setRotation(90);

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
					g2d.drawImage(imageChunks[i][j], 0, 0, null);
					imageChunks[i][j] = rotated;

					break;
				}
				case 2:{
					System.out.println("180");
					key.setRotation(180);

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
					g2d.drawImage(imageChunks[i][j], 0, 0, null);
					imageChunks[i][j] = rotated;

					break;
				}
				case 3:{
					System.out.println("270");
					key.setRotation(270);

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
					g2d.drawImage(imageChunks[i][j], 0, 0, null);
					imageChunks[i][j] = rotated;

					break;
				}
				}


				//Inversion of each block
				switch(Keys.inversionMode){
				case 0:{
					//No inversion
					System.out.println("no inversion");
					key.setInversion(0);
					break;
				}
				case 1:{
					//Block inversion horizontally 
					System.out.println("horizont");

					BufferedImage dimg = new BufferedImage(w, h, imageChunks[i][j].getType());  
					Graphics2D g = dimg.createGraphics();  
					g.drawImage(imageChunks[i][j], 0, 0, w, h, w, 0, 0, h, null); 
					g.dispose();

					imageChunks[i][j] = dimg;

					key.setInversion(1);
					break;
				}
				case 2:{
					//Block inversion vertically 
					System.out.println("vertical");

					BufferedImage dimg = new BufferedImage(w, h, imageChunks[i][j].getType());  
					Graphics2D g = dimg.createGraphics();  
					g.drawImage(imageChunks[i][j],0, 0, w, h, 0, h, w, 0, null);  
					g.dispose();

					imageChunks[i][j] = dimg;
					key.setInversion(2);
					break;
				}
				case 3:{
					//Block inversion horizontally & vertically
					System.out.println("horizont & vertical");

					BufferedImage dimg = new BufferedImage(w, h, imageChunks[i][j].getType());  
					Graphics2D g = dimg.createGraphics();  
					g.drawImage(imageChunks[i][j], 0, 0, w, h, w, 0, 0, h, null);  //Horizontally
					g.dispose();

					imageChunks[i][j] = dimg;

					BufferedImage dimg2 = new BufferedImage(w, h, imageChunks[i][j].getType());  
					Graphics2D g2 = dimg2.createGraphics();  
					g2.drawImage(imageChunks[i][j], 0, 0, w, h, 0, h, w, 0, null); 		//Vertically
					g2.dispose();

					imageChunks[i][j] = dimg2;

					key.setInversion(3);
					break;
				}
				}

				Keys.addInverseRotate(j, i, key);



				//Negative trasformation for each pixel of block
				if(Keys.negativePositiveTranform == 1){
					System.out.println("negative");

					RGBInverseTrasformation rgb = new RGBInverseTrasformation(i,j);
					Keys.addRGBInverse(j, i, rgb);


					for(int y = 0; y < h; y++){
						for(int x = 0; x < w; x++){
							int p = imageChunks[i][j].getRGB(x, y);	
							int a = (p>>24)&0xff;
							int r = (p>>16)&0xff;
							int gr = (p>>8)&0xff;
							int b = p&0xff;

							//subtract RGB from 255
							r = 255 - r;
							gr = 255 - gr;
							b = 255 - b;
							//set new RGB value
							p = (a<<24) | (r<<16) | (gr<<8) | b;

							imageChunks[i][j].setRGB(x, y, p);
						}
					}
				}else{//Else positive trasformation is performed
					System.out.println("positive");
				}



				/*Shuffling color components for each block*/

				Keys.randColor = new Random().nextInt(6);  
				for(int y = 0; y < h; y++){
					for(int x = 0; x < w; x++){
						int p = imageChunks[i][j].getRGB(x, y);	
						int a = (p>>24)&0xff;

						int r=0,gr=0,b=0;
						r = (p>>16)&0xff;
						gr = (p>>8)&0xff;
						b = p&0xff;

						switch(Keys.randColor){
						case 0:{
							p = (a<<24) | (r<<16) | (gr<<8) | b;
							break;
						}
						case 1:{
							p = (a<<24) | (gr<<8) |(r<<16) | b;
							break;
						}

						case 2:{
							p = (a<<24) | b |(r<<16) |(gr<<8);
							break;
						}

						case 3:{
							p = (a<<24) | b | (gr<<8) |(r<<16);
							break;
						}
						case 4:{
							p = (a<<24) | (r<<16) | b | (gr<<8);
							break;

						}
						case 5:{
							p = (a<<24) | (gr<<8) | b | (r<<16);
							break;
						}
						}

						imageChunks[i][j].setRGB(x, y, p);


					}
				}



				combineImage.createGraphics().drawImage(imageChunks[i][j], stackWidth, stackHeight, null);
				stackHeight += imageChunks[i][j].getHeight();
			}

			stackWidth += imageChunks[i][0].getWidth();
			stackHeight = 0;
		}

		Keys.imageKey = imageChunks;

		ImageIO.write(combineImage, "jpg", new File("img/join.jpg"));
		System.out.println("Image rejoin done.");

		FileUtils.forceDelete(new File("split")); 

	}


	public static void writeKey(BufferedImage[][] img, int w, int h, int type, int cols, int rows) throws IOException {

		//Keys.imageKey = new BufferedImage[cols + 1][rows + 1];
		Keys.imageKey = img;
		Keys.width = w;
		Keys.height = h;
		Keys.cols = cols;
		Keys.rows = rows;

		System.out.println("Writeee "+Keys.width);

		/*
		BufferedImage combineImage = new BufferedImage(Keys.width, Keys.height, type);
		int stackWidth = 0;
		int stackHeight = 0;
		for (int i = 0; i <= Keys.cols; i++) {
			for (int j = 0; j <= Keys.rows; j++) {
				combineImage.createGraphics().drawImage(Keys.imageKey[i][j], stackWidth, stackHeight, null);
				stackHeight += Keys.imageKey[i][j].getHeight();
			}
			stackWidth += Keys.imageKey[i][0].getWidth();
			stackHeight = 0;
		}

		ImageIO.write(combineImage, "jpg", new File("Decrypt/KEY.jpg"));
		//writeImage(new File("Decrypt/KEY.jpg"), combineImage);
		System.out.println("Image REEEjoin done.");
		*/
	}
	
	public static void writeKey(BufferedImage[][] img, int w, int h, int type, int cols, int rows,int index) throws IOException {

		//Keys.imageKey = new BufferedImage[cols + 1][rows + 1];
		Keys.imageKey = img;
		Keys.width = w;
		Keys.height = h;
		Keys.cols = cols;
		Keys.rows = rows;

		System.out.println("Writeee "+Keys.width);

		/*
		BufferedImage combineImage = new BufferedImage(Keys.width, Keys.height, type);
		int stackWidth = 0;
		int stackHeight = 0;
		for (int i = 0; i <= Keys.cols; i++) {
			for (int j = 0; j <= Keys.rows; j++) {
				combineImage.createGraphics().drawImage(Keys.imageKey[i][j], stackWidth, stackHeight, null);
				stackHeight += Keys.imageKey[i][j].getHeight();
			}
			stackWidth += Keys.imageKey[i][0].getWidth();
			stackHeight = 0;
		}

		ImageIO.write(combineImage, "jpg", new File("Decrypt/KEY"+index+".jpg"));
		//writeImage(new File("Decrypt/KEY.jpg"), combineImage);
		System.out.println("Image REEEjoin done.");
		*/
	}
}
