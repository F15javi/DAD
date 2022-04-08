package mqtt;



import java.util.Objects;

public class Gps {
	private Integer id;
	private Double lat;
	private Double lon;
	private Integer dir;
	private Double vel;
	private Double alt;
	private Long hor;
	

	public Gps(Integer id, Double lat, Double lon, Integer dir, Double vel, Double alt, Long hor) {
		super();
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		this.dir = dir;
		this.vel = vel;
		this.alt = alt;
		this.hor = hor;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
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


	@Override
	public int hashCode() {
		return Objects.hash(alt, dir, hor, id, lat, lon, vel);
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
		return Objects.equals(alt, other.alt) && Objects.equals(dir, other.dir) && Objects.equals(hor, other.hor)
				&& Objects.equals(id, other.id) && Objects.equals(lat, other.lat) && Objects.equals(lon, other.lon)
				&& Objects.equals(vel, other.vel);
	}


	@Override
	public String toString() {
		return "Gps [id=" + id + ", lat=" + lat + ", lon=" + lon + ", dir=" + dir + ", vel=" + vel + ", alt=" + alt
				+ ", hor=" + hor + "]";
	}

}