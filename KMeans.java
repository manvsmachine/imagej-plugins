/* KMeans.java
 * 
 * performs image segmentation based on colorspace using the 
 * Lloyd / K-Means algorithm 
 * 
 * written by dwoodley 
 * 
 * */

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.process.ColorProcessor;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import java.util.Vector;

public class KMeans implements PlugInFilter
{
	boolean loopAgain, valueChanged; // used to end iteration of the algorithm
	int[] pixels; // stores original image pixel array
	int[] newPixels; // stores segmented image pixel array
	int[] clusters; // stores pixel / cluster association
	int[] meanValues; // stores each cluster's centroid value
	int iters, pixelCount; // number of iterations that took place
	
	public int setup(String arg, ImagePlus ip)
	{
		return DOES_RGB;
	}
	
	public void run(ImageProcessor orig)
	{
		// get original image data
		int height = orig.getHeight();
		int width = orig.getWidth();
		pixelCount = orig.getPixelCount();
		pixels = (int[])orig.getPixels();
		System.out.println("Pixel array created. Length = " + pixels.length);
		
		// get k (number of clusters) from user
		int numClusters = (int)IJ.getNumber("Number of clusters: ", 3);
		
		// create array to hold cluster mean values
		// pick initial values at random
		meanValues = new int[numClusters];
		System.out.println("Mean array created. Length = " + meanValues.length);
		
		for(int i = 0; i < numClusters; i++)
		{
			meanValues[i] = orig.get((int)(Math.random() * pixels.length));
		}
		
		// perform algorithm until values converge
		clusters = new int[pixelCount];
		
		loopAgain = true;
		while(loopAgain)
		{
			iterate();
			updateMeans();
			iters++;
			if(valueChanged)
				loopAgain = true;
			else
			{
				loopAgain = false;
				System.out.println("no change");
			}
		}
		
		// create new image from the mean values
		newPixels = new int[pixelCount];
		for(int i = 0; i < pixelCount; i++)
		{
			newPixels[i] = meanValues[clusters[i]];
		}
		
		ColorProcessor ip = new ColorProcessor(width, height, newPixels);
		ImagePlus im = new ImagePlus("Segmented Image", ip);
		im.show();
		System.out.println(iters);
		
		
	}
	
	// performs one iteration of the algorithm
	private void iterate()
	{
		valueChanged = false;
		int currentDistance, distance;
		for(int i = 0; i < pixelCount; i++)
		{
			currentDistance = getDistance(pixels[i], meanValues[clusters[i]]);
			for(int j = 0; j < meanValues.length; j++)
			{
				distance = getDistance(pixels[i], meanValues[j]);
				if((distance < currentDistance))
				{
					currentDistance = distance;
					clusters[i] = j;
					valueChanged = true;
				}
			}
		}
	}
		
	
	// computes and returns the Euclidean distance between two pixel values
	private int getDistance(int pixelVal, int meanVal)
	{
		
		// separate the pixel values into their components
		int pRed = (pixelVal & 0xff0000) >> 16;
		int pGreen = (pixelVal & 0x00ff00) >> 8;
		int pBlue = (pixelVal & 0x0000ff);
		
		int mRed = (meanVal & 0xff0000) >> 16;
		int mGreen = (meanVal & 0x00ff00) >> 8;
		int mBlue = (meanVal & 0x0000ff);
		
		int dist = (int)(Math.pow(pRed - mRed, 2) + Math.pow(pGreen - mGreen, 2) + Math.pow(pBlue - mBlue, 2));
		return dist;
	}
	
	private void updateMeans()
	{
		int count, r, g, b;
		
		for(int i = 0; i < meanValues.length; i++)
		{
			// average cluster values to find new centroid
			count = 0;
			r = 0;
			g = 0;
			b = 0;
			for(int j = 0; j < clusters.length; j++)
			{
				if(clusters[j] == i)
				{
					count++;
					
					r += (pixels[j] & 0xff0000) >> 16;
					g += (pixels[j] & 0x00ff00) >> 8;
					b += (pixels[j] & 0x0000ff);
				}
			}
			if(count != 0)
			{
				r /= count;
				g /= count;
				b /= count;
				meanValues[i] = ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
			}
			System.out.println(count + " pixels belong to cluster " + i);
			System.out.println("mean value: " + meanValues[i]);
		}
	}
}
