import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class Runner {

	public static void main(String[] args) throws IOException {

		File input = new File("img/ucid.jpg");
		
		File out = EncryptionSteps.initialize(input);
		File red = EncryptionSteps.extractRed(out);
		File green = EncryptionSteps.extractGreen(out);
		File blue = EncryptionSteps.extractBlue(out);
		
		File ycbr_red = EncryptionSteps.rgb2ybcr(red);
		File ycbr_green = EncryptionSteps.rgb2ybcr(green);
		File ycbc_blue = EncryptionSteps.rgb2ybcr(blue);
		
		File final_image = EncryptionSteps.concatenateImage(ycbr_red, ycbr_green, ycbc_blue);
		File output = EncryptionSteps.convertToGray(final_image);
		
		FileUtils.cleanDirectory(new File("split")); 
		
		new File("split").mkdir();
		new File("Decrypt").mkdir();
		int blockSize = 8;         							//Block size
		BlockScrambling.splitImage(output,blockSize);
		BlockScrambling.join();
		
		DecryptionSteps.separateImage();
		DecryptionSteps.Ycbr2Rgb(new File("Decrypt/redYCBR.jpg"));
		DecryptionSteps.Ycbr2Rgb(new File("Decrypt/greenYCBR.jpg"));
		DecryptionSteps.Ycbr2Rgb(new File("Decrypt/blueYCBR.jpg"));
		DecryptionSteps.mergeRGB(new File("Decrypt/red.jpg"), new File("Decrypt/green.jpg"), new File("Decrypt/blue.jpg"));
		
	}
}
