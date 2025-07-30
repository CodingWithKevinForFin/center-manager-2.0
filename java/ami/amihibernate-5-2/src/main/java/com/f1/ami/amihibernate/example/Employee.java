package com.f1.ami.amihibernate.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "EMPLOYEE")
//@Loader(namedQuery = "test")
//@NamedNativeQuery(name = "test", query = "select * from __TABLES")
//@Persister(impl = AmiEntityPersister.class)
public class Employee implements Serializable {
	@Id
	//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//	@GeneratedValue(generator = "ami-id-generator")
	//	@GenericGenerator(name = "ami-id-generator", strategy = "com.f1.ami.amihibernate.AmiSimpleIdGenerator")

	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_id")
	@SequenceGenerator(name = "seq_id", sequenceName = "seq_id", allocationSize = 1000)

	@Column(name = "id")
	private Long id;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	@Column(name = "salary")
	private int salary;

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<EmployeeTask> tasks;

	@Embedded
	@AttributeOverrides( //
			value = { //
					@AttributeOverride(name = "addressLine1", column = @Column(name = "house_number")), //
					@AttributeOverride(name = "addressLine2", column = @Column(name = "street")) //
			})
	private EmployeeAddress addr;

	//	@OneToOne(cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY)
	//	private EmployeeTask task;

	public Employee() {
		this.tasks = new ArrayList<EmployeeTask>();

	}

	public Employee(String fname, String lname, int salary) {
		this.firstName = fname;
		this.lastName = lname;
		this.salary = salary;
	}

	@Column(name = "col1")
	private int col1;

	public int getCol1() {
		return col1;
	}

	public void setCol1(int col1) {
		this.col1 = col1;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	//	public EmployeeTask getTask() {
	//		return task;
	//	}
	//
	//	public void setTask(EmployeeTask task) {
	//		this.task = task;
	//	}
}