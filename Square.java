import java.util.List;

public class Square {
	List<Point> points;
	
	public Square(List<Point> points) {
		this.points = points;
	}
	
	public Point getTopLeft() {
		return this.points.get(0);
	}
	
	public Point getTopRight() {
		return this.points.get(1);
	}
	
	public Point getBottomRight() {
		return this.points.get(2);
	}
	
	public Point getBottomLeft() {
		return this.points.get(3);
	}
	
	@Override
	public String toString() {
		return points.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((points == null) ? 0 : points.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Square other = (Square) obj;
		if (points == null) {
			if (other.points != null)
				return false;
		} else if (!points.equals(other.points))
			return false;
		return true;
	}

}
