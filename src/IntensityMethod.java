import java.util.*;

public class IntensityMethod {

	private ArrayList<ImageResults> custArr;

	public IntensityMethod(ArrayList<ImageResults> custArr) {
		this.custArr = custArr;
	}

	/**
	 * Calculating Manhattan distance of the first image from every other image and
	 * then sorting the array according to the distance
	 */
	public ArrayList<ImageResults> calculateIntensity() throws Exception {

		for (int j = 0; j < custArr.size(); j++) {

			double distance = 0.0;

			for (int i = 0; i < custArr.get(0).getIntensityHistogram().length; i++) {
				distance += Math.abs((custArr.get(0).getIntensityHistogram()[i]
						/ (custArr.get(0).getHeight() * custArr.get(0).getWidth()))
						- (custArr.get(j).getIntensityHistogram()[i]
								/ (custArr.get(j).getHeight() * custArr.get(j).getWidth())));

			}

			custArr.get(j).setDistance(distance);

		}

//		Arrays.sort(custArr);

		Collections.sort(custArr, (o1, o2) -> Double.compare(o1.getDistance(), o2.getDistance()));

		return custArr;

	}

}
