/* FluIRMT recherche les meilleures zones de mesure.
Version : 2.2
Ocin
*/
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;

public class FluIMRT_CHU implements PlugIn {

	public void run(String arg) {
		ImagePlus imp = IJ.getImage();
		
		//get width, height and the region of interest
		int w = imp.getWidth (); //Largeur - Ligne X
		int h = imp.getHeight (); //hauteur - Colonne Y
		int CentreWi = (w/2); // centre de l'image pixels sur Width
		int CentreHi = (h/2); // centre de l'image pixels sur Height
		int offsetCix = 150; //Offset centre le image offsetCiy
		int offsetCiy = 100; //Offset centre le image offsetCiy
		int wx = 0, hy = 0;

		//Dublication de l'image d'origine
		ImagePlus imp2 = new Duplicator().run(imp);
		imp2.show();
		
		imp.setRoi(CentreWi-(offsetCix/2), CentreHi-(offsetCiy/2), offsetCix, offsetCiy); // ROI Zoom centre Image
		IJ.run("To Selection", "");
		imp2.setRoi(CentreWi-(offsetCix/2), CentreHi-(offsetCiy/2), offsetCix, offsetCiy); // ROI Zoom centre Image  
		IJ.run("To Selection", "");

		// Filtre convolve + Surface Plot
		IJ.run(imp2, "Surface Plot...", "polygon=100 shade draw_axis smooth");
		IJ.run(imp2, "Convolve...", "text1=[-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 168 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n] normalize");
		IJ.run("Tile", "");

		ImageProcessor ip2 = imp2.getProcessor();
		float[] pixels = (float[]) ip2.getPixels(); //recupere tous les valeurs pixels dans une matrice (32bit GRAY = Float)
		
		double vSeuil = 0.3 * ip2.getMax(); // Valeur Max /2 de ip2  = Seuil de detection
		int vMax = (int)ip2.getMax(); // Valeur Max
		
		//*
		// DEBUG ===========================================================>
		String DEBUG1 = new String();
		DEBUG1 = DEBUG1.valueOf(vSeuil);
		
		String DEBUG2 = new String();
		DEBUG2 = DEBUG2.valueOf(vMax);
		
		IJ.showMessage("DEBUG1 : ","DEBUG1 : "+DEBUG1+"\nDEBUG2 : "+DEBUG2);	
		// DEBUG ===========================================================<		
		//*/

		//* Code Octave/java
		for (int ww = 1; ww < w; ww++){
			for (int hh = 1; hh < h; hh++){
				if (ip2.getPixelValue(ww,hh) >= vSeuil)
					ip2.set(ww, hh, 0);
					imp2.updateAndDraw();// update image visible
				}
		}
		//*/
		
		//ROI Manager
		RoiManager rm = RoiManager.getInstance();
		if (rm==null) rm = new RoiManager();
		rm.runCommand("reset");
		IJ.run(imp, "Find Maxima...", "noise=1 output=List light");

		// Rafraichir l'image
		imp2.show();
		imp2.updateAndDraw();
	}
}
