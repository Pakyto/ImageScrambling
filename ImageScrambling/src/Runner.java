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

public class Runner {

	public static void main(String[] args) throws Exception {

		File input = new File("img/ucid.jpg");

		File out = EncryptionSteps.initialize(input);

		/*
		//Compression of image scrambled
		Image image = ImageIO.read(out);
		int defaultQuality = 1;   
		File imgCompressed = new File("img/compressed.jpeg");
		FileOutputStream dataOut = new FileOutputStream(imgCompressed);
		JpegEncoder encoder = new JpegEncoder(image, defaultQuality, dataOut);
		encoder.Compress();
		dataOut.close();

		File red = EncryptionSteps.extractRed(imgCompressed);
		File green = EncryptionSteps.extractGreen(imgCompressed);
		File blue = EncryptionSteps.extractBlue(imgCompressed)
		 */

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
		int blockSize = 16;         							//Block size
		BlockScrambling.splitImage(output,blockSize);
		BlockScrambling.join();

		int quality = new Random().nextInt(101);
		System.out.println("quality "+quality);
		
		String[] arg = {"img/ucid.jpg","img/test.jpg","-subsamp","420","-q",Integer.toString(quality)};

		TJExample.main(arg);

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

		DecryptionSteps.gray2YCbCr();
		DecryptionSteps.separateImage();
		DecryptionSteps.Ycbr2Rgb(new File("Decrypt/redYCBR.jpg"));
		DecryptionSteps.Ycbr2Rgb(new File("Decrypt/greenYCBR.jpg"));
		DecryptionSteps.Ycbr2Rgb(new File("Decrypt/blueYCBR.jpg"));
		DecryptionSteps.mergeRGB(new File("Decrypt/red.jpg"), new File("Decrypt/green.jpg"), new File("Decrypt/blue.jpg"));



	}
}
