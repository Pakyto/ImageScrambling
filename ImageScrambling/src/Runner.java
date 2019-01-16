import java.io.File;
import java.io.IOException;

public class Runner {

	public static void main(String[] args) throws IOException {

		File input = new File("img/ucid.tif");
		
		File out = EncryptionSteps.initialize(input);
		File red = EncryptionSteps.extractRed(out);
		File green = EncryptionSteps.extractGreen(out);
		File blue = EncryptionSteps.extractBlue(out);
		
		File ycbr_red = EncryptionSteps.rgb2ybcr(red);
		File ycbr_green = EncryptionSteps.rgb2ybcr(green);
		File ycbc_blue = EncryptionSteps.rgb2ybcr(blue);
		
		File final_image = EncryptionSteps.concatenateImage(ycbr_red, ycbr_green, ycbc_blue);
		File output = EncryptionSteps.convertToGray(final_image);
		
		new File("split").mkdir();
		new File("Decrypt").mkdir();
		int blockSize = 16;
		BlockScrambling.splitImage(output,blockSize);
		BlockScrambling.join();
		
		DecryptionSteps.separateImage();
	}
}
