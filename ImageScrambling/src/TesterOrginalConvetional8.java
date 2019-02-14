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


public class TesterOrginalConvetional8 {

	public static void main(String[] args) throws Exception {
		
		int evaluate = 1;
		
		/*EVALUATION*/
		if(evaluate == 1) {									
			int quality = 100;
			int count = 0;
			
			
			for(int i=1; i<=20; i++){
				
				if(quality>100) {
					break;
				} 
				
				File input = null;
				if(i<10){
					input = new File("dataset/ucid0000"+i+".tif");
				}else{
					input = new File("dataset/ucid000"+i+".tif");
				}

				File out = EncryptionSteps.initializeEval(input,i);
				
				int blockSize = 8;         							//Block size
				
				new File("split").mkdir();
				FileUtils.cleanDirectory(new File("split")); 
				
				BlockScrambling.splitImage(out,blockSize);
				BlockScrambling.join(i);
			
			
				File key = new File("Decrypt/KEY"+i+".jpg");
				
				String[] arg1 = {key.getAbsolutePath(),"img/final"+i+".jpg","-subsamp","444","-q",String.valueOf(quality)};
				TJExample.main(arg1);								//Compression of key image

				String[] arg4 = {"img/final"+i+".jpg"};				//JBENCH Decompression of key image
				TJBench.main(arg4);									//JBENCH generate decompressed image (compressed_decompressed)			
				

				
				if(i==20) {
					i = 0;
					quality= quality+5;
				}
				
			}
			
		} else {
			
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
