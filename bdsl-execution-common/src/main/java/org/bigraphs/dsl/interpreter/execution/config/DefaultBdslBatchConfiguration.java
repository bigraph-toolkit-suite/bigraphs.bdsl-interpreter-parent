package org.bigraphs.dsl.interpreter.execution.config;

import org.bigraphs.dsl.interpreter.execution.jobs.processor.BaseBdslStatementProcessor;
import org.bigraphs.dsl.interpreter.execution.jobs.processor.DefaultBdslStatementProcessor;
import org.bigraphs.dsl.interpreter.execution.jobs.reader.BaseBdslItemReader;
import org.bigraphs.dsl.interpreter.execution.jobs.reader.DefaultBdslDocumentReader;
import org.bigraphs.dsl.interpreter.execution.jobs.writer.BaseBdslItemWriter;
import org.bigraphs.dsl.interpreter.execution.jobs.writer.NoopItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

//TODO make also abstract and put more methods to the parent class
@Configuration
@ComponentScan(basePackageClasses = {JobCompletionNotificationListener.class})
@EnableBatchProcessing
public class DefaultBdslBatchConfiguration extends AbstractBdslBatchConfigurer {

    @Value("${test:default}")
    private String name;

    @Bean
    public TaskExecutor taskExecutor() {
        return new SyncTaskExecutor();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public BaseBdslItemReader<Object> reader() {
        return new DefaultBdslDocumentReader();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public BaseBdslStatementProcessor<Object, Object> processor() {
        return new DefaultBdslStatementProcessor<>();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public BaseBdslItemWriter<Object> writer() {
        return new NoopItemWriter();
    }

    public String getStepName() {
        return "defaultStatementProcessingJob_Step1";
    }

    @Bean(name = "defaultStatementProcessingJob_Step1")
    public Step defaultStatementProcessingJob_Step1() {
        return stepBuilderFactory.get(getStepName())
                .<Object, Object>chunk(1)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .allowStartIfComplete(true)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean(name = "defaultStatementProcessingJob")
    public Job defaultStatementProcessingJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("defaultStatementProcessingJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
//                .preventRestart()
                .flow(defaultStatementProcessingJob_Step1())
                .end()
                .build();
    }
}
