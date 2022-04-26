package clases;

import java.util.Objects;

public class Airport {
	private Integer id_Airport;
	private String name; 
	private Double lat;
	private Double lon;

	
	public Airport(Integer id_Airport, String name, Double lat, Double lon) {
		super();
		this.id_Airport = id_Airport;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
	}
	
	public Airport() {
		super();
		this.id_Airport = 0;
		this.name = "";
		this.lat = 00.0;
		this.lon = 00.0;
	}

	public Integer getId_Airport() {
		return id_Airport;
	}

	public void setId_Airport(Integer id_Airport) {
		this.id_Airport = id_Airport;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	@Override
	public int hashCode() {
		return Objects.hash(id_Airport, lat, lon, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Airport other = (Airport) obj;
		return Objects.equals(id_Airport, other.id_Airport) && Objects.equals(lat, other.lat)
				&& Objects.equals(lon, other.lon) && Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "Airport [id_Airport=" + id_Airport + ", name=" + name + ", lat=" + lat + ", lon=" + lon + "]";
	}
	

	
	
}
