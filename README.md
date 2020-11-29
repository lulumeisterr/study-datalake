## AWS Serverless Cloud Native Java RESTful API

Develop an API following the best practices in the set of
principles of REST (State Representation Transfer) architecture.

The travel entity must contain the attributes: Country, City, Date (YYYY / MM / DD) and Reason.

o Create a new trip record:
- HTTP return code 201


o Get trips by period:
- Via query / travel parameter? Start = X and end = Y
- Return 200 containing the results in a list.
- Even when nothing is found, return an empty list and
HTTP code 200.
- Returns all fields of the entity in the body.


o get trips by country:
- Via parameter, inform the country / trips / <Country>
- Return only one trip from the country.
- HTTP Code 200 case found, body not found
empty and HTTP Code 404.


enjoy city trips:
- Via Consulta inform the city and via Path parameter inform the
country / trips / <Country> /? city ​​= <City>
- The city search for being of type contains (like - LSI Sort
Indexing key)
- Return only one trip from the country.
- HTTP Code 200 case found, body not found
empty and HTTP Code 404.

• No API source code should contain a README file with
instructions for running an API locally and also on AWS.

## Requirements

* AWS CLI already configured with at least PowerUser permission
* [Java SE Development Kit 8 installed](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Docker installed](https://www.docker.com/community-edition)
* [Maven](https://maven.apache.org/install.html)
* [SAM CLI](https://github.com/awslabs/aws-sam-cli)
* [Python 3](https://docs.python.org/3/)

## Setup process

### Installing dependencies

We use `maven` to install our dependencies and package our application into a JAR file:

```bash
mvn install
```

### Local development

**Invoking function locally through local API Gateway**

1. Update AWS credentials
      vi .aws/credentials

2. Start DynamoDB Local in a Docker container. `docker run -p 8000:8000 -v $(pwd)/local/dynamodb:/data/ amazon/dynamodb-local -jar DynamoDBLocal.jar -sharedDb -dbPath /data`

3. - Use this command to list the table        
      
            aws dynamodb list-tables --endpoint-url http://localhost:8000

4. Create the DynamoDB table.

   Documentation: https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_CreateTable.html
      
   You can use :
   
       aws dynamodb create-table --table-name trip --attribute-definitions AttributeName=country,AttributeType=S AttributeName=date,AttributeType=S AttributeName=city,AttributeType=S AttributeName=reason,AttributeType=S --key-schema AttributeName=country,KeyType=HASH AttributeName=date,KeyType=RANGE --local-secondary-indexes 'IndexName=cityIndex,KeySchema=[{AttributeName=country,KeyType=HASH},{AttributeName=city,KeyType=RANGE}],Projection={ProjectionType=ALL}' 'IndexName=reasonIndex,KeySchema=[{AttributeName=country,KeyType=HASH},{AttributeName=reason,KeyType=RANGE}],Projection={ProjectionType=ALL}' --billing-mode PAY_PER_REQUEST --endpoint-url http://localhost:8000
   
   Or
     
   Use this command inside the project folder:

         aws dynamodb create-table --cli-input-json file://dbDynamo.json --endpoint-url http://localhost:8000

5. If You want to create the table using the (http://localhost:8000/shell/#)
      
      - past file dbDynamo.json and run
                  
      
If the table already exist, you can delete: 
      `aws dynamodb delete-table --table-name trip --endpoint-url http://localhost:8000`

6. Start the SAM local API.
 - On a Mac: `sam local start-api --env-vars src/test/resources/test_environment_mac.json`
 - On Windows: `sam local start-api --env-vars src/test/resources/test_environment_windows.json`
 - On Linux: `sam local start-api --env-vars src/test/resources/test_environment_linux.json`
 
 OBS:  If you already have the container locally (in your case the java8), then you can use --skip-pull-image to remove the download


7. Urls

  - Create a trip
      POST -> http://localhost:3000/trip/
            
            Body :
            
                    {
                      "country": "brasil",
                      "date": "2020-12-01T00:0045Z",
                      "city": "osasco",
                      "reason": "false"
                     }
      
  - Get a country
      GET -> http://localhost:3000/trip/country/{nameofCountry}
      
  - Find by city
  
      GET -> http://localhost:3000/trip/country/{country}/{city}
