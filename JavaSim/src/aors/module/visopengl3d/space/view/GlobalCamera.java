package aors.module.visopengl3d.space.view;

/**
 * Global camera
 * 
 * @author Susanne Schölzel
 * @since November 17th, 2011
 * 
 */
public class GlobalCamera {
	public static final String GLOBAL_CAMERA = "GlobalCamera";
	
	public static final String EYE_POSITION = "eyePosition";
	public static final String LOOK_AT = "lookAt";
	public static final String UP_VECTOR = "upVector";
	
	private double[] eyePosition = new double[3];
	private double[] lookAt = new double[3];
	private double[] upVector = new double[3];
	  
	
	public double[] getEyePosition() {
		return eyePosition;
	}
		  
	public void setEyePosition(double[] eyePosition) {
		this.eyePosition = eyePosition;
	}
		  
	public double[] getLookAt() {
		return lookAt;
	}
		  
	public void setLookAt(double[] lookAt) {
		this.lookAt = lookAt;
	}
	  
	public double[] getUpVector() {
		return upVector;
	}
	  
	public void setUpVector(double[] upVector) {
		this.upVector = upVector;
	}
}
