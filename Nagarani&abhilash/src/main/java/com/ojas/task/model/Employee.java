package com.ojas.task.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "emp")
public class Employee {
	@Id
	private Integer id;
	private String name;
	private Long salary;
	private String compName;

	public Employee(Integer id, String name, Long salary, String compName) {
		super();
		this.id = id;
		this.name = name;
		this.salary = salary;
		this.compName = compName;
	}

	public Employee() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSalary() {
		return salary;
	}

	public void setSalary(Long salary) {
		this.salary = salary;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	@Override
	public String toString() {
		return "Employee [Id=" + id + ", name=" + name + ", salary=" + salary + ", compName=" + compName + "]";
	}

}
