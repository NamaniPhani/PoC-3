package com.example.demo;

import org.springframework.batch.item.ItemProcessor;

import com.example.demo.emp.Employee;

public class EmpProcessor implements ItemProcessor<Employee, Employee> {

	@Override
	public Employee process(Employee item) throws Exception {
		return item;
	}
}