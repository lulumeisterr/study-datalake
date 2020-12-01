package br.com.fiap.dao;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DynamoDBManager {

	private static DynamoDBMapper mapper;

	static {
		
		AmazonDynamoDB ddb = null;
		final String endpoint = System.getenv("ENDPOINT_OVERRIDE");
		System.out.println("CONFIG Endpoint:" + endpoint);
        
        if (endpoint != null && !endpoint.isEmpty()) {
        	System.out.println("Entrou aqui no local");
        	EndpointConfiguration endpointConfiguration = new EndpointConfiguration(endpoint, "us-east-1");
        	ddb = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(endpointConfiguration).build();
        } else {
			System.out.println("Entrou aqui no ambiente normal");
        	ddb = AmazonDynamoDBClientBuilder.defaultClient();
        }

		mapper = new DynamoDBMapper(ddb);
	}

	public static DynamoDBMapper mapper() {
		return DynamoDBManager.mapper;
	}

}
