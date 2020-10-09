package com.poc.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.poc.demo.model.Person;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

    @Autowired
    EntityManagerFactory emf;

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    DataSource dataSource;


    @Bean
    @StepScope
    public FlatFileItemReader<Person> flatFileItemReader() {
        FlatFileItemReader<Person> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("People.csv"));
        reader.setLinesToSkip(1);  //we specify that the first line has to be skipped as the first line is header.

        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(); //Tokenizer is utilized to split the line into tokens based on our delimiter.
        tokenizer.setNames("Id","JobTitle", "JobDescription","contry","state","availability","replyrate","payrate","experience","skills","language","jobtype");

        BeanWrapperFieldSetMapper<Person> fieldSetMapper = new BeanWrapperFieldSetMapper<>();//map each of the tokens into a model class based on BeanWrapperFieldSetMapper.
        fieldSetMapper.setTargetType(Person.class);

        lineMapper.setFieldSetMapper(fieldSetMapper);
        lineMapper.setLineTokenizer(tokenizer);
        reader.setLineMapper(lineMapper);

        return reader;
    }

    @Bean//specifies JpaItemWriter which persists the person model into database.
    public JpaItemWriter<Person> jpaItemWriter() {
        JpaItemWriter<Person> writer = new JpaItemWriter();
        writer.setEntityManagerFactory(emf);//JpaItemWriter uses the auto configured EntityManagerFactory to persist the model.
        return writer;
    }


    @Bean//we configure a single step flatFileJpaWriterStep which executes our reader and writer.
    public Job flowJob() {
        return jobBuilderFactory.get("flowJob")
                .incrementer(new RunIdIncrementer())//We provide a RunIdIncrementer to ensure that each execution of the job gets an unique instance. 
                                                    //This will help Spring to differentiate multiple executions of the same job even if rest of the job parameters are same.
                .start(flatFileJpaWriterStep())
                .next(jdbcStaxWriterStep())
                .next(staxFileWriterStep())
                .build();
    }

    private Step flatFileJpaWriterStep() {
        return stepBuilderFactory.get("flatFileJpaWriterStep")
                .<Person, Person>chunk(1)
                .reader(flatFileItemReader())
                .writer(jpaItemWriter())
                .build();
    }

    private Step jdbcStaxWriterStep() {
        return stepBuilderFactory.get("jdbcStaxWriterStep")
                .<Person, Person>chunk(100)
                .reader(jdbcCursorItemReader())
                .writer(personStaxEventItemWriter())
                .build();
    }

    //contains the reader as jdbcCursorItemReader and personStaxEventItemWriter to run in sequence.
    private Step staxFileWriterStep() {
        return stepBuilderFactory.get("staxFileWriterStep")
                .<Person, Person>chunk(100)
                .reader(personStaxEventItemReader())
                .writer(flatFileItemWriter())
                .build();
    }


    @Bean
    public JdbcCursorItemReader<Person> jdbcCursorItemReader() {
        JdbcCursorItemReader<Person> personJdbcCursorItemReader = new JdbcCursorItemReader<>();
        personJdbcCursorItemReader.setSql("select * from person");
        personJdbcCursorItemReader.setDataSource(dataSource);
        personJdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<>(Person.class));//We specify BeanPropertyRowMapper to set the values for Person class.

        return personJdbcCursorItemReader;
    }

    @Bean
    public StaxEventItemWriter<Person> personStaxEventItemWriter() {
        StaxEventItemWriter<Person> staxEventItemWriter = new StaxEventItemWriter<>();
        staxEventItemWriter.setResource(new FileSystemResource("src/main/resources/people.xml"));
        staxEventItemWriter.setRootTagName("People");
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();//We specify Jaxb2Marshaller to use our Person model as the class to be used for marshalling to XML.

        marshaller.setClassesToBeBound(Person.class);
        staxEventItemWriter.setMarshaller(marshaller);
        return staxEventItemWriter;
    }

    @Bean
    public StaxEventItemReader<Person> personStaxEventItemReader() {
        StaxEventItemReader<Person> staxEventItemReader = new StaxEventItemReader<>();
        staxEventItemReader.setResource(new FileSystemResource("src/main/resources/people.xml"));
        staxEventItemReader.setFragmentRootElementName("Person");
        Jaxb2Marshaller unMarshaller = new Jaxb2Marshaller();
        unMarshaller.setClassesToBeBound(Person.class);
        staxEventItemReader.setUnmarshaller(unMarshaller);
        return staxEventItemReader;
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Person> flatFileItemWriter() {
        FlatFileItemWriter<Person> flatFileItemWriter = new FlatFileItemWriter<>();
        flatFileItemWriter.setShouldDeleteIfExists(true);
        flatFileItemWriter.setResource(new FileSystemResource("src/main/resources/modified_people.txt"));
        flatFileItemWriter.setLineAggregator((person) -> {
            return person.getId() + ":" + person.getJobTitle()+":"+person.getJobDescription()+":"+person.getCountry()+":"
        +person.getState()+":"+person.getAvailability()+":"+person.getReplyRate()+":"+person.getPayRate()+":"+person.getExperience()+":"
        +person.getSkills() +":"+person.getLanguage()+":"+person.getJobType();
        });
        return flatFileItemWriter;
    }


}
