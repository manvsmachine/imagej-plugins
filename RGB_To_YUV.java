import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.process.FloatProcessor;

public class RGB_To_YUV implements PlugInFilter
{
	public int setup(String arg, ImagePlus im)
	{
		return DOES_RGB;
	}
	
	public void run(ImageProcessor ip)
	{
		int width = ip.getWidth();
		int height = ip.getHeight();
		int[] pixels = (int[])ip.getPixels();
		int r, g, b;
		float[] y = new float[pixels.length];
		float[] u = new float[pixels.length];
		float[] v = new float[pixels.length];
		
		for(int i = 0; i < pixels.length; i++)
		{
			int pix = pixels[i];
			r = (pix & 0xff0000) >> 16;
			g = (pix & 0x00ff00) >> 8;
			b = pix & 0x0000ff;
		
			y[i] = (float)r * .299f + (float)g * .587f + (float)b * .114f;
            u[i] = (float)r * -.147f + (float)g * -.289f + (float)b * .436f;
            v[i] = (float)r * .615f + (float)g * -.515f + (float)b * -.100f;
        }
		
		FloatProcessor fp1 = new FloatProcessor(width, height, y, null);
		FloatProcessor fp2 = new FloatProcessor(width, height, u, null);
		FloatProcessor fp3 = new FloatProcessor(width, height, v, null);
		
		ImagePlus temp1 = new ImagePlus("Y", fp1);
		ImagePlus temp2 = new ImagePlus("U", fp2);
		ImagePlus temp3 = new ImagePlus("V", fp3);
		
		temp1.show();
		temp2.show();
		temp3.show();
	}
}