package es.us.dad.dadVertx;

public class Gps {
	private double Lat;
	private double Long;
	private int dir;
	private double vel;
	private double alt;
	

	//Método Constructor
	public Gps(double lat, double l, int dir, double vel, double alt) {
		super();
		Lat = lat;
		Long = l;
		this.dir = dir;
		this.vel = vel;
		this.alt = alt;
	}

	
	//Métodos Getters y Setters 
	public double getLat() {
		return Lat;
	}

	public void setLat(double lat) {
		Lat = lat;
	}

	public double getLong() {
		return Long;
	}

	public void setLong(double l) {
		Long = l;
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}

	public double getVel() {
		return vel;
	}

	public void setVel(double vel) {
		this.vel = vel;
	}

	public double getAlt() {
		return alt;
	}

	public void setAlt(double alt) {
		this.alt = alt;
	}

	//Método HashCode
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(Lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(Long);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(alt);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + dir;
		temp = Double.doubleToLongBits(vel);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	//Método Equals
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Gps other = (Gps) obj;
		if (Double.doubleToLongBits(Lat) != Double.doubleToLongBits(other.Lat))
			return false;
		if (Double.doubleToLongBits(Long) != Double.doubleToLongBits(other.Long))
			return false;
		if (Double.doubleToLongBits(alt) != Double.doubleToLongBits(other.alt))
			return false;
		if (dir != other.dir)
			return false;
		if (Double.doubleToLongBits(vel) != Double.doubleToLongBits(other.vel))
			return false;
		return true;
	}


	//Método toString
	@Override
	public String toString() {
		return "Gps [Lat=" + Lat + ", Long=" + Long + ", dir=" + dir + ", vel=" + vel + ", alt=" + alt + "]";
	}
	
	

	
	

}
