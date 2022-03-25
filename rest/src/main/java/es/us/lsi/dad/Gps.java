package es.us.lsi.dad;

import java.util.Objects;

public class Gps {
	private Integer id;
	private Double Lat;
	private Double Long;
	private Integer dir;
	private Double vel;
	private Double alt;
	

	public Gps(Integer id, Double lat, Double lon, Integer dir, Double vel, Double alt) {
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


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Double getLat() {
		return Lat;
	}


	public void setLat(Double lat) {
		Lat = lat;
	}


	public Double getLong() {
		return Long;
	}


	public void setLong(Double l) {
		Long = l;
	}


	public Integer getDir() {
		return dir;
	}


	public void setDir(Integer dir) {
		this.dir = dir;
	}


	public Double getVel() {
		return vel;
	}


	public void setVel(Double vel) {
		this.vel = vel;
	}


	public Double getAlt() {
		return alt;
	}


	public void setAlt(Double alt) {
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
		return Objects.equals(Lat, other.Lat) && Objects.equals(Long, other.Long) && Objects.equals(alt, other.alt)
				&& Objects.equals(dir, other.dir) && Objects.equals(id, other.id) && Objects.equals(vel, other.vel);
	}


	@Override
	public String toString() {
		return "Gps [id=" + id + ", Lat=" + Lat + ", Long=" + Long + ", dir=" + dir + ", vel=" + vel + ", alt=" + alt
				+ "]";
	}
	
}