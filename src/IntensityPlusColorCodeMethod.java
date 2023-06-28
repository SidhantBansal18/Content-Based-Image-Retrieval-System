import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class IntensityPlusColorCodeMethod {

	private ArrayList<ImageResults> custArr;
	private Set<ImageResults> relevanceFeedbackIndices;

	public IntensityPlusColorCodeMethod(ArrayList<ImageResults> custArr, Set<ImageResults> relevanceFeedbackIndices) {

		this.custArr = custArr;
		this.relevanceFeedbackIndices = relevanceFeedbackIndices;
	}

	public ArrayList<ImageResults> calculateIntensityPlusColor() {
		// If no image is selected as relevant use the entire image set.
		List<ImageResults> imageResults = custArr;
		if (relevanceFeedbackIndices.size() != 0){
			imageResults = relevanceFeedbackIndices.stream().toList();
		}

		int len = custArr.get(0).getNormalizedFeatures().length;

		Double[] avg = new Double[len];
		Double[] sd = new Double[len];

		for (int index = 0; index < len; index++) {

			Double sum = 0.0;
			// Calculating average of each column of only the top images that are
			// true/relevant as checked by the user.
			for (ImageResults imageResult: imageResults) {
				sum += imageResult.getNormalizedFeatures()[index];
			}
			avg[index] = sum / (double) imageResults.size();
		}

		Double minSd = Double.MAX_VALUE;

		for (int index = 0; index < len; index++) {

			double sum = 0.0;
			// Calculating standard deviation of each column of only the top images that are
			// true/relevant as checked by the user.
			for  (ImageResults imageResult: imageResults) {
				sum += Math.pow((imageResult.getNormalizedFeatures()[index] - avg[index]), 2);
			}

			sd[index] = Math.sqrt(sum / (imageResults.size() - 1.0));
			if (sd[index] > 0 && sd[index] < minSd) {
				minSd = sd[index];
			}
		}

		for (int index = 0; index < sd.length; index++) {
			if (sd[index] == 0 && avg[index] != 0) {
				sd[index] = minSd / 2.0;
			}
		}

		// Updating weights of each column according to their standard deviations.
		Double[] updatedWeight = new Double[len];
		Double[] normalizedWeight = new Double[len];

		if (relevanceFeedbackIndices.size() != 0){
			double sum = 0.0;
			for (int index = 0; index < updatedWeight.length; index++) {
				if (sd[index] == 0) {
					updatedWeight[index] = 0.0;
				} else {
					updatedWeight[index] = 1 / sd[index];
				}
				sum += updatedWeight[index];
			}

			// Normalizing the updated weights.
			for (int index = 0; index < normalizedWeight.length; index++) {
				normalizedWeight[index] = updatedWeight[index] / sum;
			}
		}
		
		// Using the normalized weights to compute the weighted distance
		for (ImageResults result : custArr) {
			double distance = 0.0;
			for (int index2 = 0; index2 < len; index2++) {
				double currentWeight = relevanceFeedbackIndices.size() == 0 ? (1.0 / 89.0) : normalizedWeight[index2];
				distance += currentWeight * (Math.abs(custArr.get(0).getNormalizedFeatures()[index2]
						- result.getNormalizedFeatures()[index2]));
			}
			result.setDistance(distance);
		}

		// Sorting the list according to the weighted distances before sending it back
		// to the caller method.
		custArr.sort(Comparator.comparingDouble(ImageResults::getDistance));
		return custArr;
	}
}
