package itt.t00154755.mouseapp;

import java.io.Serializable;

public class AccelerometerObject implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private float x;
	private float y;
	private float z;
	/**
	 * 
	 */
	public AccelerometerObject() {
		
	}

	public AccelerometerObject(float[] values) {
		setX(values[0]);
		setY(values[1]);
		setZ(values[2]);
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	
	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	@Override
	public String toString() {
		String output = "X-Axis: " +getX() + "\nY-Axis: " + getY() + "\nZ-Axis: " + getY();
		return output;
	}
}