package com.poc.cfg;

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

import com.poc.model.Employee;
import com.poc.model.JobProcessor;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private DataSource dataSource;

	@Bean
	public JdbcBatchItemWriter<Employee> xmlwriter() {
		JdbcBatchItemWriter<Employee> writer = new JdbcBatchItemWriter<Employee>();
		writer.setDataSource(dataSource);
		writer.setSql("insert into employee(job_id,name) values(:jobId,:name)");
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Employee>());
		return writer;
	}

	@Bean
	public Job CsvToXml() {
		return jobBuilderFactory.get("CsvToXmlJob").flow(step1()).next(step2()).end().build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Employee, Employee>chunk(10).reader(reader()).writer(writer())
				.processor(processor()).build();
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2").<Employee, Employee>chunk(10).reader(reader()).writer(xmlwriter())
				.build();
	}

	@Bean
	public JobProcessor processor() {
		return new JobProcessor();

	}

	@Bean
	public FlatFileItemReader<Employee> reader() {
		FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
		/*
		 * String[] s = new String[Employee.class.getDeclaredFields().length];
		 * int i = 0; for (java.lang.reflect.Field f :
		 * Employee.class.getDeclaredFields()) { s[i++] = f.getName();
		 * System.out.println(f.getName()); }
		 */
		reader.setResource(new ClassPathResource("Employee.csv"));
		reader.setLineMapper(new DefaultLineMapper<Employee>() {
			{
				setFieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {
					{
						setTargetType(Employee.class);
					}
				});
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames(new String[] { "jobId", "name" });
						// setNames(s);
					}
				});
			}
		});
		return reader;
	}

	/*
	 * StaxEventItemWriter is an implementation of ItemWriter which uses StAX
	 * and Marshaller for serializing object to XML
	 */
	@Bean
	public StaxEventItemWriter<Employee> writer() {
		StaxEventItemWriter<Employee> writer = new StaxEventItemWriter<>();
		writer.setResource(new FileSystemResource("job.xml"));
		writer.setMarshaller(jobUnmarshaller());
		writer.setRootTagName("job");
		return writer;
	}

	/* unmarshal XML from external sources */
	@Bean
	public XStreamMarshaller jobUnmarshaller() {
		XStreamMarshaller unMarshaller = new XStreamMarshaller();
		Map<String, Class> aliases = new HashMap<String, Class>();
		aliases.put("job", Employee.class);
		unMarshaller.setAliases(aliases);
		return unMarshaller;
	}
}
