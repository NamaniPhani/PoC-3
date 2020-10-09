package com.poc.demo.model;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@Entity
@XmlRootElement
public class Person {
	@Id

	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private String jobTitle;

	private String jobDescription;

	private String country;

	private String state;

	private String availability;

	private Integer replyRate;

	private Integer payRate;

	private Integer experience;

	private String skills;

	private String language;

	private String jobType;

}
