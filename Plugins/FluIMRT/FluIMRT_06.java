/*=====================================================================
| Version: Mai 26, 2012 DN06
\=====================================================================*/

	import ij.*;
	import ij.process.*;
	import ij.gui.*;
	import java.awt.*;
	import java.awt.event.*;
	import ij.plugin.*;
	
	/**
	This plugin demonstrates how to use a mask to tally the values
	and number of pixels within a non-rectangular selection.
	*/




	public class FluIMRT_06 implements PlugIn {

	private final double FLT_EPSILON = (double)Float.intBitsToFloat((int)0x33FFFFFF);

	/*....................................................................
		public methods
	....................................................................*/
	/*------------------------------------------------------------------*/
	public void getHorizontalGradient (
		ImageProcessor ip,
		double tolerance
	) {
		if (!(ip.getPixels() instanceof float[])) {
			throw new IllegalArgumentException("Float image required");
		}

		int width = ip.getWidth();
		int height = ip.getHeight();
		double line[] = new double[width];

		for (int y = 0; (y < height); y++) {
			getRow(ip, y, line);
			getSplineInterpolationCoefficients(line, tolerance);
			getGradient(line);
			putRow(ip, y, line);
		}
	} /* end getHorizontalGradient */

	/*------------------------------------------------------------------*/
	public void getVerticalGradient (
		ImageProcessor ip,
		double tolerance
	) {
		if (!(ip.getPixels() instanceof float[])) {
			throw new IllegalArgumentException("Float image required");
		}

		int width = ip.getWidth();
		int height = ip.getHeight();
		double line[] = new double[height];

		for (int x = 0; (x < width); x++) {
			getColumn(ip, x, line);
			getSplineInterpolationCoefficients(line, tolerance);
			getGradient(line);
			putColumn(ip, x, line);
		}
	} /* end getVerticalGradient */

	/*------------------------------------------------------------------*/
	private void getColumn (
		ImageProcessor ip,
		int x,
		double[] column
	) {
		int width = ip.getWidth();

		if (ip.getHeight() != column.length) {
			throw new IndexOutOfBoundsException("Incoherent array sizes");
		}
		if (ip.getPixels() instanceof float[]) {
			float[] floatPixels = (float[])ip.getPixels();
			for (int i = 0; (i < column.length); i++) {
				column[i] = (double)floatPixels[x];
				x += width;
			}
		}
		else {
			throw new IllegalArgumentException("Float image required");
		}
	} /* end getColumn */

	/*------------------------------------------------------------------*/
	private void getGradient (
		double[] c
	) {
		double h[] = {0.0, -1.0 / 2.0};
		double s[] = new double[c.length];

		antiSymmetricFirMirrorOnBounds(h, c, s);
		System.arraycopy(s, 0, c, 0, s.length);
	} /* end getGradient */

	/*------------------------------------------------------------------*/
	private double getInitialAntiCausalCoefficientMirrorOnBounds (
		double[] c,
		double z,
		double tolerance
	) {
		return((z * c[c.length - 2] + c[c.length - 1]) * z / (z * z - 1.0));
	} /* end getInitialAntiCausalCoefficientMirrorOnBounds */

	/*------------------------------------------------------------------*/
	double getInitialCausalCoefficientMirrorOnBounds (
		double[] c,
		double z,
		double tolerance
	) {
		double z1 = z, zn = Math.pow(z, c.length - 1);
		double sum = c[0] + zn * c[c.length - 1];
		int horizon = c.length;

		if (0.0 < tolerance) {
			horizon = 2 + (int)(Math.log(tolerance) / Math.log(Math.abs(z)));
			horizon = (horizon < c.length) ? (horizon) : (c.length);
		}
		zn = zn * zn;
		for (int n = 1; (n < (horizon - 1)); n++) {
			zn = zn / z;
			sum = sum + (z1 + zn) * c[n];
			z1 = z1 * z;
		}
		return(sum / (1.0 - Math.pow(z, 2 * c.length - 2)));
	} /* end getInitialCausalCoefficientMirrorOnBounds */

	/*------------------------------------------------------------------*/
	private void getRow (
		ImageProcessor ip,
		int y,
		double[] row
	) {
		int rowLength = ip.getWidth();

		if (rowLength != row.length) {
			throw new IndexOutOfBoundsException("Incoherent array sizes");
		}
		y *= rowLength;
		if (ip.getPixels() instanceof float[]) {
			float[] floatPixels = (float[])ip.getPixels();
			for (int i = 0; (i < rowLength); i++) {
				row[i] = (double)floatPixels[y++];
			}
		}
		else {
			throw new IllegalArgumentException("Float image required");
		}
	} /* end getRow */

	/*------------------------------------------------------------------*/
	private void getSplineInterpolationCoefficients (
		double[] c,
		double tolerance
	) {
		double z[] = {Math.sqrt(3.0) - 2.0};
		double lambda = 1.0;

		if (c.length == 1) {
			return;
		}
		for (int k = 0; (k < z.length); k++) {
			lambda = lambda * (1.0 - z[k]) * (1.0 - 1.0 / z[k]);
		}
		for (int n = 0; (n < c.length); n++) {
			c[n] = c[n] * lambda;
		}
		for (int k = 0; (k < z.length); k++) {
			c[0] = getInitialCausalCoefficientMirrorOnBounds(c, z[k], tolerance);
			for (int n = 1; (n < c.length); n++) {
				c[n] = c[n] + z[k] * c[n - 1];
			}
			c[c.length - 1] = getInitialAntiCausalCoefficientMirrorOnBounds(c, z[k],
				tolerance);
			for (int n = c.length - 2; (0 <= n); n--) {
				c[n] = z[k] * (c[n+1] - c[n]);
			}
		}
	} /* end getSplineInterpolationCoefficients */

	/*------------------------------------------------------------------*/
	private void putColumn (
		ImageProcessor ip,
		int x,
		double[] column
	) {
		int width = ip.getWidth();

		if (ip.getHeight() != column.length) {
			throw new IndexOutOfBoundsException("Incoherent array sizes");
		}
		if (ip.getPixels() instanceof float[]) {
			float[] floatPixels = (float[])ip.getPixels();
			for (int i = 0; (i < column.length); i++) {
				floatPixels[x] = (float)column[i];
				x += width;
			}
		}
		else {
			throw new IllegalArgumentException("Float image required");
		}
	} /* end putColumn */

	/*------------------------------------------------------------------*/
	private void putRow (
		ImageProcessor ip,
		int y,
		double[] row
	) {
		int rowLength = ip.getWidth();

		if (rowLength != row.length) {
			throw new IndexOutOfBoundsException("Incoherent array sizes");
		}
		y *= rowLength;
		if (ip.getPixels() instanceof float[]) {
			float[] floatPixels = (float[])ip.getPixels();
			for (int i = 0; (i < rowLength); i++) {
				floatPixels[y++] = (float)row[i];
			}
		}
		else {
			throw new IllegalArgumentException("Float image required");
		}
	} /* end putRow */

	/*....................................................................
		private methods
	....................................................................*/
	/*------------------------------------------------------------------*/
	private void antiSymmetricFirMirrorOnBounds (
		double[] h,
		double[] c,
		double[] s
	) {
		if (h.length != 2) {
			throw new IndexOutOfBoundsException(
				"The half-length filter size should be 2");
		}
		if (h[0] != 0.0) {
			throw new IllegalArgumentException(
				"Antisymmetry violation (should have h[0]=0.0)");
		}
		if (c.length != s.length) {
			throw new IndexOutOfBoundsException("Incompatible size");
		}
		if (2 <= c.length) {
			s[0] = 0.0;
			for (int i = 1; (i < (s.length - 1)); i++) {
				s[i] = h[1] * (c[i + 1] - c[i - 1]);
			}
			s[s.length - 1] = 0.0;
		}
		else {
			if (c.length == 1) {
				s[0] = 0.0;
			}
			else {
				throw new NegativeArraySizeException("Invalid length of data");
			}
		}
	} /* end antiSymmetricFirMirrorOnBounds */

	/*------------------------------------------------------------------*/
	private void getGradient () {


	} /* end getGradient */

	/*------------------------------------------------------------------*/
	/*------------------------------------------------------------------*/

		public void run(String arg) {
			ImagePlus imp = IJ.getImage();

			//recupere tous les valeurs pixels dans une matrice (32bit GRAY = Float)
			ImageProcessor ip = imp.getProcessor();
			float[] pixels = (float[]) ip.getPixels(); 

			//Dublication de l'image d'origine
			ImagePlus imp2 = new Duplicator().run(imp);
			imp2.show();
			ImageProcessor ip2 = imp2.getProcessor();
			float[] pixels2 = (float[]) ip2.getPixels(); 

			if (!(ip.getPixels() instanceof float[])) {
				throw new IllegalArgumentException("Float image required");
			}			
			
			//get width, height and the region of interest
			int w = imp.getWidth (); //Largeur - Ligne X
			int h = imp.getHeight (); //hauteur - Colonne Y
			int CentreWi = (w/2); // centre de l'image pixels sur Width
			int CentreHi = (h/2); // centre de l'image pixels sur Height
			int offsetCix = 150; //Offset centre le image offsetCiy
			int offsetCiy = 100; //Offset centre le image offsetCiy

			int width = ip.getWidth();
			int height = ip.getHeight();

			// ROI Zoom centre Image
			imp.setRoi(CentreWi-(offsetCix/2), CentreHi-(offsetCiy/2), offsetCix, offsetCiy);
			IJ.run("To Selection", "");
			imp2.setRoi(CentreWi-(offsetCix/2), CentreHi-(offsetCiy/2), offsetCix, offsetCiy);  
			IJ.run("To Selection", "");
			
			// Valeur Max /2 de ip2  = Seuil de detection
			double vSeuil = 0.3 * ip2.getMax(); 
			int vMax = (int)ip2.getMax(); // Valeur Max
			
			//GRADIENT_DIRECTION from Plugin Differential 
				ImageProcessor hgr = ip2.duplicate();
				ImageProcessor vgr = ip2.duplicate();
				float[] floatPixels = (float[])ip2.getPixels();
				float[] floatPixelsH = (float[])hgr.getPixels();
				float[] floatPixelsV = (float[])vgr.getPixels();

				getHorizontalGradient(hgr, FLT_EPSILON);
				getVerticalGradient(vgr, FLT_EPSILON);
				for (int y = 0, k = 0; (y < height); y++) {
					for (int x = 0; (x < width); x++, k++) {
						floatPixels[k] =
							(float)Math.atan2(floatPixelsH[k], floatPixelsV[k]);
					}
				}

				ip2.resetMinAndMax();
				imp2.updateAndDraw();
/*
			//Set ROI de 3x3 pour detection palteau Mean
			int gh_roi = 0;
			int gb_roi = 3;
			int dh_roi = 3;
			int db_roi = 3;

			for (int gh=0; gh<w; gh++) {

				for (int gb=3; gb<h; gb++) {
					gb_roi = gb_roi + 3;
					
					imp2.setRoi(gh_roi, gb_roi, dh_roi, db_roi);		
					Roi roi = imp2.getRoi();
					
					if (roi!=null && !roi.isArea()) roi = null;
					//ImageProcessor ip = imp.getProcessor();
					ImageProcessor mask = roi!=null?roi.getMask():null;
					Rectangle r = roi!=null?roi.getBounds():new Rectangle(0,0,ip.getWidth(),ip.getHeight());
					double sum = 0;
					int count = 0;
					for (int y=0; y<r.height; y++) {
						for (int x=0; x<r.width; x++) {
							if (mask==null||mask.getPixel(x,y)!=0) {
								count++;
								sum += ip.getPixelValue(x+r.x, y+r.y);
							}
						}
					}
				IJ.log("count: "+count);
				IJ.log("mean: "+IJ.d2s(sum/count,4));
				IJ.log("max: "+vMax);
				IJ.log(gh_roi+"======================================"+gb_roi);
				gh_roi = gh_roi + 3;
				}
			}*/
		}

	}
