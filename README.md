## AWS SAM Application for Managing Study Data Lake

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
      -> vi .aws/credentials

2. Start DynamoDB Local in a Docker container. `docker run -p 8000:8000 -v $(pwd)/local/dynamodb:/data/ amazon/dynamodb-local -jar DynamoDBLocal.jar -sharedDb -dbPath /data`

3. Create the DynamoDB table. 
      -> https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_CreateTable.html
      -> Commands

aws dynamodb create-table --cli-input-json file://mydb.json --endpoint-url http://localhost:8000

Create a jsonfile mydb:

      {
       "TableName": "Trip",
       "BillingMode": "PAY_PER_REQUEST",
       "KeySchema": [
         {
           "AttributeName": "Country",
           "KeyType": "HASH"
         },
         {
           "AttributeName": "Date",
           "KeyType": "RANGE"
         }
       ],
       "AttributeDefinitions": [
         {
           "AttributeName": "Country",
           "AttributeType": "S"
         },
         {
           "AttributeName": "City",
           "AttributeType": "S"
         },
         {
           "AttributeName": "Date",
           "AttributeType": "S"
         },
         {
           "AttributeName": "Reason",
           "AttributeType": "S"
         }
       ],
       "ProvisionedThroughput": {
         "WriteCapacityUnits": 5,
         "ReadCapacityUnits": 5
       },
       "LocalSecondaryIndexes": [
         {
           "IndexName": "cityIndex",
           "KeySchema": [
             {
               "AttributeName": "Country",
               "KeyType": "HASH"
             },
             {
               "AttributeName": "City",
               "KeyType": "RANGE"
             }
           ],
           "Projection": { 
                 "ProjectionType": "ALL"
              }
       },

       {
           "IndexName": "reasonIndex",
           "KeySchema": [
             {
               "AttributeName": "Country",
               "KeyType": "HASH"
             },
             {
               "AttributeName": "Reason",
               "KeyType": "RANGE"
             }
           ],
           "Projection": { 
                 "ProjectionType": "ALL"
              }
       }

      ]

      }


If the table already exist, you can delete: `aws dynamodb delete-table --table-name study --endpoint-url http://localhost:8000`

3. Start the SAM local API.
 - On a Mac: `sam local start-api --env-vars src/test/resources/test_environment_mac.json`
 - On Windows: `sam local start-api --env-vars src/test/resources/test_environment_windows.json`
 - On Linux: `sam local start-api --env-vars src/test/resources/test_environment_linux.json`
 
 OBS:  If you already have the container locally (in your case the java8), then you can use --skip-pull-image to remove the download

If the previous command ran successfully you should now be able to hit the following local endpoint to
invoke the functions rooted at `http://localhost:3000/study/{topic}?starts=2020-01-02&ends=2020-02-02`.
It shoud return 404. Now you can explore all endpoints, use the src/test/resources/Study DataLake.postman_collection.json to import a API Rest Collection into Postman.

**SAM CLI** is used to emulate both Lambda and API Gateway locally and uses our `template.yaml` to
understand how to bootstrap this environment (runtime, where the source code is, etc.) - The
following excerpt is what the CLI will read in order to initialize an API and its routes:


## Packaging and deployment

AWS Lambda Java runtime accepts either a zip file or a standalone JAR file - We use the latter in
this example. SAM will use `CodeUri` property to know where to look up for both application and
dependencies:

Firstly, we need a `S3 bucket` where we can upload our Lambda functions packaged as ZIP before we
deploy anything - If you don't have a S3 bucket to store code artifacts then this is a good time to
create one:

```bash
export BUCKET_NAME=my_cool_new_bucket
aws s3 mb s3://$BUCKET_NAME
```

Next, run the following command to package our Lambda function to S3:

```bash
sam package \
    --template-file template.yaml \
    --output-template-file packaged.yaml \
    --s3-bucket $BUCKET_NAME
```

Next, the following command will create a Cloudformation Stack and deploy your SAM resources.

```bash
sam deploy \
    --template-file packaged.yaml \
    --stack-name study-datalake \
    --capabilities CAPABILITY_IAM
```

> **See [Serverless Application Model (SAM) HOWTO Guide](https://github.com/awslabs/serverless-application-model/blob/master/HOWTO.md) for more details in how to get started.**

After deployment is complete you can run the following command to retrieve the API Gateway Endpoint URL:

```bash
aws cloudformation describe-stacks \
    --stack-name sam-orderHandler \
    --query 'Stacks[].Outputs'
```

# Appendix

## AWS CLI commands

AWS CLI commands to package, deploy and describe outputs defined within the cloudformation stack:

```bash
sam package \
    --template-file template.yaml \
    --output-template-file packaged.yaml \
    --s3-bucket REPLACE_THIS_WITH_YOUR_S3_BUCKET_NAME

sam deploy \
    --template-file packaged.yaml \
    --stack-name sam-orderHandler \
    --capabilities CAPABILITY_IAM \
    --parameter-overrides MyParameterSample=MySampleValue

aws cloudformation describe-stacks \
    --stack-name sam-orderHandler --query 'Stacks[].Outputs'
```

## Bringing to the next level

Next, you can use the following resources to know more about beyond hello world samples and how others
structure their Serverless applications:

* [AWS Serverless Application Repository](https://aws.amazon.com/serverless/serverlessrepo/)
