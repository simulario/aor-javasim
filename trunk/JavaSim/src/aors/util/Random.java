package aors.util;

import java.util.Date;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Binomial;
import cern.jet.random.ChiSquare;
import cern.jet.random.Distributions;
import cern.jet.random.Exponential;
import cern.jet.random.Gamma;
import cern.jet.random.HyperGeometric;
import cern.jet.random.Logarithmic;
import cern.jet.random.NegativeBinomial;
import cern.jet.random.Normal;
import cern.jet.random.Poisson;
import cern.jet.random.StudentT;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;

/**
 * Provides some important probability distributions.
 * 
 * @author Daniel Draeger
 */
public class Random {

  private static RandomEngine mersenne = new MersenneTwister();

  public static void setSeed(int seed) {
    mersenne = new MersenneTwister(seed);
  }

  public static void setRandomSeed() {
    mersenne = new MersenneTwister(new Date(System.currentTimeMillis()));
  }

  public static boolean uniformBoolean() {
    return Random.uniformInt() == 1;
  }

  /**
   * Using this method is equal to use <CODE>uniform(0, 1)</CODE>.
   * 
   * @return the next value of a uniformally distributed random variable
   *         between 0 and 1
   */
  public static int uniformInt() {
    Uniform uniX = new Uniform(mersenne);
    return uniX.nextInt();
  }

  public static double uniform() {
    Uniform uniX = new Uniform(mersenne);
    return uniX.nextDouble();
  }

  /**
   * @param b  max-Value
   * @return the next value of an uniformally distributed random variable
   *         between 0 and b
   */
  public static int uniformInt(int b) {
    Uniform uniX = new Uniform(mersenne);
    return uniX.nextIntFromTo(0, b);
  }

  public static double uniform(double b) {
    Uniform uniX = new Uniform(mersenne);
    return uniX.nextDoubleFromTo(0, b);
  }

  /**
   * @param a  min-Value
   * @param b  max-Value
   * @return the next value of a uniformally distributed random variable
   *         between a and b
   */
  public static int uniformInt(int a, int b) {
    Uniform uniX = new Uniform(mersenne);
    return uniX.nextIntFromTo(a, b);
  }

  public static double uniform(double a, double b) {
    Uniform uniX = new Uniform(mersenne);
    return uniX.nextDoubleFromTo(a, b);
  }

  /**
   * @param mean (µ)
   * @param stDev (σ)  standard deviation
   * @return the next value of a normally distributed random variable
   */
  public static double normal(double mean, double stDev) {
    Normal norX = new Normal(mean, stDev, mersenne);
    return norX.nextDouble();
  }

  public static int normalInt(double mean, double stDev) {
    Normal norX = new Normal(mean, stDev, mersenne);
    return norX.nextInt();
  }

  public static double logNormal(double mean, double stDev) {
    double next = normal(mean, stDev);
    return Math.exp(next);
  }

  public static int logNormalInt(double mean, double stDev) {
    double next = normal(mean, stDev);
    return new Double(Math.exp(next)).intValue();
  }

  /**
   * @param totalPopulationSize
   *          (Wikipedia:N)
   * @param successPopulationSize
   *          (Wikipedia:m) Size of the population the elements of which
   *          represent success
   * @param numberOfDraws
   *          (Wikipedia:n) sample size
   * @return the next value of a hypergeometric distributed random variable
   */
  public static int hyperGeometric(int totalPopulationSize,
      int successPopulationSize, int numberOfDraws) {
    HyperGeometric hypX = new HyperGeometric(totalPopulationSize,
        successPopulationSize, numberOfDraws, mersenne);
    return hypX.nextInt();
  }

  /**
   * @param sampleSize
   *          (n)
   * @param probability
   *          (p) probability of success
   * @return the next value of a binomial distributed random variable
   */
  public static int binomial(int sampleSize, double probability) {
    Binomial binX = new Binomial(sampleSize, probability, mersenne);
    return binX.nextInt();
  }

  /**
   * @param numberOfFailure
   *          (r)
   * @param probability
   *          (p) probability of success
   * @return the next value of a negative binomial distributed random variable
   */
  public static int negBinomial(int numberOfFailure, double probability) {
    NegativeBinomial negX = new NegativeBinomial(numberOfFailure, probability,
        mersenne);
    return negX.nextInt();
  }

  /**
   * @param mean
   *          (λ)
   * @return the next value of a poisson distributed random variable
   */
  public static int poisson(double mean) {
    Poisson p = new Poisson(mean, mersenne);
    return p.nextInt();
  }

  /**
   * @param probability
   *          (p)
   * @return the next value of a logarithmic distributed random variable
   */
  public static int logarithmic(double probability) {
    Logarithmic logX = new Logarithmic(probability, mersenne);
    return logX.nextInt();
  }

