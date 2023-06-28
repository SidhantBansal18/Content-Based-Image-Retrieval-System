import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

/**
 * @author Sidhant and Sreja This class initializes the application by
 *         displaying the front end, rendering images and buttons and also
 *         implements the methods to calculate the intensity and color coded
 *         histograms which are required for Manhattan Distance calculation
 */

public class UserInterface {
	private JFrame frame;
	private JPanel imageBrowser;
	private File[] allFiles;
	private JLabel[] imageGallery;
	private JPanel imageViewer;
	private JScrollPane displayImage;
	private JPanel buttons;
	private JPanel page;
	private JTabbedPane tabbedPane;
	private ArrayList<ImageResults> custArr; // ArrayList to store all the details of an image
	private JCheckBox relevanceCheckBox;
	private boolean relevantFlag = false; // Flag to display a checkbox under all images
	private final Set<ImageResults> relevanceFeedbackImageIndices = new HashSet<>();
	public UserInterface() throws IOException {

		// Creating the custom ArrayList with all the image details.
		initiateImageResults();

		// Method to initiate the main JFrame
		initiateFrame();

	}

	public void initiateFrame() {

		// Setting the properties of the frame
		frame = new JFrame("Image Query");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1500, 800);

		// First panel to browse images
		imageBrowser = new JPanel();
		imageBrowser.setPreferredSize(new Dimension(300, 400));
		tabbedPane = new JTabbedPane();

		// Second panel to view selected image
		imageViewer = new JPanel();
		imageViewer.setPreferredSize(new Dimension(550, 400));

		// Third panel with all the necessary buttons
		buttons = new JPanel();

		firstPanel();
		secondPanel(0);
		thirdPanel();

