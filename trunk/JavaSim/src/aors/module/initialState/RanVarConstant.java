package aors.module.initialState;

public interface RanVarConstant {
  
  public static final String ranTypeArr[] = {"Uniform","Triangular","Normal","Binomial","NegBinomial",
    "HyperGeometric","Poisson","Exponential","LogNormal","Logarithmic",
    "Gamma","Erlang","Weibull"};

  public static final String ranProperty[][] = {{"lowerBound","upperBound"},
    {"lowerBound","upperBound","mode"},
    {"mean","standardDeviation"},
    {"n","p"},
    {"r","p"},
    {"totalPopulationSize","successPopulationSize","numberOfDraws"},
    {"lambda"},
    {"lambda"},
    {"mean","standardDeviation"},
    {"p"},
    {"shape","rate"},
    {"shape","rate"},
    {"shape","scale"}};

  public static final String ranExprProperty[][] = {{"LowerBoundExpr","UpperBoundExpr"},
    {"LowerBoundExpr","UpperBoundExpr","ModeExpr"},
    {"MeanExpr","StandardDeviationExpr"},
    {"N-Expr","P-Expr"},
    {"R-Expr","P-Expr"},
    {"TotalPopulationSizeExpr","SuccessPopulationSizeExpr","NumberOfDrawsExpr"},
    {"LambdaExpr"},
    {"LambdaExpr"},
    {"MeanExpr","StandardDeviationExpr"},
    {"P-Expr"},
    {"ShapeExpr","RateExpr"},
    {"ShapeExpr","RateExpr"},
    {"ShapeExpr","ScaleExpr"}
    };

  public static final String ranLan [] = {"Java", "JavaScript", "PHP"};

}