  /**
   * @param degreesOfFreedom
   *          (t)
   * @return the next value of a student-t distributed random variable
   */
  public static double studentT(double degreesOfFreedom) {
    StudentT stuX = new StudentT(degreesOfFreedom, mersenne);
    return stuX.nextDouble();
  }

  public static int studentTInt(double degreesOfFreedom) {
    StudentT stuX = new StudentT(degreesOfFreedom, mersenne);
    return stuX.nextInt();
  }

  /**
   * @param degreesOfFreedom
   *          (f)
   * @return the next value of a ChiSquared distributed random variable
   */
  public static double chisquare(double degreesOfFreedom) {
    ChiSquare chiX = new ChiSquare(degreesOfFreedom, mersenne);
    return chiX.nextDouble();
  }

  public static int chisquareInt(double degreesOfFreedom) {
    ChiSquare chiX = new ChiSquare(degreesOfFreedom, mersenne);
    return chiX.nextInt();
  }

  /**
   * @param rate
   *          (λ)
   * @return the next value of a Exponential distributed random variable
   */
  public static double exponential(double rate) {
    Exponential expX = new Exponential(rate, mersenne);
    return expX.nextDouble();
  }

  public static int exponentialInt(double rate) {
    Exponential expX = new Exponential(rate, mersenne);
    return expX.nextInt();
  }

  /**
   * @param shape
   *          (α) - parameter of Gamma-function
   * @param rate
   *          (β) - rate parameter
   * @return the next value of a Gamma distributed random variable
   */
  public static double gamma(double shape, double rate) {
    Gamma gamX = new Gamma(shape, rate, mersenne);
    return gamX.nextDouble();
  }

  public static int gammaInt(double shape, double rate) {
    Gamma gamX = new Gamma(shape, rate, mersenne);
    return gamX.nextInt();
  }

  /**
   * @param shapeInt
   *          - amount of variates which are independent identical exponential
   *          distributed
   * @param rate
   *          (λ)
   * @return the next value of a Erlang distributed random variable
   */
  public static double erlang(int shapeInt, double rate) {
    double shape = shapeInt;
    return gamma(shape, rate);
  }

  public static int erlangInt(int shapeInt, double rate) {
    double shape = shapeInt;
    return gammaInt(shape, rate);
  }

  /**
   * @param shape
   *          (k)
   * @param scale
   *          (λ)
   * @return the next value of a Weibull distributed random variable
   */
  public static double weibull(double shape, double scale) {
    return Distributions.nextWeibull(shape, scale, mersenne);
  }

  public static int weibullInt(double shape, double scale) {
    return (int) Distributions.nextWeibull(shape, scale, mersenne);
  }

  /**
   * @param min
   *          (a)
   * @param max
   *          (b)
   * @param mode
   *          (c)
   * @return the next value of a Triangular distributed random variable
   */
  public static double triangular(double min, double max, double mode) {

    try {
      Triangular triX = new Triangular(min, max, mode, mersenne);
      return triX.nextDouble();
    } catch (IllegalArgumentException iae) {
      System.err.println(iae.getMessage());
    }
    return Double.NaN;
  }

  public static int triangularInt(double min, double max, double mode) {
    try {
      Triangular triX = new Triangular(min, max, mode, mersenne);
      return triX.nextInt();
    } catch (IllegalArgumentException iae) {
      System.err.println(iae.getMessage());
    }
    return (int) Double.NaN;
  }

  public static class Triangular extends AbstractContinousDistribution {

    private static final long serialVersionUID = -5462205700012985168L;
    private double min, max, mode;
    private RandomEngine engine;

    public Triangular(double min, double max, double most, RandomEngine eng) {
      if (max <= min)
        throw new IllegalArgumentException(
            "Illegal min- or maxvalues in Triangular constructor!");
      this.min = min;
      this.max = max;
      this.mode = most;
      this.engine = eng;
    }

    @Override
    public double nextDouble() {
      double r = (mode - min) / (max - min);
      double result = 0;
      double x = engine.raw();
      double alp = Math.atan(1 / (2 * r));
      double bnew = (mode - min) / Math.cos(alp);
      double h = Math.sqrt(bnew * bnew - (mode - min) * (mode - min));
      // h = 2/(max-min);
      if ((0 < x) && (x <= r)) {
        result = min + Math.sqrt(2 * (mode - min) * x / h) * (mode - min)
            / (2 * r);
      } else {
        result = max - Math.sqrt(2 * (1 - x) * (max - mode) / h) * (mode - min)
            / (2 * r);
      }
      return result;
    }

    @Override
    public int nextInt() {
      return (int) nextDouble();
    }
  }

}
