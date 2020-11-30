## AWS Serverless Cloud Native Java RESTful API

Develop an API following the best practices in the set of
principles of REST (State Representation Transfer) architecture.

Stack Java SE 8:

      • AWS SAM
      • Amazon API Gateway
      • AWS Lambda
      • Amazon DynamoDB

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
mvn clean install
```

### Local development

**Invoking function locally through local API Gateway**

1. Update AWS credentials
      vi .aws/credentials

#### Go to project local directory where is this repository:

1. Start DynamoDB Local in a Docker container.

```docker run -p 8000:8000 -v $(pwd)/local/dynamodb:/data/ amazon/dynamodb-local -jar DynamoDBLocal.jar -sharedDb -dbPath /data```

or using docker-compose file to up em down dynamoDB (use it for windows with wsl)

```docker-compose up ```

1. Set permissions for DynamoDB Local in a Docker container. `chmod 777 -R $(pwd)/local/dynamodb`

1. Create the DynamoDB table.

   Documentation: https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_CreateTable.html
      
   You can use :
   
       aws dynamodb create-table --table-name trip --attribute-definitions AttributeName=country,AttributeType=S AttributeName=dateTrip,AttributeType=S AttributeName=city,AttributeType=S AttributeName=reason,AttributeType=S --key-schema AttributeName=country,KeyType=HASH AttributeName=dateTrip,KeyType=RANGE --local-secondary-indexes 'IndexName=cityIndex,KeySchema=[{AttributeName=country,KeyType=HASH},{AttributeName=city,KeyType=RANGE}],Projection={ProjectionType=ALL}' 'IndexName=reasonIndex,KeySchema=[{AttributeName=country,KeyType=HASH},{AttributeName=reason,KeyType=RANGE}],Projection={ProjectionType=ALL}' --billing-mode PAY_PER_REQUEST --endpoint-url http://localhost:8000

  Or
     
   Use this command inside the project folder:

         aws dynamodb create-table --cli-input-json file://dbDynamo.json --endpoint-url http://localhost:8000
      
    - past file dbDynamo.json and run

1. If the table already exist, you can delete: 
      `aws dynamodb delete-table --table-name trip --endpoint-url http://localhost:8000`

1. Use this command to list all tables:
       `aws dynamodb list-tables --endpoint-url http://localhost:8000`

1. Use this command to see the content of table trip:
       `aws dynamodb scan --table-name trip --endpoint-url http://localhost:8000`

2. Start the SAM local API.
 - On a Mac: `sam local start-api --env-vars src/test/resources/test_environment_mac.json`
 - On Windows: `sam local start-api --env-vars src/test/resources/test_environment_windows.json`
 - On Linux: `sam local start-api --env-vars src/test/resources/test_environment_linux.json`
 
 OBS:  If you already have the container locally (in your case the java8), then you can use --skip-pull-image to remove the download


## Packaging and deployment

AWS Lambda Java runtime accepts either a zip file or a standalone JAR file - We use the latter in
this example. SAM will use `CodeUri` property to know where to look up for both application and
dependencies:

Firstly, we need a `S3 bucket` where we can upload our Lambda functions packaged as ZIP before we
deploy anything - If you don't have a S3 bucket to store code artifacts then this is a good time to
create one:

```shell script
export BUCKET_NAME=trip-bucket
aws s3 mb s3://$BUCKET_NAME
```
On Windows
```shell script
set BUCKET_NAME=trip-bucket
aws s3 mb s3://%BUCKET_NAME%
```

Next, run the following command to package our Lambda function to S3:

```shell script
sam package --template-file template.yaml --output-template-file packaged.yaml --s3-bucket $BUCKET_NAME
```

Next, the following command will create a Cloudformation Stack and deploy your SAM resources.

```shell script
sam deploy --template-file packaged.yaml \
    --stack-name trip-datalake \
    --capabilities CAPABILITY_IAM
```

> **See [Serverless Application Model (SAM) HOWTO Guide](https://github.com/awslabs/serverless-application-model/blob/master/HOWTO.md) for more details in how to get started.**

After deployment is complete you can run the following command to retrieve the API Gateway Endpoint URL:

```shell script
aws cloudformation describe-stacks \
    --stack-name trip-datalake \
    --query 'Stacks[].Outputs'
```

# Appendix

## AWS CLI commands

AWS CLI commands to package, deploy and describe outputs defined within the cloudformation stack:

*Remember to set your credentials (~/.aws/credentials)*

* Windows:
```shell script
set BUCKET_NAME=trip-bucket
set STACK_NAME=trip-datalake

aws s3 mb s3://$BUCKET_NAME

sam package ^
    --template-file template.yaml ^
    --output-template-file packaged.yaml ^
    --s3-bucket %BUCKET_NAME%

sam deploy ^
    --template-file packaged.yaml ^
    --capabilities CAPABILITY_IAM ^
    --stack-name %STACK_NAME%

aws cloudformation describe-stacks ^
    --stack-name %STACK_NAME% --query 'Stacks[].Outputs'

```
* Linux/Mac:
````shell script
export BUCKET_NAME=bucket_trip
export STACK_NAME=trip-datalake

aws s3 mb s3://$BUCKET_NAME

sam package ^
    --template-file template.yaml \
    --output-template-file packaged.yaml \
    --s3-bucket trip-bucket

sam deploy \
    --template-file packaged.yaml \
    --capabilities CAPABILITY_IAM \
    --stack-name trip-datalake

aws cloudformation describe-stacks \
    --stack-name trip-datalake --query 'Stacks[].Outputs'
```
