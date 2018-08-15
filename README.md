# Example usage of @Where annotation
The goal of this sample project is to demonstrate the usage of Hibernate's @Where annotation in the context of a Spring Data JPA.

There are 2 examples :
* First one with the annotation directly on the entity.
* Second one with the annotation on a abstract parent class. This example demonstrate that the annotation has no effect on the queries.

This application was generated using JHipster 5.2.0, you can find documentation and help at [https://www.jhipster.tech/documentation-archive/v5.2.0](https://www.jhipster.tech/documentation-archive/v5.2.0).

## WhereAnnotationTest.java
This test class creates 2 entities of type MyLovelyEntity (with the @Where annotation), one with deleted=false, the other one with deleted=true.
After a request to the resource, the test verifies that only one record is returned.

## WhereAnnotationOnParentTest.java
This test class creates also 2 entities, but of type ChildEntity, having the @Where annotation on an abstract parent class.
After a request to the resource, the test verifies that both records are returned.

## Show SQL
The show_sql property is activated and you can see in the logs that the clause from @Where is added only when querying MyLovelyEntity


