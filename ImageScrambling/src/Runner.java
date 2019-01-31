import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.io.FileUtils;

import com.idrsolutions.image.jpeg.JpegDecoder;
import com.idrsolutions.image.jpeg.JpegEncoder;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codecimpl.JPEGCodec;
import com.sun.media.jai.codecimpl.JPEGImageDecoder;
import com.sun.media.jai.codecimpl.util.Range;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class Runner {

	private static final String EXCEL_FILE_LOCATION = "MyFirstExcel.xls";

	public static void main(String[] args) throws Exception {
		
		int evaluate = 1;
		
		if(evaluate == 1) {
			int quality = 70;
			int count = 0;
			WritableWorkbook myFirstWbook = null;

			Files.deleteIfExists(Paths.get(EXCEL_FILE_LOCATION));

			myFirstWbook = Workbook.createWorkbook(new File(EXCEL_FILE_LOCATION));

			// create an Excel sheet
			WritableSheet excelSheet = myFirstWbook.createSheet("Sheet 1", 0);

			// add something into the Excel sheet
			Label label = new Label(0, 0, "Quality factor");       //Colonna 0 riga 0
			excelSheet.addCell(label);

			label = new Label(1, 0, "PSNR");      
			excelSheet.addCell(label);
			
			for(int i=1; i<=20; i++){
				
				if(quality>70) {
					break;
				} 
				
				File input = null;
				if(i<10){
					input = new File("dataset/ucid0000"+i+".tif");
				}else{
					input = new File("dataset/ucid000"+i+".tif");
				}

				File out = EncryptionSteps.initialize(input);

				String outFile = "img/"+out.getName();
				

				File red = EncryptionSteps.extractRed(out);
				File green = EncryptionSteps.extractGreen(out);
				File blue = EncryptionSteps.extractBlue(out);

				File ycbr_red = EncryptionSteps.rgb2ycbcr(red);
				File ycbr_green = EncryptionSteps.rgb2ycbcr(green);
				File ycbc_blue = EncryptionSteps.rgb2ycbcr(blue);

				File final_image = EncryptionSteps.concatenateImage(ycbr_red, ycbr_green, ycbc_blue);
				File output = EncryptionSteps.convertToGray(final_image);


				new File("split").mkdir();
				FileUtils.cleanDirectory(new File("split")); 

				new File("Decrypt").mkdir();
				int blockSize = 128;         							//Block size
				BlockScrambling.splitImage(output,blockSize);
				BlockScrambling.join();


				//int quality = new Random().nextInt((100-70)+1)+70;
				//quality+=count;
				System.out.println("quality "+quality);

				String[] arg = {outFile,"img/compressed.jpg","-subsamp","444","-q",String.valueOf(quality)};

				TJExample.main(arg);
				
				String[] arg2 = {"img/compressed.jpg"};				//JBENCH
				TJBench.main(arg2);									//JBENCH				
				
				/*DECOMPRESSIONE IMMAGINE CON LIBRERIA*/
				/*File f = new File("img/compressed.jpg");
				BufferedImage compressed = ImageIO.read(f);
				ByteArrayOutputStream b=new ByteArrayOutputStream();
				ImageIO.write(compressed, "jpg", b);
				byte[] byteImage = b.toByteArray();
				JpegDecoder jpeg = new JpegDecoder();
				BufferedImage decompressed = jpeg.read(byteImage);
				ImageIO.write(decompressed, "jpg", new File("Decrypt/decompressed.jpg"));*/
	
				
				DecryptionSteps.gray2YCbCr();
				DecryptionSteps.separateImage();
				DecryptionSteps.Ycbr2Rgb(new File("Decrypt/redYCBR.jpg"));
				DecryptionSteps.Ycbr2Rgb(new File("Decrypt/greenYCBR.jpg"));
				DecryptionSteps.Ycbr2Rgb(new File("Decrypt/blueYCBR.jpg"));
				DecryptionSteps.mergeRGB(new File("Decrypt/red.jpg"), new File("Decrypt/green.jpg"), new File("Decrypt/blue.jpg"));
				
				
				BufferedImage img = ImageIO.read(new File("img/compressed_full.jpg"));					//JBENCH
				//BufferedImage img = ImageIO.read(new File("Decrypt/decompressed.jpg")); 				//LIBRERIA JDELI
				BufferedImage img2 = ImageIO.read(new File("Decrypt/final.jpg"));					
				
				double psnr = Tools.printPSNR(img, img2);

				Number number = new Number(0, count, quality);       //Colonna 0 riga 1
				excelSheet.addCell(number);

				
				label = new Label(1, count, String.valueOf(psnr).replace('.', ','));
				excelSheet.addCell(label);
				count++;
				
				if(i==20) {
					i = 0;
					quality++;
				}
				
			}
			
			myFirstWbook.write();

			if (myFirstWbook != null) {
				try {
					myFirstWbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
			}
		} else {
			/*
			//Compression of image scrambled
			Image image = ImageIO.read(new File("img/join.jpg"));
			int quality = new Random().nextInt(101);
			System.out.println("quality "+quality);
			FileOutputStream dataOut = new FileOutputStream (new File("img/compressed.jpg"));
			JPEGEncoder encoder = new JPEGEncoder(image, quality, dataOut);
			encoder.Compress();
			dataOut.close();



			BufferedImage img = ImageIO.read(new File("img/join.jpg"));
			FileOutputStream dataOut = new FileOutputStream (new File("img/compressed.jpg"));
			JpegEncoder encr = new JpegEncoder();
			encr.setQuality(80);
			encr.write(img, dataOut);
			File f = new File("img/compressed.jpg");
			
			
			BufferedImage compressed = ImageIO.read(f);
			ByteArrayOutputStream b=new ByteArrayOutputStream();
			ImageIO.write(compressed, "jpg", b);
			byte[] byteImage = b.toByteArray();
			JpegDecoder jpeg = new JpegDecoder();
			BufferedImage decompressed = jpeg.read(byteImage);
			ImageIO.write(decompressed, "jpg", new File("Decrypt/decompressed.jpg"));
			 */
			
			File input = new File("dataset/ucid00002.tif");
			
			File out = EncryptionSteps.initialize(input);
			
			File red = EncryptionSteps.extractRed(out);
			File green = EncryptionSteps.extractGreen(out);
			File blue = EncryptionSteps.extractBlue(out);

			File ycbr_red = EncryptionSteps.rgb2ycbcr(red);
			File ycbr_green = EncryptionSteps.rgb2ycbcr(green);
			File ycbc_blue = EncryptionSteps.rgb2ycbcr(blue);

			File final_image = EncryptionSteps.concatenateImage(ycbr_red, ycbr_green, ycbc_blue);
			File output = EncryptionSteps.convertToGray(final_image);


			new File("split").mkdir();
			FileUtils.cleanDirectory(new File("split")); 

			new File("Decrypt").mkdir();
			int blockSize = 128;         							//Block size
			BlockScrambling.splitImage(output,blockSize);
			BlockScrambling.join();
			
			int quality = /*new Random().nextInt((100-70)+1)+70*/ 80;
			
			String[] arg = {"img/join.jpg","img/compressed.jpg","-subsamp","444","-q",String.valueOf(quality)};

			TJExample.main(arg);					/*COMPRESSION*/

			String[] arg2 = {"img/compressed.jpg"};
			TJBench.main(arg2);						/*DECOMPRESSION*/
			
			
			DecryptionSteps.gray2YCbCr();
			DecryptionSteps.separateImage();
			DecryptionSteps.Ycbr2Rgb(new File("Decrypt/redYCBR.jpg"));
			DecryptionSteps.Ycbr2Rgb(new File("Decrypt/greenYCBR.jpg"));
			DecryptionSteps.Ycbr2Rgb(new File("Decrypt/blueYCBR.jpg"));
			DecryptionSteps.mergeRGB(new File("Decrypt/red.jpg"), new File("Decrypt/green.jpg"), new File("Decrypt/blue.jpg"));
		}
	}
}