		frame.setVisible(true);

	}

	// Creating and initializing the custom ArrayList with all the required details
	// of
	// the image.
	public void initiateImageResults() throws IOException {
		File path = new File("src/images");

		int length = Objects.requireNonNull(path.listFiles()).length;

		allFiles = new File[length];

		for (int index = 1; index <= length; index++) {
			allFiles[index - 1] = new File("src/images/" + index + ".jpg");
		}

		imageGallery = new JLabel[allFiles.length];

		BufferedImage[] bufferedImages = new BufferedImage[allFiles.length];
		custArr = new ArrayList<>();

		for (int index = 0; index < allFiles.length; index++) {

			int[] intensityHistogram = new int[25];
			int[] colorCodeHistogram = new int[64];

			// Storing the Intensity and 6 bit code for each pixel in an image in array list.
			ArrayList<Double> intensityFeaturesOfOneImage = new ArrayList<>();
			ArrayList<Double> colorCodedFeaturesOfOneImage = new ArrayList<>();

			// Assigning unique identifier to each image so that it becomes easy to swap
			// images.

			String name = allFiles[index].getName();

			bufferedImages[index] = ImageIO.read(Objects.requireNonNull(UserInterface.class.getResource("/images/" + name)));

			Double height = (double) bufferedImages[index].getHeight();
			Double width = (double) bufferedImages[index].getWidth();
			Double sizeOfImage = height * width;

			for (int x = 0; x < bufferedImages[index].getWidth(); x++) {
				for (int y = 0; y < bufferedImages[index].getHeight(); y++) {

					// Using the Buffered image class to get the RGB values of a pixel
					int pixel = bufferedImages[index].getRGB(x, y);
					Color color = new Color(pixel);

					int red = color.getRed();
					int green = color.getGreen();
					int blue = color.getBlue();

					// Calculating intensity of a pixel and storing it in an array list.
					Double intensity = ((0.299) * (red)) + ((0.587) * (green)) + ((0.114) * (blue));
					intensityFeaturesOfOneImage.add(intensity);

					String redTemp = convertToBinary(red);
					String greenTemp = convertToBinary(green);
					String blueTemp = convertToBinary(blue);

					// Forming six bit code from 2 most significant bits of RGB values and storing
					// it in an ArrayList
					int resultantSixBit = Integer.parseInt(redTemp.substring(0, 2) + greenTemp.substring(0, 2) + blueTemp.substring(0, 2)
							// Forming six bit code from 2 most significant bits of RGB values and storing
							// it in an ArrayList
							, 2);
					colorCodedFeaturesOfOneImage.add((double) resultantSixBit);
				}
			}

			// Creating the intensity and color coded histogram of an image
			intensityHistogram = generateIntensityHistogram(intensityHistogram, intensityFeaturesOfOneImage);
			colorCodeHistogram = generateColorCodedHistogram(colorCodeHistogram, colorCodedFeaturesOfOneImage);

			// Storing all the properties of image. Initially the distance of each image is
			// stored as 0.0
			custArr.add(new ImageResults(index, name, 0.0, height, width, sizeOfImage, intensityHistogram,
					colorCodeHistogram));
		}

		// Calculating the normalized features of all the images and storing it in the
		// custom ArrayList for every image.
		calculateNormalizedFeatures();

	}

	/**
	 * @param histogram Histogram bucket with values in the range between 0-255
	 * @param list Intensity features of a single image stored in h * w sized list
	 * @return histogram generates intensity histogram array for an image
	 */
	public int[] generateIntensityHistogram(int[] histogram, ArrayList<Double> list) {
		for (Double d : list) {
			int index = (int) (d / 10);
			if (index > 24) {
				index = 24;
			}
			histogram[index]++;
		}
		return histogram;
	}

	/**
	 * @param histogram Histogram bucket to hold the colorCode output
	 * @param list Color Coded features of a single image stored in h * w sized list
	 * @return histogram generates color coded histogram array for an image
	 */
	public int[] generateColorCodedHistogram(int[] histogram, ArrayList<Double> list) {
		for (Double d : list) {
			histogram[d.intValue()]++;
		}
		return histogram;
	}

	// Converting RGB integer values to string and then to Binary for color coded
	// method calculations
	public String convertToBinary(int color) {

		StringBuilder result = new StringBuilder();
		for (int j = 7; j >= 0; j--) {
			int mask = 1 << j;
			result.append((color & mask) != 0 ? 1 : 0);
		}

		return result.toString();
	}

	// Calculating the normalized features of every image
	public void calculateNormalizedFeatures() {

		int intensitySize = custArr.get(0).getIntensityHistogram().length;
		int colorCodedSize = custArr.get(0).getColorCodeHistogram().length;
		int featuresSize = intensitySize + colorCodedSize;

		// Creating the initial features array of all the images
		double[][] features = new double[custArr.size()][featuresSize];

		double[] avg = new double[features[0].length];
		double[] sd = new double[features[0].length];

		for (int firstIndex = 0; firstIndex < features.length; firstIndex++) {

			int secondIndex = 0;
			int intensityIndex = 0;
			int colorCodedIndex = 0;

			while (secondIndex < features[0].length) {

				while (intensityIndex < intensitySize) {
					features[firstIndex][secondIndex] = custArr.get(firstIndex)
							.getIntensityHistogram()[intensityIndex++] / custArr.get(firstIndex).getSize();

					// Updating the average of each column while filling the features only
					avg[secondIndex] += features[firstIndex][secondIndex] / features.length;

					secondIndex++;
				}

				while (colorCodedIndex < colorCodedSize) {
					features[firstIndex][secondIndex] = custArr.get(firstIndex)
							.getColorCodeHistogram()[colorCodedIndex++] / custArr.get(firstIndex).getSize();

					// Updating the average of each column while filling the features only
					avg[secondIndex] += features[firstIndex][secondIndex] / features.length;

					secondIndex++;
				}

			}

		}

		// Keeping a track of minimum value of standard deviation, to be used when
		// standard deviation of a column is zero but corresponding average is not zero
		double minSd = Double.MAX_VALUE;

		for (int index = 0; index < features[0].length; index++) {

			double sum = 0.0;
			for (double[] feature : features) {
				sum += Math.pow((feature[index] - avg[index]), 2);
			}
			sd[index] = Math.sqrt(sum / ((features.length) - 1.0));
			if (sd[index] > 0 && sd[index] < minSd) {
				minSd = sd[index];
			}

		}

		for (int index = 0; index < sd.length; index++) {
			if (sd[index] == 0 && avg[index] != 0) {
				sd[index] = minSd / 2.0;
			}
		}

		// Final normalized features which will be used in all iterations of relevance
		// feedback.
		Double[] normalizedFeaturesToSet;

		for (int i = 0; i < features.length; i++) {

			normalizedFeaturesToSet = new Double[features[0].length];
			for (int j = 0; j < features[0].length; j++) {
				if (sd[j] == 0) {
					normalizedFeaturesToSet[j] = 0.0;
				} else {
					normalizedFeaturesToSet[j] = (features[i][j] - avg[j]) / sd[j];
				}
			}
			custArr.get(i).setNormalizedFeatures(normalizedFeaturesToSet);
		}

	}

	public void firstPanel() {

		int pageNumber = -1; // Keeping track of page numbers to dynamically output pages according to the
								// number of images

		int index = 0;

		while (index < allFiles.length) {

			// Each page can have at most 20 images
			if (index / 20 == pageNumber) {

				imageGallery[index] = new JLabel();
				imageGallery[index].setPreferredSize(new Dimension(170, 170));

				ImageIcon img = new ImageIcon(Objects.requireNonNull(UserInterface.class.getResource("/images/" + custArr.get(index).getName())));

				Image image = img.getImage();
				Image toSet = image.getScaledInstance(90, 90, 0);
				img = new ImageIcon(toSet);
				imageGallery[index].setIcon(img);
				int currIndex = index;
				JButton imageName = new JButton(custArr.get(index).getName());
				imageName.setBounds(5, 133, 80, 25);
				imageGallery[index].add(imageName);

				// Button listener for images that change the selected image in second panel if
				// clicked and not already set
				imageName.addActionListener(e -> {

					// Swapping the first index of the array with the clicked button index if they
					// are not equal and displaying it on second panel.
					if (currIndex != custArr.get(0).getUniqueIdentifier()) {
						secondPanel(currIndex);
						ImageResults temp = custArr.get(0);
						custArr.set(0, custArr.get(currIndex));
						custArr.set(currIndex, temp);

						tabbedPane.removeAll();
						firstPanel();

					}
				});

				// If relevance check box is selected then only a checkbox under each image
				// will show up
				if (relevantFlag) {
					JCheckBox imageCheckBox = new JCheckBox();
					imageCheckBox.setBounds(30, 159, 15, 15);
					imageCheckBox.setBackground(Color.LIGHT_GRAY);
					imageCheckBox.setSelected(custArr.get(currIndex).isSelected());

					imageGallery[index].add(imageCheckBox);

					// Changing the state of the image to selected/not selected for relevance
// depending on the previous state while keeping a count of selected images
					imageCheckBox.addActionListener(e -> {

						if (custArr.get(currIndex).isSelected()) {
							custArr.get(currIndex).setSelected(false);
							relevanceFeedbackImageIndices.remove(custArr.get(currIndex));
						}

						else if (!custArr.get(currIndex).isSelected()) {
							custArr.get(currIndex).setSelected(true);
							relevanceFeedbackImageIndices.add(custArr.get(currIndex));
						}
					});

				}
				page.add(imageGallery[index]);
				index++;

			} else {

				// If the page has reached a limit of 20 images then only a new page is
				// generated
				pageNumber++;
				page = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Rendering images on a page from left to right
				page.setBackground(Color.LIGHT_GRAY);
				page.setPreferredSize(new Dimension(900, 800));
				String pageNum = "Page " + (pageNumber + 1);
				tabbedPane.add(pageNum, page);
			}
		}

		imageBrowser.add(tabbedPane);

		// Adding the panel to the frame
		frame.getContentPane().add(imageBrowser, BorderLayout.CENTER);

//		for(int i = 0; i < custArr.length; i++) {
//			System.out.println(custArr[i].getName() + " " +custArr[i].isSelected());
//		}

	}

	public void secondPanel(int index) {

		// Clearing content because whenever this method would be called it would be
		// called with index of the image to be displayed
		imageViewer.removeAll();
		JLabel currPicture = new JLabel(new ImageIcon(Objects.requireNonNull(UserInterface.class.getResource("/images/" + custArr.get(index).getName()))));

		displayImage = new JScrollPane(currPicture);
		displayImage.setBorder(new EmptyBorder(20, 20, 20, 20));
		displayImage.setPreferredSize(new Dimension(400, 400));
		imageViewer.add(displayImage);

		// These are used to dynamically update the frame after a click
		imageViewer.repaint();
		imageViewer.validate();

		// Adding the panel to the frame
		frame.getContentPane().add(imageViewer, BorderLayout.EAST);
	}

	public void thirdPanel() {
		JButton intensity = new JButton("Retrieve by Intensity Method");
		JButton colorCode = new JButton("Retrieve by Color-Code Method");
		JButton intensityPlusColor = new JButton("Retrieve by Intensity + Color-Code Method");
		JButton reset = new JButton("Reset");
		JButton close = new JButton("Close");
		relevanceCheckBox = new JCheckBox("Relevance");
		relevanceCheckBox.setBounds(2, 155, 15, 15);
		buttons.add(intensity);
		buttons.add(colorCode);
		buttons.add(intensityPlusColor);
		buttons.add(reset);
		buttons.add(close);
		buttons.add(relevanceCheckBox);

		// Reset button that will clear the reset the complete UI once clicked
		reset.addActionListener(e -> {
			displayImage.removeAll();
			displayImage.repaint();
			displayImage.validate();

			tabbedPane.removeAll();
			custArr.sort(Comparator.comparingInt(ImageResults::getUniqueIdentifier));

			// Setting distances to 0 and unselecting the checkboxes of each image.
			for (ImageResults imageResults : custArr) {
				imageResults.setDistance(0.0);
				imageResults.setSelected(false);
			}
			firstPanel();
		});

		// Close button that will close the frame and terminate the application once
		// clicked
		close.addActionListener(e -> frame.dispose());

		// Button to retrieve images based on their intensity
		intensity.addActionListener(e -> {

			// Sending the array of all images with necessary features
			IntensityMethod im = new IntensityMethod(custArr);

			try {

				// Returning the updated array after sorting images according to their
				// intensities.
				custArr = im.calculateIntensity();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			// Dynamically updating the frame
			tabbedPane.removeAll();
			firstPanel();
		});

		// Button to retrieve images based on their color codes
		colorCode.addActionListener(e -> {

			// Sending the array of all images with necessary features
			ColorCodeMethod cm = new ColorCodeMethod(custArr);
			try {
				// Returning the updated array after sorting images according to their color
				// codes.
				custArr = cm.calculateColorCode();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Dynamically updating the frame
			tabbedPane.removeAll();
			firstPanel();
		});

		// Button to retrieve images based on their intensity and color code with
		// relevance feedback
		intensityPlusColor.addActionListener(e -> {
			IntensityPlusColorCodeMethod icc = new IntensityPlusColorCodeMethod(custArr, relevanceFeedbackImageIndices);

			custArr = icc.calculateIntensityPlusColor();

			tabbedPane.removeAll();
			firstPanel();

		});

		// If relevance check box is selected then a checkbox under each image will
		// show up
		relevanceCheckBox.addActionListener(e -> {

			if (relevanceCheckBox.isSelected()) {
				relevantFlag = true;
			}

			else if (!relevanceCheckBox.isSelected()) {
				relevantFlag = false;
			}

			tabbedPane.removeAll();
			firstPanel();
		});

		// Adding buttons to the frame
		frame.getContentPane().add(buttons, BorderLayout.SOUTH);
	}
}
