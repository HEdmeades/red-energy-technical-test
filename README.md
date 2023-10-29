# Henry Edmeades Technical Test Submission

## Summary
* Java 8
* Spring boot 2.7.17

I have tried to write this application as if it was going to a part of a bigger project. 
I've abstracted out the code as much as I can for re-usability as if the project would grow and have many contributors.
I have also implemented javax validation to the domain objects to firstly check the data is valid but also prepare this application
for a persistence layer.

I have also added a few redundant/unnecessary comments just to explain my thinking. These are the sort of comments I would be making during a pull review.

I extended the domain objects with a DomainObject class to implement generic validation. <br />
I have updated the domain objects to use Lombok to clean up the classes. <br />
I have created a series of files to use for testing (this to simulate business test cases and end-to-end testing of the application). <br />

## Questions & Assumptions
* Can I assume the file is a valid CSV?
  * Assumption: Yes.
* Can I assume the CSV won't have any trailing or leading empty spaces?
  * Assumption: Yes, assume the CSV is valid and formatted correctly.
* Can I assume the data will be valid?
  * Assumption: No. I have implemented validation methods to the domain object to validate this.
* Can I assume that the dates for each meter read / meter volume is unique?
  * Assumption: No. I have added a check to validate this.
* Can I assume that each meter volume will have a corresponding meter read?
  * Assumption: No. I have added a check to validate this. Although, I'm unable to validate if the data has a missing meter read for a series of meter volumes, where the file already has a meter read.

