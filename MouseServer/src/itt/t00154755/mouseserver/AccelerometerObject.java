package itt.t00154755.mouseserver;

public class AccelerometerObject {

	private int x;
	private int y;
	private int z;

	/**
	 * 
	 */
	public AccelerometerObject() {

	}

	public AccelerometerObject(int[] values) {
		setX(values[0]);
		setY(values[1]);
		setZ(values[2]);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {

		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	@Override
	public String toString() {
		String output = "X-Axis: " + getX() + "\nY-Axis: " + getY()
				+ "\nZ-Axis: " + getY();

		return output;
	}
}
