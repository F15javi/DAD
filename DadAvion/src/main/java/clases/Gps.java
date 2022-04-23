package clases;

import java.util.Objects;

public class Gps {
	private Integer id_Gps;
	private Integer id_Fly;
	private Double lat;
	private Double lon;
	private Integer dir;
	private Double vel;
	private Double alt;
	private Long time;
	
	public Gps(Integer id_Gps, Integer id_Fly, Double lat, Double lon, Integer dir, Double vel, Double alt, Long time) {
		super();
		this.id_Gps = id_Gps;
		this.id_Fly = id_Fly;
		this.lat = lat;
		this.lon = lon;
		this.dir = dir;
		this.vel = vel;
		this.alt = alt;
		this.time = time;
	}
	
	public Gps() {
		super();
	}

	
	public Integer getId_Gps() {
		return id_Gps;
	}

	public void setId_Gps(Integer id_Gps) {
		this.id_Gps = id_Gps;
	}

	public Integer getId_Fly() {
		return id_Fly;
	}

	public void setId_Fly(Integer id_Fly) {
		this.id_Fly = id_Fly;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
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

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	@Override
	public int hashCode() {
		return Objects.hash(alt, dir, time, id_Fly, id_Gps, lat, lon, vel);
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
		return Objects.equals(alt, other.alt) && Objects.equals(dir, other.dir) && Objects.equals(time, other.time)
				&& Objects.equals(id_Fly, other.id_Fly) && Objects.equals(id_Gps, other.id_Gps)
				&& Objects.equals(lat, other.lat) && Objects.equals(lon, other.lon) && Objects.equals(vel, other.vel);
	}

	@Override
	public String toString() {
		return "Gps [id_Gps=" + id_Gps + ", id_Fly=" + id_Fly + ", lat=" + lat + ", lon=" + lon + ", dir=" + dir
				+ ", vel=" + vel + ", alt=" + alt + ", time=" + time + "]";
	}
	
	

}