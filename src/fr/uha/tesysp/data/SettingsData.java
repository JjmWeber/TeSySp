package fr.uha.tesysp.data;

import fr.uha.tesysp.util.Constants;

public class SettingsData {

    private int nbThreads = Constants.DEFAULTTHREADNUMBER;

    private int FGBGThresh = Constants.DEFAULTFGBGTHRESH;

    private double orientationRobustnessAngularStep = Constants.DEFAULTORIENTATIONROBUSTNESSANGULARSTEP;
    private double alpha = Constants.DEFAULTALPHA;
    private double beta = Constants.DEFAULTBETA;

    private boolean symmetry = Constants.DEFAULTSYMMETRY;
    private boolean onlyFG = Constants.DEFAULTONLYFG;
    private boolean onlyBG = Constants.DEFAULTONLYBG;

    private boolean autoFindResult = Constants.DEFAULTAUTOFINDTHRESHOLD;

    public SettingsData() {

    }

    public int getNbThreads() {
        return nbThreads;
    }

    public void setNbThreads(int nbThreads) {
        this.nbThreads = nbThreads;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public int getFGBGThresh() {
        return FGBGThresh;
    }

    public void setFGBGThresh(int fGBGThresh) {
        FGBGThresh = fGBGThresh;
    }

    public double getAngularStep() {
        return orientationRobustnessAngularStep;
    }

    public void setAngularStep(double angularStep) {
        this.orientationRobustnessAngularStep = angularStep;
    }

    public boolean isSymmetry() {
        return symmetry;
    }

    public void setSymmetry(boolean symmetry) {
        this.symmetry = symmetry;
    }

    public boolean isOnlyFG() {
        return onlyFG;
    }

    public void setOnlyFG(boolean onlyFG) {
        this.onlyFG = onlyFG;
    }

    public boolean isOnlyBG() {
        return onlyBG;
    }

    public void setOnlyBG(boolean onlyBG) {
        this.onlyBG = onlyBG;
    }

    public boolean isAutoFindResult() {
        return autoFindResult;
    }

    public void setAutoFindResult(boolean buildReferences) {
        this.autoFindResult = buildReferences;
    }
}
