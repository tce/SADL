package jsat.distributions.empirical;

import java.util.LinkedList;
import java.util.List;

import jsat.distributions.Distribution;
import jsat.distributions.empirical.kernelfunc.GaussKF;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import jsat.math.Function;
import jsat.math.optimization.GoldenSearch;
import sadl.constants.KDEFormelVariant;

public class KernelDensityEstimatorButla {

	protected Vec dataPoints;
	protected Function kernelPdfFunction;
	protected Function kernelDerivationFunction;

	protected double minSearchAccuracy;
	protected double minSearchStep;

	protected double startX;
	protected double endX;

	public static final double DEFAULT_BANDWIDTH = 0.6d;
	public static final double DEFAULT_MIN_SEARCH_ACCURACY = 0.25d;

	public KernelDensityEstimatorButla(double[] dataPoints, KDEFormelVariant formelVariant) {
		this(new DenseVector(dataPoints), formelVariant, DEFAULT_BANDWIDTH, DEFAULT_BANDWIDTH / 4.0, DEFAULT_MIN_SEARCH_ACCURACY);
	}

	public KernelDensityEstimatorButla(Vec dataPoints, KDEFormelVariant formelVariant) {
		this(dataPoints, formelVariant, DEFAULT_BANDWIDTH, DEFAULT_BANDWIDTH / 4.0, DEFAULT_MIN_SEARCH_ACCURACY);
	}

	public KernelDensityEstimatorButla(double[] dataPoints, KDEFormelVariant formelVariant, double bandwidth) {
		this(new DenseVector(dataPoints), formelVariant, bandwidth, bandwidth / 4.0, DEFAULT_MIN_SEARCH_ACCURACY);
	}

	public KernelDensityEstimatorButla(Vec dataPoints, KDEFormelVariant formelVariant, double bandwidth) {
		this(dataPoints, formelVariant, bandwidth, bandwidth / 4.0, DEFAULT_MIN_SEARCH_ACCURACY);
	}

	public KernelDensityEstimatorButla(double[] dataPoints, KDEFormelVariant formelVariant, double bandwidth, double minSearchStep,
			double minSearchAccuracy) {
		this(new DenseVector(dataPoints), formelVariant, bandwidth, minSearchStep, minSearchAccuracy);
	}

