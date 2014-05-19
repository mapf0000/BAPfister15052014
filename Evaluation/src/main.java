import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
 
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.IRStatisticsImpl;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.RatingSGDFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDPlusPlusFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.common.RandomUtils;
 


public class main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws TasteException 
	 */
	public static void main(String[] args) throws IOException, TasteException {
		
//		RandomUtils.useTestSeed()

		DataModel origModel = new FileDataModel(new File("datasets/weineDEClean.csv"));;
		DataModel genModel = new FileDataModel(new File("datasets/generated.csv"));;

		RMSRecommenderEvaluator RmsEvaluator = new RMSRecommenderEvaluator();
		RmsEvaluator.setMaxPreference(5);
		RmsEvaluator.setMinPreference(1);

		AverageAbsoluteDifferenceRecommenderEvaluator MaeEvaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		MaeEvaluator.setMaxPreference(5);
		MaeEvaluator.setMinPreference(1);
		
		ArrayList<String> userBasedResults = new ArrayList<String>();
		ArrayList<String> itemBasedResults = new ArrayList<String>();
		ArrayList<String> svdResults = new ArrayList<String>();
		ArrayList<String> svdPPResults = new ArrayList<String>();
		
		int nTests = 100; //Anzahl der Testdurchläufe
		double rmse = 0;
		double mae = 0;
		
		//Evaluation for RMSE and MAE
		//user-based
		for(int i=0; i<nTests; i++){
			double testRmse = RmsEvaluator.evaluate(userBasedRecommenderBuilder(50), null, origModel, 0.9, 1.0);
			double testMae = MaeEvaluator.evaluate(userBasedRecommenderBuilder(50), null, origModel, 0.9, 1.0);
			rmse += testRmse;
			mae += testMae;
		}
		userBasedResults.add("Original Datensatz (" + nTests + " Testdurchläufe) :");
		userBasedResults.add("RMSE: " + rmse/nTests);
		userBasedResults.add("MAE: " + mae/nTests);
		
		rmse = 0;
		mae = 0;
		for(int i=0; i<nTests; i++){
			double testRmse = RmsEvaluator.evaluate(userBasedRecommenderBuilder(11), null, genModel, 0.9, 1.0);
			double testMae = MaeEvaluator.evaluate(userBasedRecommenderBuilder(11), null, genModel, 0.9, 1.0);
			rmse += testRmse;
			mae += testMae;
		}
		userBasedResults.add("Generierter Datensatz (" + nTests + " Testdurchläufe) :");
		userBasedResults.add("RMSE: " + rmse/nTests);
		userBasedResults.add("MAE: " + mae/nTests);
		
		//item-based
		rmse = 0;
		mae = 0;
		for(int i=0; i<nTests; i++){
			double testRmse = RmsEvaluator.evaluate(itemBasedRecommenderBuilder(), null, origModel, 0.9, 1.0);
			double testMae = MaeEvaluator.evaluate(itemBasedRecommenderBuilder(), null, origModel, 0.9, 1.0);
			rmse += testRmse;
			mae += testMae;
		}
		itemBasedResults.add("Original Datensatz (" + nTests + " Testdurchläufe) :");
		itemBasedResults.add("RMSE: " + rmse/nTests);
		itemBasedResults.add("MAE: " + mae/nTests);
		
		rmse = 0;
		mae = 0;
		for(int i=0; i<nTests; i++){
			double testRmse = RmsEvaluator.evaluate(itemBasedRecommenderBuilder(), null, genModel, 0.9, 1.0);
			double testMae = MaeEvaluator.evaluate(itemBasedRecommenderBuilder(), null, genModel, 0.9, 1.0);
			rmse += testRmse;
			mae += testMae;
		}
		itemBasedResults.add("Generierter Datensatz (" + nTests + " Testdurchläufe) :");
		itemBasedResults.add("RMSE: " + rmse/nTests);
		itemBasedResults.add("MAE: " + mae/nTests);
		
		//svd
		rmse = 0;
		mae = 0;
		for(int i=0; i<nTests; i++){
			double testRmse = RmsEvaluator.evaluate(svdRecommenderBuilder(50, 15), null, origModel, 0.9, 1.0);
			double testMae = MaeEvaluator.evaluate(svdRecommenderBuilder(50, 15), null, origModel, 0.9, 1.0);
			rmse += testRmse;
			mae += testMae;
		}
		svdResults.add("Original Datensatz (" + nTests + " Testdurchläufe) :");
		svdResults.add("RMSE: " + rmse/nTests);
		svdResults.add("MAE: " + mae/nTests);
		
		rmse = 0;
		mae = 0;
		for(int i=0; i<nTests; i++){
			double testRmse = RmsEvaluator.evaluate(svdRecommenderBuilder(50, 5), null, genModel, 0.9, 1.0);
			double testMae = MaeEvaluator.evaluate(svdRecommenderBuilder(50, 5), null, genModel, 0.9, 1.0);
			rmse += testRmse;
			mae += testMae;
		}
		svdResults.add("Generierter Datensatz (" + nTests + " Testdurchläufe) :");
		svdResults.add("RMSE: " + rmse/nTests);
		svdResults.add("MAE: " + mae/nTests);
		
		//svd++
		rmse = 0;
		mae = 0;
		for(int i=0; i<nTests; i++){
			double testRmse = RmsEvaluator.evaluate(svdPPRecommenderBuilder(50, 17), null, origModel, 0.9, 1.0);
			double testMae = MaeEvaluator.evaluate(svdPPRecommenderBuilder(50, 17), null, origModel, 0.9, 1.0);
			rmse += testRmse;
			mae += testMae;
		}
		svdPPResults.add("Original Datensatz (" + nTests + " Testdurchläufe) :");
		svdPPResults.add("RMSE: " + rmse/nTests);
		svdPPResults.add("MAE: " + mae/nTests);
		
		rmse = 0;
		mae = 0;
		for(int i=0; i<nTests; i++){
			double testRmse = RmsEvaluator.evaluate(svdPPRecommenderBuilder(50, 9), null, genModel, 0.9, 1.0);
			double testMae = MaeEvaluator.evaluate(svdPPRecommenderBuilder(50, 9), null, genModel, 0.9, 1.0);
			rmse += testRmse;
			mae += testMae;
		}
		svdPPResults.add("Generierter Datensatz (" + nTests + " Testdurchläufe) :");
		svdPPResults.add("RMSE: " + rmse/nTests);
		svdPPResults.add("MAE: " + mae/nTests);
		
		
		//Evaluation for Coverage
		RandomUtils.useTestSeed();
		
		GenericRecommenderIRStatsEvaluator IREvaluator = new GenericRecommenderIRStatsEvaluator();
		
		//user-based
		IRStatistics IRStats = IREvaluator.evaluate(
				userBasedRecommenderBuilder(50), null, origModel, null, 5,
					GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);
		userBasedResults.add("Abdeckung (Original Datensatz): " + IRStats.getReach());
		
		IRStats = IREvaluator.evaluate(
				userBasedRecommenderBuilder(11), null, genModel, null, 5,
					GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);
		userBasedResults.add("Abdeckung (Generierter Datensatz): " + IRStats.getReach());
		
		//item-based
		IRStats = IREvaluator.evaluate(
				itemBasedRecommenderBuilder(), null, origModel, null, 5,
					GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);
		itemBasedResults.add("Abdeckung (Original Datensatz): " + IRStats.getReach());
		
		IRStats = IREvaluator.evaluate(
				itemBasedRecommenderBuilder(), null, genModel, null, 5,
					GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);
		itemBasedResults.add("Abdeckung (Generierter Datensatz): " + IRStats.getReach());
		
		//svd
		IRStats = IREvaluator.evaluate(
				svdRecommenderBuilder(50, 15), null, origModel, null, 5,
					GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);
		svdResults.add("Abdeckung (Original Datensatz): " + IRStats.getReach());
		
		IRStats = IREvaluator.evaluate(
				svdRecommenderBuilder(50, 5), null, genModel, null, 5,
					GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);
		svdResults.add("Abdeckung (Generierter Datensatz): " + IRStats.getReach());
		
		//svd++
		IRStats = IREvaluator.evaluate(
				svdRecommenderBuilder(50, 17), null, origModel, null, 5,
					GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);
		svdPPResults.add("Abdeckung (Original Datensatz): " + IRStats.getReach());
		
		IRStats = IREvaluator.evaluate(
				svdRecommenderBuilder(50, 9), null, genModel, null, 5,
					GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);
		svdPPResults.add("Abdeckung (Generierter Datensatz): " + IRStats.getReach());


		//print results
		System.out.println("User-based recommender: ");
		for (int i = 0; i < userBasedResults.size(); i++) {
			System.out.println(userBasedResults.get(i));
		}
		
		System.out.println("");
		System.out.println("Item-based recommender: ");
		for (int i = 0; i < itemBasedResults.size(); i++) {
			System.out.println(itemBasedResults.get(i));
		}
		
		System.out.println("");
		System.out.println("Svd recommender: ");
		for (int i = 0; i < svdResults.size(); i++) {
			System.out.println(svdResults.get(i));
		}
		
		System.out.println("");
		System.out.println("Svd++ recommender: ");
		for (int i = 0; i < svdPPResults.size(); i++) {
			System.out.println(svdPPResults.get(i));
		}
	}
	
	private static RecommenderBuilder svdPPRecommenderBuilder(final int features,
			final int iterations) { 
		return new RecommenderBuilder() {
			@Override
			public Recommender buildRecommender(DataModel model)
					throws TasteException {
				return new SVDRecommender(model, new SVDPlusPlusFactorizer(
						model, features, iterations));
			}
		};
	}

	private static RecommenderBuilder svdRecommenderBuilder(final int features,
			final int iterations) { 
		return new RecommenderBuilder() {
			@Override
			public Recommender buildRecommender(DataModel model)
					throws TasteException {
				return new SVDRecommender(model, new RatingSGDFactorizer(
						model, features, iterations));
			}
		};
	}
	
	private static RecommenderBuilder userBasedRecommenderBuilder(final int nNeighbours) { 
		return new RecommenderBuilder() {
			@Override
			public Recommender buildRecommender(DataModel model)
					throws TasteException {
				PearsonCorrelationSimilarity pearsSim;
				NearestNUserNeighborhood userNeigh;
				pearsSim = new PearsonCorrelationSimilarity(model);
				userNeigh = new NearestNUserNeighborhood(nNeighbours, pearsSim, model);
				return new GenericUserBasedRecommender(model, userNeigh, pearsSim);
			}
		};
	}
	
	private static RecommenderBuilder itemBasedRecommenderBuilder() {
		return new RecommenderBuilder() {
			@Override
			public Recommender buildRecommender(DataModel model)
					throws TasteException {
				UncenteredCosineSimilarity cosSim;
				cosSim = new UncenteredCosineSimilarity(model);
				return new GenericItemBasedRecommender(model, cosSim);
			}
		};
	}
}
