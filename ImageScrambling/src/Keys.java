import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Keys {
	volatile static BufferedImage[][] imageKey;
	volatile static int width;
	volatile static int height;
	volatile static int totalwidth,totalheight;
	volatile static int type;
	volatile static int cols;
	volatile static int rows;
	volatile static int splitMode;
	volatile static int inversionMode;
	volatile static int angleRotation;
	volatile static int negativePositiveTranform;
	volatile static int randColor;
	
	volatile static RGB[][] listValueRGB;
	volatile static RGBInverseTrasformation[][] listValueRGBInverse;
	volatile static KeysInvRotationInversion[][] keysInvRotationInverse;
	volatile static HashMap<CoordsImageBlock, BufferedImage> listCoordsImage;
	volatile static YCbCr[][] listValueYCbCrRed;       //Lista dei valori YCbCr del rosso
	volatile static YCbCr[][] listValueYCbCrGreen; 	   //Lista dei valori YCbCr del verde
	volatile static YCbCr[][] listValueYCbCrBlue;	   //Lista dei valori YCbCr del blu
	
	static void initializeCoords(){
		listCoordsImage = new HashMap<>();
	}
	
	static void initializeRed(){
		listValueYCbCrRed = new YCbCr[height][width];
	}
	
	static void initializeGreen(){
		listValueYCbCrGreen = new YCbCr[height][width];
	}
	
	static void initializeBlue(){
		listValueYCbCrBlue = new YCbCr[height][width];
	}
	
	static void initializeRGB(){
		listValueRGB = new RGB[height][width];
	}
	
	static void initializeInvRotInv(int i,int j){
		keysInvRotationInverse = new KeysInvRotationInversion[i][j];
	}
	
	static void initializeRGBInverse(int i,int j){
		listValueRGBInverse = new RGBInverseTrasformation[i][j];
	}
	
	static void addCoords(CoordsImageBlock real ,BufferedImage newCoords){
		listCoordsImage.put(real, newCoords);
	}
	
	static void addInverseRotate(int j, int i, KeysInvRotationInversion key){
		keysInvRotationInverse[j][i] = key;
	}
	
	static void addRGBInverse(int j, int i,RGBInverseTrasformation value){
		listValueRGBInverse[j][i] = value;
	}
	
	static void addRGB(int j, int i,RGB value){
		listValueRGB[j][i] = value;
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