	public KernelDensityEstimatorButla(Vec dataPoints, KDEFormelVariant formelVariant, double bandwidth, double minSearchStep, double minSearchAccuracy) {

		this.dataPoints = dataPoints.sortedCopy();
		this.minSearchStep = minSearchStep;
		this.minSearchAccuracy = minSearchAccuracy;

		if (formelVariant == KDEFormelVariant.OriginalKDE) {

			final KernelDensityEstimator kernelDensity = new KernelDensityEstimator(dataPoints, GaussKF.getInstance(), bandwidth);
			kernelPdfFunction = Distribution.getFunctionPDF(kernelDensity);
			kernelDerivationFunction = Distribution.getFunctionPDF(new KernelDensityEstimator(dataPoints, GaussKFDerivation.getInstance(), bandwidth));

			startX = kernelDensity.min() + bandwidth;
			endX = kernelDensity.max() - bandwidth;

		} else if (formelVariant == KDEFormelVariant.OriginalButlaVariableBandwidth) {

			kernelPdfFunction = new Function() {

				@Override
				public double f(Vec x) {
					return f(new double[] { x.get(0) });
				}
				@Override
				public double f(double... x) {

					final double t = x[0];
					double sum = 0.0d;

					for (int i = 0; i < dataPoints.length(); i++) {
						final double ti = dataPoints.get(i);
						sum += Math.exp(-Math.pow(t - ti, 2) / (2 * 0.05 * ti)) / (Math.sqrt(2.0 * Math.PI) * 0.05 * t);
					}

					return sum / dataPoints.length();
				}
			};

			kernelDerivationFunction = new Function() {
				@Override
				public double f(Vec x) {
					return f(new double[] { x.get(0) });
				}

				@Override
				public double f(double... x) {

					final double t = x[0];
					double sum = 0.0d;

					for (int i = 0; i < dataPoints.length(); i++) {
						final double ti = dataPoints.get(i);
						sum += 159.577 * Math.exp(-10 * Math.pow(t - ti, 2) / ti) * (ti - t) / Math.pow(ti, 2);
					}

					return sum / dataPoints.length();
				}
			};

			startX = dataPoints.get(0);
			endX = dataPoints.get(dataPoints.length() - 1);

		} else if (formelVariant == KDEFormelVariant.ButlaBandwidthNotSquared) {

			kernelPdfFunction = new Function() {
				@Override
				public double f(Vec x) {
					return f(new double[] { x.get(0) });
				}

				@Override
				public double f(double... x) {

					final double t = x[0];
					double sum = 0.0d;

					for (int i = 0; i < dataPoints.length(); i++) {
						final double ti = dataPoints.get(i);
						sum += Math.exp(-Math.pow(t - ti, 2) / (2 * Math.pow(0.05 * ti, 2))) / (Math.sqrt(2.0 * Math.PI) * 0.05 * t);
					}

					return sum / dataPoints.length();
				}
			};

			kernelDerivationFunction = new Function() {
				@Override
				public double f(Vec x) {
					return f(new double[] { x.get(0) });
				}

				@Override
				public double f(double... x) {

					final double t = x[0];
					double sum = 0.0d;

					for (int i = 0; i < dataPoints.length(); i++) {
						final double ti = dataPoints.get(i);
						sum += 3191.54 * Math.exp(-200 * Math.pow(t - ti, 2) / Math.pow(ti, 2)) * (ti - t) / Math.pow(ti, 3);
					}

					return sum / dataPoints.length();
				}
			};

			startX = dataPoints.get(0);
			endX = dataPoints.get(dataPoints.length() - 1);

		} else if (formelVariant == KDEFormelVariant.ButlaBandwidthSquared) {

			kernelPdfFunction = new Function() {
				@Override
				public double f(Vec x) {
					return f(new double[] { x.get(0) });
				}

				@Override
				public double f(double... x) {

					final double t = x[0];
					double sum = 0.0d;

					for (int i = 0; i < dataPoints.length(); i++) {
						final double ti = dataPoints.get(i);
						sum += Math.exp(-Math.pow(t - ti, 2) / (2 * 0.05 * ti)) / (Math.sqrt(2.0 * Math.PI * 0.05 * t));
					}

					return sum / dataPoints.length();
				}
			};

			kernelDerivationFunction = new Function() {
				@Override
				public double f(Vec x) {
					return f(new double[] { x.get(0) });
				}

				@Override
				public double f(double... x) {

					final double t = x[0];
					double sum = 0.0d;

					for (int i = 0; i < dataPoints.length(); i++) {
						final double ti = dataPoints.get(i);
						sum += 35.6825 * Math.exp(-10 * Math.pow(t - ti, 2) / ti) * (ti - t) / Math.sqrt(Math.pow(ti, 3));
					}

					return sum / dataPoints.length();
				}
			};

			startX = dataPoints.get(0);
			endX = dataPoints.get(dataPoints.length() - 1);
		}
	}

	public double prime(double x) {

		return kernelDerivationFunction.f(x);
	}

	/**
	 * @param accuracy
	 *            The accuracy of min-search in getMinima function.
	 */
	public void setAccuracy(double accuracy) {

		if (Double.isNaN(accuracy)) {
			throw new IllegalArgumentException();
		}
		this.minSearchAccuracy = accuracy;
	}

	public Double[] getMinima() {

		final List<Double> pointList = new LinkedList<>();

		double lastX = startX;
		double lastValue = kernelDerivationFunction.f(lastX);

		for (double x = lastX + minSearchStep; x < endX; x = x + minSearchStep) {
			final double newValue = kernelDerivationFunction.f(x);

			if (lastValue < 0 && newValue > 0) {
				pointList.add(GoldenSearch.minimize(minSearchAccuracy, 100, lastX, x, 0, kernelPdfFunction, new double[1]));
			}

			lastX = x;
			lastValue = newValue;
		}

		return pointList.toArray(new Double[0]);
	}

}
