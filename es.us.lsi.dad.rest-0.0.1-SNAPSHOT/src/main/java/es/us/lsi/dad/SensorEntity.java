package es.us.lsi.dad;

import java.util.Arrays;

public class SensorEntity {
	private int idSensor;
	private String sensor;
	private long time;
	private double[] data;
	public SensorEntity(int idSensor, String sensor, long time, double[] data) {
		super();
		this.sensor = sensor;
		this.time = time;
		this.idSensor = idSensor;
		this.data = data;
	}
	public SensorEntity() {
		super();
		data = new double[0];
		sensor = "";
		time = 0;
		idSensor = 0;
	}
	
	public int getIdSensor() {
		return idSensor;
	}
	public void setIdSensor(int idSensor) {
		this.idSensor = idSensor;
	}
	public String getSensor() {
		return sensor;
	}
	public void setSensor(String sensor) {
		this.sensor = sensor;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public double[] getData() {
		return data;
	}
	public void setData(double[] data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "SensorEntity [sensor=" + sensor + ", time=" + time + ", data=" + Arrays.toString(data) + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		result = prime * result + (int) (idSensor ^ (idSensor >>> 32));
		result = prime * result + ((sensor == null) ? 0 : sensor.hashCode());
		result = prime * result + (int) (time ^ (time >>> 32));
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
		SensorEntity other = (SensorEntity) obj;
		if (!Arrays.equals(data, other.data))
			return false;
		if (idSensor != other.idSensor)
			return false;
		if (sensor == null) {
			if (other.sensor != null)
				return false;
		} else if (!sensor.equals(other.sensor))
			return false;
		if (time != other.time)
			return false;
		return true;
	}
	
	
	
	
	
}
