package sadl.detectors;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sadl.anomalydetecion.AnomalyDetection;
import sadl.constants.ProbabilityAggregationMethod;
import sadl.constants.ScalingMethod;
import sadl.detectors.featureCreators.MinimalFeatureCreator;
import sadl.detectors.featureCreators.SmallFeatureCreator;
import sadl.detectors.featureCreators.UberFeatureCreator;
import sadl.experiments.ExperimentResult;
import sadl.modellearner.PdttaLearner;
import sadl.oneclassclassifier.LibSvmClassifier;
import sadl.oneclassclassifier.ThresholdClassifier;

public class AggSublistsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		try{
			final PdttaLearner learner = new PdttaLearner(0.05, false);

			final MinimalFeatureCreator featureCreator = new MinimalFeatureCreator();
			final ThresholdClassifier classifier = new ThresholdClassifier(Math.exp(-5), Math.exp(-8));
			final VectorDetector detector = new VectorDetector(ProbabilityAggregationMethod.NORMALIZED_MULTIPLY, featureCreator, classifier, false);
			final VectorDetector detector2 = new VectorDetector(ProbabilityAggregationMethod.NORMALIZED_MULTIPLY, featureCreator, classifier, true);

			final SmallFeatureCreator featureCreator3 = new SmallFeatureCreator();
			final ThresholdClassifier classifier3 = new ThresholdClassifier(Math.exp(-5), Math.exp(-8), Math.exp(-5), Math.exp(-8));
			final VectorDetector detector3 = new VectorDetector(ProbabilityAggregationMethod.NORMALIZED_MULTIPLY, featureCreator3, classifier3, true);


			final UberFeatureCreator featureCreator4 = new UberFeatureCreator();
			final LibSvmClassifier classifier4 = new LibSvmClassifier(1, 0.2, 0.1, 1, 0.001, 3, ScalingMethod.NONE);
			final VectorDetector detector4 = new VectorDetector(ProbabilityAggregationMethod.NORMALIZED_MULTIPLY, featureCreator4, classifier4, true);

			final AnomalyDetection detection = new AnomalyDetection(detector, learner);
			final AnomalyDetection detection2 = new AnomalyDetection(detector2, learner);
			final AnomalyDetection detection3 = new AnomalyDetection(detector3, learner);
			final AnomalyDetection detection4 = new AnomalyDetection(detector4, learner);


			final ExperimentResult expected = new ExperimentResult(467, 4340, 193, 0);
			final Path p = Paths.get(this.getClass().getResource("/pdtta/smac_mix_type1.txt").toURI());
			// final ExperimentResult actual = detection.trainTest(p);
			// final ExperimentResult actual2 = detection2.trainTest(p);
			// final ExperimentResult actual3 = detection3.trainTest(p);
			final ExperimentResult actual4 = detection4.trainTest(p);

			// assertEquals(expected, actual);
			final ExperimentResult expected2 = new ExperimentResult(467, 2642, 1891, 0);
			// assertEquals(expected2, actual2);

			final ExperimentResult expected3 = new ExperimentResult(467, 572, 3961, 0);
			// assertEquals(expected3, actual3);

			final ExperimentResult expected4 = new ExperimentResult(467, 572, 3961, 0);
			assertEquals(expected4, actual4);
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

}
