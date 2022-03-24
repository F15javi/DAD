package es.us.dad.dadVertx;

import java.util.Date;
import java.util.Objects;

public class ExampleJson {
	private String nombre;
	private String apellidos;
	private int edad;
	private Date nacimiento;
	private float estatura;

	public ExampleJson(String nombre, String apellidos, int edad, Date nacimiento, float estatura) {
		super();
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.edad = edad;
		this.nacimiento = nacimiento;
		this.estatura = estatura;
	}

	public ExampleJson() {
		super();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public int getEdad() {
		return edad;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}

	public Date getNacimiento() {
		return nacimiento;
	}

	public void setNacimiento(Date nacimiento) {
		this.nacimiento = nacimiento;
	}

	public float getEstatura() {
		return estatura;
	}

	public void setEstatura(float estatura) {
		this.estatura = estatura;
	}

	@Override
	public int hashCode() {
		return Objects.hash(apellidos, edad, estatura, nacimiento, nombre);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExampleJson other = (ExampleJson) obj;
		return Objects.equals(apellidos, other.apellidos) && edad == other.edad
				&& Float.floatToIntBits(estatura) == Float.floatToIntBits(other.estatura)
				&& Objects.equals(nacimiento, other.nacimiento) && Objects.equals(nombre, other.nombre);
	}

	@Override
	public String toString() {
		return "ExampleJson [nombre=" + nombre + ", apellidos=" + apellidos + ", edad=" + edad + ", nacimiento="
				+ nacimiento + ", estatura=" + estatura + "]";
	}

}
