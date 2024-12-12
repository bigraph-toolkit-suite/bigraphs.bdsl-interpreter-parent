# BDSL Execution Module

This module provides an abstract execution framework for BDSL statements to specify and manage the overall BDSL program execution.
It is implemented by the `bdsl-interpreter-cli` module.

It basically provides the whole logic for reading, processing/executing and "writing" BDSL statements.
It is implemented using the Spring Batch processing engine.

**Some Features:**

- BDSL document can also be in a database
    - write new datasource and "repository loader"

- flow batch processing can be specified

- provide configuration file to configure the processing/execution steps

- chunk size what do read/process/write can be conveniently specified

## Basic Usage

This section explores some of the possible usages of this module.

### Via Annotations

The following listing shows a minimal working example:

```java
@SpringBootApplication
@Import(DefaultBatchConfiguration.class)) // <- import specific configuration
public class Application implements CommandLineRunner {
    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    @Qualifier("defaultStatementProcessingJob")
    Job job; // acquire the default job

    @Override
    public void run(String... args) throws Exception {
        try {
            JobExecution execution = jobLauncher.run(job, new JobParameters());
            System.out.println("Job Status : " + execution.getStatus());
            System.out.println("Job completed");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Job failed");
        }
    }
}
```

## Configuration

Takes place in subclasses of `AbstractBatchConfigurer` which implement the necessary beans.

What can be configured? For example,
- Steps
- Chunksize
- Flow
- Data source and writer

### Adjusting Parameters

- Provide them via the bdsl-execution.properties file

### Changing the General Behavior

- extend `AbstractBatchConfigurer` and implement all necessary methods
- extend a concrete implementation and override methods

## Parallel Steps and Remote Chunking

- to improve the throughput for statements to process, when processing is more expensive than the reading of BDSL statements

- see https://docs.spring.io/spring-batch/docs/current/reference/html/scalability.html#scalabilityParallelSteps

- see https://docs.spring.io/spring-batch/docs/current/reference/html/spring-batch-integration.html#remote-chunking


when I/O is the bottleneck use remote partitioning (not needed however).