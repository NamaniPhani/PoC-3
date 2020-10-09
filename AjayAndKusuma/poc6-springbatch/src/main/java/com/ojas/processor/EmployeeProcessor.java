package com.ojas.processor;

import org.springframework.batch.item.ItemProcessor;

import com.ojas.model.Employee;

public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {

	public Employee process(Employee item) throws Exception {
		return item;
	}

}
