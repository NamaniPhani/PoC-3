package com.poc.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Entity
@Data
@XmlRootElement(name = "Person")
public class Person1 {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)

	private int id;
	private String lastName;
	private String firstName;

}
