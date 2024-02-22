package it.eremind.progetto_scuole.app_eventi.api.dto;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;

public class UserDto implements UserOperationI {
	
	@NotBlank
	private String idUser;
	
	@Valid
	private Data data;
	@Valid
	private PhysicalDetails phys;
	
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder(1024);
		sb.append("{")
		.append("idUser:").append(idUser).append(";")
		.append("data=").append(data).append(";")
		.append("}");
		return sb.toString();		
	}

	public static class Data{
		@NotBlank
		private String firstName;
		@NotBlank
		private String lastName;
		
		public Data() {	}
		public Data(String firstName, String lastName) {
			this.firstName=firstName;
			this.lastName=lastName;
		}
		
		@Override
		public String toString(){
			StringBuilder sb=new StringBuilder(1024);
			sb.append("{")
			.append("firstName:").append(firstName).append(";")
			.append("lastName=").append(lastName).append(";")
			.append("}");
			return sb.toString();		
		}
		
		public String getFirstName() {
			return firstName;
		}
		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
		public String getLastName() {
			return lastName;
		}
		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
	}
	
	public static class PhysicalDetails{
		
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private java.util.Date dateOfBirth;
		private String height;
		private String weight;
		private String sex;
		
		public PhysicalDetails() {	}
		public PhysicalDetails(Date dateOfBirth, String height, String weight, String sex) {
			this.dateOfBirth=dateOfBirth;
			this.height=height;
			this.weight=weight;
			this.sex=sex;
		}
		
		public java.util.Date getDateOfBirth() {
			return dateOfBirth;
		}
		public void setDateOfBirth(java.util.Date dateOfBirth) {
			this.dateOfBirth = dateOfBirth;
		}
		public String getHeight() {
			return height;
		}
		public void setHeight(String height) {
			this.height = height;
		}
		public String getWeight() {
			return weight;
		}
		public void setWeight(String weight) {
			this.weight = weight;
		}
		public String getSex() {
			return sex;
		}
		public void setSex(String sex) {
			this.sex = sex;
		}
	}

	public String getIdUser() {
		return idUser;
	}
	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}
	
	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public PhysicalDetails getPhys() {
		return phys;
	}

	public void setPhys(PhysicalDetails phys) {
		this.phys = phys;
	}
	
}
