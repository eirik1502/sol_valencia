package utils.maths;

public class Vec2 {

	public float x, y;

	public Vec2() {
		x = 0.0f;
		y = 0.0f;
	}

	public Vec2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vec2(Vec2 copyOf) {
		x = copyOf.x;
		y = copyOf.y;
	}
	
	public static Vec2 newLenDir(float length, float dir) {
		Vec2 v = new Vec2();
		v.setLenDir(length, dir);
		return v;
	}
	public static Vec2 newMaxValue() {
		return new Vec2(Float.MAX_VALUE, Float.MAX_VALUE);
	}
	
	
	public void setLenDir(float length, float direction) {
		x = M.cos(direction)*length;
		y = M.sin(direction)*length;
	}
	public void setLength(float length) {
		setLenDir(length, getDirection());
	}
	public void setDirection(float dir) {
		setLenDir(getLength(), dir);
	}
	

	public Vec2 add(Vec2 vec) {
		return new Vec2(x + vec.x, y + vec.y);
	}
	public Vec2 subtract(Vec2 vec) {
		return new Vec2(x - vec.x, y - vec.y);
	}
	public float dotProduct(Vec2 vec) {
		return x*vec.x + y*vec.y;
	}
	
	public Vec2 scale(float s) {
		return new Vec2(x*s, y*s);
	}
	
	public Vec2 negative() {
		return new Vec2(-x, -y);
	}
	
	public Vec2 normalize() {
		float len = getLength();
		if(len == 0) return new Vec2();
		return new Vec2(x/len, y/len);
	}
	
	public float getLength() {
		return M.sqrt(x*x + y*y);
	}
	public float getLengthSquared() {
		return x*x + y*y;
	}
	public float getDirection() {
		return M.atan2(y, x);
	}
	
	public boolean isNull() {
		return x == 0 && y == 0;
	}
	
	public void setAs(Vec2 v) {
		x = v.x;
		y = v.y;
	}
	public void setZero() {
		x = 0;
		y = 0;
	}
	
	@Override
	public boolean equals(Object vec) {
		Vec2 v = (Vec2)vec;
		return x == v.x && y == v.y;
	}
	
	
	public String toString() {
		return "[Vec2; x="+x+" y="+y+"]";
	}

}
