package clases;

import java.util.Objects;

public class Fly {
	private Integer id_Fly;
	private Integer id_AirportDest;
	private Integer id_AirportOrig;
	private String plate;
	private Long time_Dep;
	private Long time_Arr;
	
	public Fly( Integer id_Fly, Integer id_AirportDest, Integer id_AirportOrig, String plate,  Long time_Dep, Long time_Arr) {
		super(); 
		this.id_Fly = id_Fly;
		this.id_AirportDest = id_AirportDest;
		this.id_AirportOrig = id_AirportDest;
		this.plate = plate;
		this.time_Dep = time_Dep;
		this.time_Arr = time_Arr;
	}
	public Fly() {
		super();
	}
	
	public Integer getId_Fly() {
		return id_Fly;
	}
	public void setId_Fly(Integer id_Fly) {
		this.id_Fly = id_Fly;
	}
	public Integer getId_AirportDest() {
		return id_AirportDest;
	}
	public void setId_AirportDest(Integer id_AirportDest) {
		this.id_AirportDest = id_AirportDest;
	}
	public Integer getId_AirportOrig() {
		return id_AirportOrig;
	}
	public void setId_AirportOrig(Integer id_AirportOrig) {
		this.id_AirportOrig = id_AirportOrig;
	}
	public String getPlate() {
		return plate;
	}
	public void setPlate(String plate) {
		this.plate = plate;
	}
	public Long getTime_Dep() {
		return time_Dep;
	}
	public void setTime_Dep(Long time_Dep) {
		this.time_Dep = time_Dep;
	}
	public Long getTime_Arr() {
		return time_Arr;
	}
	public void setTime_Arr(Long time_Arr) {
		this.time_Arr = time_Arr;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id_AirportDest, id_AirportOrig, id_Fly, plate, time_Arr, time_Dep);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Fly other = (Fly) obj;
		return Objects.equals(id_AirportDest, other.id_AirportDest)
				&& Objects.equals(id_AirportOrig, other.id_AirportOrig) && Objects.equals(id_Fly, other.id_Fly)
				&& Objects.equals(plate, other.plate) && Objects.equals(time_Arr, other.time_Arr)
				&& Objects.equals(time_Dep, other.time_Dep);
	}
	
	@Override
	public String toString() {
		return "Fly [id_Fly=" + id_Fly + ", id_AirportDest=" + id_AirportDest + ", id_AirportOrig=" + id_AirportOrig
				+ ", plate=" + plate + ", time_Dep=" + time_Dep + ", time_Arr=" + time_Arr + "]";
	}
	
}
