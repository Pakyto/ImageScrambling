import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Keys {
	volatile static BufferedImage[][] imageKey;
	volatile static int width;
	volatile static int height;
	volatile static int type;
	volatile static int cols;
	volatile static int rows;
	volatile static int splitMode;
	volatile static int inversionMode;
	volatile static int angleRotation;
	volatile static YCbCr[][] listValueYCbCrRed;       //Lista dei valori YCbCr del rosso
	volatile static YCbCr[][] listValueYCbCrGreen; 	   //Lista dei valori YCbCr del verde
	volatile static YCbCr[][] listValueYCbCrBlue;	   //Lista dei valori YCbCr del blu
	
	static void initializeRed(){
		listValueYCbCrRed = new YCbCr[height][width];
	}
	
	static void initializeGreen(){
		listValueYCbCrGreen = new YCbCr[height][width];
	}
	
	static void initializeBlue(){
		listValueYCbCrBlue = new YCbCr[height][width];
	}
	
	
	
	static void addRed(int j, int i,YCbCr value){
		listValueYCbCrRed[j][i] = value;
	}
	
	
	public static YCbCr[][] getListValueYCbCrRed() {
		return listValueYCbCrRed;
	}


	static void addBlue(int j, int i,YCbCr value){
		listValueYCbCrBlue[j][i] = value;
	}

	static void addGreen(int j, int i,YCbCr value){
		listValueYCbCrGreen[j][i] = value;
	}

}
