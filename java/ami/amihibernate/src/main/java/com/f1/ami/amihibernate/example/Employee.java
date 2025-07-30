package com.f1.ami.amihibernate.example;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Persister;

import com.f1.ami.amihibernate.AmiEntityPersister;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "Employee")
@Table(name = "EMPLOYEE")
@Persister(impl = AmiEntityPersister.class)
public class Employee {
	@Id
	@GeneratedValue(generator = "ami-id-generator")
	@GenericGenerator(name = "ami-id-generator", strategy = "com.f1.ami.amihibernate.AmiSimpleIdGenerator")
	@Column(name = "id")
	private int id;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	@Column(name = "salary")
	private int salary;

	public Employee() {

	}

	public Employee(String fname, String lname, int salary) {
		this.firstName = fname;
		this.lastName = lname;
		this.salary = salary;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String first_name) {
		this.firstName = first_name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String last_name) {
		this.lastName = last_name;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}
}