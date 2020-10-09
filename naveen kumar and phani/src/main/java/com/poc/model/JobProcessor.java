package com.poc.model;

import org.springframework.batch.item.ItemProcessor;

public class JobProcessor implements ItemProcessor<Employee, Employee> {

	@Override
	public Employee process(Employee emp) throws Exception {
		return emp;
	}

}
