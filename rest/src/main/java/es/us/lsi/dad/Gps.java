package es.us.lsi.dad;

import java.util.Objects;

public class Gps {
	private int id;
	private double Lat;
	private double Long;
	private int dir;
	private double vel;
	private double alt;
	

	public Gps(int id, double lat, double lon, int dir, double vel, double alt) {
		super();
		this.id = id;
		this.Lat = lat;
		this.Long = lon;
		this.dir = dir;
		this.vel = vel;
		this.alt = alt;
	}


	public Gps() {
		super();
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


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


	@Override
	public int hashCode() {
		return Objects.hash(Lat, Long, alt, dir, id, vel);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Gps other = (Gps) obj;
		return Double.doubleToLongBits(Lat) == Double.doubleToLongBits(other.Lat)
				&& Double.doubleToLongBits(Long) == Double.doubleToLongBits(other.Long)
				&& Double.doubleToLongBits(alt) == Double.doubleToLongBits(other.alt) && dir == other.dir
				&& id == other.id && Double.doubleToLongBits(vel) == Double.doubleToLongBits(other.vel);
	}


	@Override
	public String toString() {
		return "Gps [id=" + id + ", Lat=" + Lat + ", Long=" + Long + ", dir=" + dir + ", vel=" + vel + ", alt=" + alt
				+ "]";
	}
	


	

}