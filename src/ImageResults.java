
/**
 * @author Sidhant and Sreja This class stores all the aspects of an image. The
 *         unique identifier of the image The distance of the image from the
 *         image selected for retrieval Height of the image Width of the image
 *         Intensity histogram of the image Color Coded histogram of the image
 */
public class ImageResults {

	private final int uniqueIdentifier;
	private final String name;
	private Double distance;
	private final Double height;
	private final Double width;
	private final Double size;
	private final int[] intensityHistogram;
	private final int[] colorCodeHistogram;
	private Double[] normalizedFeatures;
	private boolean selected;

	public ImageResults(int uniqueIdentifier, String name, Double distance, Double height, Double width, Double size,
			int[] intensityHistogram, int[] colorCodeHistogram) {
		this.uniqueIdentifier = uniqueIdentifier;
		this.name = name;
		this.distance = distance;
		this.height = height;
		this.width = width;
		this.intensityHistogram = intensityHistogram;
		this.colorCodeHistogram = colorCodeHistogram;
		this.size = size;
	}

	public int getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	public String getName() {
		return name;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Double getHeight() {
		return height;
	}

	public Double getWidth() {
		return width;
	}

	public Double getSize() {
		return size;
	}

	public int[] getIntensityHistogram() {
		return intensityHistogram;
	}

	public int[] getColorCodeHistogram() {
		return colorCodeHistogram;
	}

	public Double[] getNormalizedFeatures() {
		return normalizedFeatures;
	}

	public void setNormalizedFeatures(Double[] normalizedFeatures) {
		this.normalizedFeatures = normalizedFeatures;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
