import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

public class BlockScrambling {

	private static int totalCols,totalRows;
	private static int rowsOfSquare,colsOfSquare;

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
				
		//writeKey(imageChunks, totalWidth, totalHeight, type, cols, rows);
		
		/*SHUFFLING BLOCCHI*/
		for(int i=0; i<imageChunks.length; i++){
			for(int j=0; j<imageChunks[i].length; j++){
				int i1 = (int) (Math.random()*imageChunks.length);
				int j1 = (int) (Math.random()*imageChunks[i].length);

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
		for (int i = 0; i <= cols; i++) {
			for (int j = 0; j <= rows; j++) {
				combineImage.createGraphics().drawImage(imageChunks[i][j], stackWidth, stackHeight, null);
				stackHeight += imageChunks[i][j].getHeight();
			}
			stackWidth += imageChunks[i][0].getWidth();
			stackHeight = 0;
		}

		ImageIO.write(combineImage, "jpg", new File("img/join.jpg"));
		System.out.println("Image rejoin done.");

		FileUtils.cleanDirectory(new File("split")); 
	}
	
	/*
	public static void writeKey(BufferedImage[][] img, int w, int h, int type, int cols, int rows) throws IOException {
		
		
		//Keys.imageKey = new BufferedImage[cols + 1][rows + 1];
		Keys.imageKey = img;
		Keys.width = w;
		Keys.height = h;
		Keys.cols = cols;
		Keys.rows = rows;

		System.out.println("Writeee "+Keys.width);

		
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
		System.out.println("Image REEEjoin done.");
	}*/
	
}
