package utils.maths;



public class TrigUtils {
	
	public static float pointDirection(float x1, float y1, float x2, float y2) {
		float deltaY = y2 - y1;
		float deltaX = x2 - x1;
		float currRotation;
//		if (deltaX == 0) currRotation = deltaY > 0? M.PI/2 : 1.5f*M.PI;
//		else {
//			currRotation = M.atan( deltaY/deltaX);
//			if (deltaX < 0) currRotation -= M.PI;
//		}
		currRotation = M.atan2(deltaY, deltaX);
		return currRotation;
	}
	public static float pointDirection(Vec2 v1, Vec2 v2) {
		return pointDirection(v1.x, v1.y, v2.x, v2.y);
	}
	/**
	 * returns a normalized vector in the direction specified by points
	 */
	public static Vec2 pointDirectionVec( Vec2 p1, Vec2 p2) {
		Vec2 v = p2.subtract(p1);
		return v.normalize();
	}

	/**
	 *
	 * @param angle1
	 * @param angle2
	 * @return shortest positive relative angle from angle1 to angle2 (negative the other way)
	 */
	public static float shortesAngleBetween(float angle1, float angle2) {
		float diffAngle = angle2 - angle1;

		//if the angle between is greater than PI, get the inverse
		if (M.abs(diffAngle) > M.PI) {
			diffAngle = (2*M.PI - M.abs(diffAngle) );

			if (angle2 > angle1) {
				diffAngle = -diffAngle;
			}
		}
		return diffAngle;
	}

	/**
	 * maps an angle to the range -PI, PI
	 * @param angle
	 * @return
	 */
	public static float mapAngleToRange(float angle) {
		if (angle > -M.PI) {
			return ( (angle+M.PI) % (2*M.PI) ) - M.PI;
		}
		else {
			return - (( (M.abs(angle) +M.PI) % (2*M.PI) ) - M.PI );
		}

	}
	
	public static float lengthdirX(float length, float dir) {
		return (float)M.cos(dir)*length;
	}
	public static float lengthdirY(float length, float dir) {
		return (float)M.sin(dir)*length;
	}
}
