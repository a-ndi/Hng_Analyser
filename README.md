Analyser API

A Spring Boot REST API that analyses strings, stores them in a database, and allows querying based on string properties or natural language descriptions.

**Features**

**Analyse Strings**
Accepts a string and calculates:

- Length

- Palindrome check

- Word count

- Unique characters

- Character frequency map

- SHA-256 hash

**Retrieve Strings**

- By exact value

**Filtered by properties (length, palindrome, word count, contains character)**

**Filtered using natural language queries**

**Delete Strings**

- Delete by value
  

**Error Handling**

- Returns meaningful HTTP status codes and JSON error messages

- 404 if string does not exist

- 422 if input value is empty or invalid

**API Endpoints**
1. Analyse a String
POST /api/analyse
Content-Type: application/json

{
  "value": "jay"
}


Responses:

- 201 Created – Returns the analysed string object

- 422 Unprocessable Entity – If "value" is empty or not a string

2. Get String by Value
GET /api/analyse/{stringValue}


Responses:

- 200 OK – Returns the analysed string object

- 404 Not Found – If string does not exist

3. Get All Strings (with optional filters)
GET /api/getValues?is_palindrome=true&min_length=3&contains_character=a


- Query Parameters (all optional):

- is_palindrome – Boolean

- min_length – Minimum string length

- max_length – Maximum string length

- word_count – Exact word count

- contains_character – Strings containing this character

Responses:

- 200 OK – Returns filtered list of strings with applied filters and count



4. Filter Strings Using Natural Language
GET /api/filter-by-natural-language?query=all single word palindromic strings


Responses:

- 200 OK – Returns strings matching the interpreted query

5. Delete String by Value
DELETE /api/analyse/{stringValue}


Responses:

- 204 No Content – Successfully deleted

- 404 Not Found – If string does not exist







Technologies Used

- Java 17

- Spring Boot

- Spring Data JPA

- Hibernate

- H2 / MySQL (configurable)

- Maven



Setup Instructions

1. Clone the repository: git clone <repo-url> cd analyser-api
2. Configure database in application.properties:


spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url = jdbc:mysql://localhost:3306/springbook
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.generate-ddl=true
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQLDialect


Build and run:

mvn clean install
mvn spring-boot:run


Access API at: http://localhost:8082/api


<img width="1052" height="455" alt="image" src="https://github.com/user-attachments/assets/d4e1c2e0-a212-48e0-94a8-f76a1ca87366" />
