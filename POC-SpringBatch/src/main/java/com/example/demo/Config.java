package com.example.demo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.example.demo.emp.Employee;

@Configuration
@EnableBatchProcessing
public class Config {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private DataSource datasource;

	@Bean
	public XStreamMarshaller empMarshller() {

		XStreamMarshaller marshaller = new XStreamMarshaller();
		Map<String, Class> map = new HashMap();
		map.put("employee", Employee.class);
		marshaller.setAliases(map);

		return marshaller;

	}

	@Bean
	public StaxEventItemWriter<Employee> writer() {
		StaxEventItemWriter<Employee> emp = new StaxEventItemWriter<>();
		emp.setMarshaller(empMarshller());
		emp.setResource(new FileSystemResource("NewFile.xml"));
		emp.setRootTagName("employees");
		return emp;

	}

	@Bean
	public FlatFileItemReader<Employee> reader() {
		FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource("sample.csv"));
		DefaultLineMapper<Employee> dlm = new DefaultLineMapper<>();
		BeanWrapperFieldSetMapper<Employee> fsm = new BeanWrapperFieldSetMapper<>();
		DelimitedLineTokenizer dlmt = new DelimitedLineTokenizer();
		Field[] fields = Employee.class.getDeclaredFields();
		String s[] = new String[fields.length];
		int i = 0;
		for (Field f : fields) {
			s[i++] = f.getName();
		}
		dlmt.setNames(s);
		fsm.setTargetType(Employee.class);
		dlm.setFieldSetMapper(fsm);
		dlm.setLineTokenizer(dlmt);
		reader.setLineMapper(dlm);
		return reader;

	}

	@Bean
	public Job csvToXmlJob() {
		return jobBuilderFactory.get("csvToXmlJob").flow(step1()).next(step2()).end().build();
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2").<Employee, Employee>chunk(10).reader(reader()).writer(writerToDb())
				.processor(processor()).build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Employee, Employee>chunk(10).reader(reader()).writer(writer())
				.processor(processor()).build();
	}

	@Bean
	public EmpProcessor processor() {
		return new EmpProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Employee> writerToDb() {
		JdbcBatchItemWriter<Employee> writer = new JdbcBatchItemWriter<>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Employee>());
		writer.setSql("INSERT INTO employee(id,name,sal,city,phone) VALUES (:id,:name,:sal,:city,:phone)");
		writer.setDataSource(datasource);

		return writer;
	}

}
