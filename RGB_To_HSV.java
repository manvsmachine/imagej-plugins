import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;

public class RGB_To_HSV implements PlugInFilter
{
    public int setup(String arg, ImagePlus im)
    {
        return DOES_RGB;
    }
    
    public void run(ImageProcessor ip)
    {
        int width = ip.getWidth(), height = ip.getHeight();
        int r, g, b, hi, lo; // RBG color components
        int[] pixels = (int[])ip.getPixels();
        float[] h = new float[pixels.length];
        float[] s = new float[pixels.length];
        float[] v = new float[pixels.length]; // arrays for hue / saturation / value 
        float hue = 0.0f, sat = 0.0f, val = 0.0f;
        float rNorm, gNorm, bNorm, rng;
        final float max = 255.0f;
        
        for(int i = 0; i < pixels.length; i++)
        {
            // determine color component values
            r = (pixels[i] & 0xff0000) >> 16;
            g = (pixels[i] & 0x00ff00) >> 8;
            b = pixels[i] & 0x0000ff;
            
            // find maximum component value and set
            // range, saturation, value accordingly
            hi = Math.max(r, Math.max(g, b));
            lo = Math.min(r, Math.max(g, b));
            rng = (float)(hi - lo);
            if(hi > 0)
                sat = rng / (float)hi;
            else
                sat = 0.0f;
            val = (float)hi / max;
            
            if(rng > 0)
            {
                rNorm = (float)(hi - r) / rng;
                gNorm = (float)(hi - g) / rng;
                bNorm = (float)(hi - b) / rng;
                
                // preliminary (NOT ACTUAL) value for H 
                if(hi == r)
                    hue = bNorm - gNorm;
                else if(hi == g)
                    hue = rNorm - bNorm + 2;
                else
                    hue = gNorm - rNorm + 4;
            
                // normalized value for H (if necessary)
                if(hue < 0)
                    hue += 6;
                
                    hue = hue / 6;
            }
            else
            {
                rNorm = 0.0f;
                gNorm = 0.0f;
                bNorm = 0.0f;
            }
            
            
            
            
            // insert HSV values into their respective arrays
            h[i] = hue;
            s[i] = sat;
            v[i] = val;
            
        }
        
        // set the new image array values 
        FloatProcessor fp1 = new FloatProcessor(width, height, h, null);
        FloatProcessor fp2 = new FloatProcessor(width, height, s, null);
        FloatProcessor fp3 = new FloatProcessor(width, height, v, null);
        
        // display the new images
        ImagePlus temp1 = new ImagePlus("H", fp1);
        ImagePlus temp2 = new ImagePlus("S", fp2);
        ImagePlus temp3 = new ImagePlus("V", fp3);
        temp1.show();
        temp2.show();
        temp3.show();
    }
}
