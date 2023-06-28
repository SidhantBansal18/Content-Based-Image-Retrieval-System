import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ColorCodeMethod {

	private ArrayList<ImageResults> custArr;

	public ColorCodeMethod(ArrayList<ImageResults> custArr) {
		this.custArr = custArr;
	}

	/**
	 * Calculating Manhattan distance of the first image from every other image and
	 * then sorting the array according to the distance
	 */
	public ArrayList<ImageResults> calculateColorCode() throws IOException {

		for (int j = 0; j < custArr.size(); j++) {

			double distance = 0.0;

			for (int i = 0; i < custArr.get(0).getColorCodeHistogram().length; i++) {
				distance += Math.abs((custArr.get(0).getColorCodeHistogram()[i]
						/ (custArr.get(0).getHeight() * custArr.get(0).getWidth()))
						- (custArr.get(j).getColorCodeHistogram()[i]
								/ (custArr.get(j).getHeight() * custArr.get(j).getWidth())));
			}

			custArr.get(j).setDistance(distance);

		}

//		Arrays.sort(custArr);

		Collections.sort(custArr, (o1, o2) -> Double.compare(o1.getDistance(), o2.getDistance()));

		return custArr;

	}
}
