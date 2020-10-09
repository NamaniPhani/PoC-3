package com.ojas.jobs;

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

import com.ojas.model.Employee;
import com.ojas.processor.EmployeeProcessor;
 
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	@Autowired
	  private JobBuilderFactory jobBuilderFactory;
	  @Autowired
	  private StepBuilderFactory stepBuilderFactory;
	  @Autowired
	  DataSource datasource;
	 
	 
	  @Bean
	  public Job CsvToXmlJob() {
	    return jobBuilderFactory.get("CsvToXmlJob").flow(step1()).next(step2()).end().build();
	  }
	 
	  @Bean
	  public Step step1() {
	    return stepBuilderFactory.get("step1").<Employee, Employee>chunk(10).reader(reader())
	        .writer(writer()).processor(processor()).build();
	  }
	 
	  @Bean
	  public EmployeeProcessor processor() {
	    return new EmployeeProcessor();
	  }
	 
	  @Bean
	  public FlatFileItemReader<Employee> reader() {
	    FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
	    reader.setResource(new ClassPathResource("employee.csv"));
	    reader.setLineMapper(new DefaultLineMapper<Employee>() {{
	      setFieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {{
	        setTargetType(Employee.class);
	      }});
	      setLineTokenizer(new DelimitedLineTokenizer() {{
	        setNames(new String[]{"id", "name", "city"});
	      }});
	    }});
	    return reader;
	  }
	 
	  @Bean
	  public StaxEventItemWriter<Employee> writer() {
	    StaxEventItemWriter<Employee> writer = new StaxEventItemWriter<>();
	    writer.setResource(new FileSystemResource("employee.xml"));
	    writer.setMarshaller(studentUnmarshaller());
	    writer.setRootTagName("employees");
	    return writer;
	  }
	 
	  @Bean
	  public XStreamMarshaller studentUnmarshaller() {
	    XStreamMarshaller unMarshaller = new XStreamMarshaller();
	    Map<String, Class> aliases = new HashMap<String, Class>();
	    aliases.put("employee", Employee.class);
	    unMarshaller.setAliases(aliases);
	    return unMarshaller;
	  }
	  @Bean
	    public JdbcBatchItemWriter<Employee> writerToDb() {
	        JdbcBatchItemWriter<Employee> writer = new JdbcBatchItemWriter<>();
	        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Employee>());
	        writer.setSql("INSERT INTO Employee(id,name,city) VALUES(:id,:name,:city)");
	        writer.setDataSource(datasource);
	        return writer;
	    }
	  @Bean
	    public Step step2() {
	        return stepBuilderFactory.get("step2").<Employee, Employee>chunk(10).reader(reader()).writer(writerToDb())
	                .processor(processor()).build();
	    }
	}