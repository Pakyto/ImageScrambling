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


		/*EVALUATION*/

		int quality = 70;
		int count = 0;


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

			File out = EncryptionSteps.initializeEval(input,i);

			int blockSize = 8;         							//Block size

			new File("split").mkdir();
			FileUtils.cleanDirectory(new File("split")); 

			BlockScrambling.splitImage(out,blockSize);
			BlockScrambling.join(i);

			DecryptionSteps.inverseNegativePositveEval(i);

			File key = new File("Decrypt/finalInverse"+i+".jpg");

			String[] arg1 = {key.getAbsolutePath(),"img/final"+i+".jpg","-subsamp","444","-q",String.valueOf(quality)};
			TJExample.main(arg1);								//Compression of key image

			String[] arg4 = {"img/final"+i+".jpg"};				//JBENCH Decompression of key image
			TJBench.main(arg4);									//JBENCH generate decompressed image (compressed_decompressed)			



			if(i==20) {
				i = 0;
				quality= quality+5;
			}

		}
	}
}

