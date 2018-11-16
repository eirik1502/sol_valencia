package utils.maths;

public class Vec3 {

	public float x, y, z;

	public Vec3() {
		x = 0.0f;
		y = 0.0f;
		z = 0.0f;
	}

	public Vec3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3(Vec2 v, float z) {
		this(v.x, v.y, z);

	}
	

	public Vec3 subtract(Vec3 vec) {
		return new Vec3(x-=vec.x, y-=vec.y, z-=vec.z);
	}
}
