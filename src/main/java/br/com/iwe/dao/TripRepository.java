package br.com.iwe.dao;

import br.com.iwe.model.Trip;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripRepository {

	private static final DynamoDBMapper mapper = DynamoDBManager.mapper();

	public Trip save(final Trip trip) {
		System.out.println("Entrou no Repository : " + trip.getCountry());
		try {
			mapper.save(trip);
		}catch(DynamoDBMappingException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return trip;
	}

	public List<Trip> findByCity(final String country, final String city) {

		System.out.println("Pais : "+ country);
		System.out.println("Cidade " + city);

		final Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":val1", new AttributeValue().withS(country));
		eav.put(":val2", new AttributeValue().withS(city));

		final DynamoDBQueryExpression<Trip> queryExpression = new DynamoDBQueryExpression<Trip>()
				.withIndexName("cityIndex").withConsistentRead(false)
				.withKeyConditionExpression("country = :val1 and city = :val2").withExpressionAttributeValues(eav);

		final List<Trip> trips = mapper.query(Trip.class, queryExpression);

		return trips;
	}

	public List<Trip> findByCountry(final String country) {

		final Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":val1", new AttributeValue().withS(country));

		final DynamoDBQueryExpression<Trip> queryExpression = new DynamoDBQueryExpression<Trip>()
				.withKeyConditionExpression("country = :val1").withExpressionAttributeValues(eav);

		final List<Trip> trips = mapper.query(Trip.class, queryExpression);

		return trips;

	}

	public List<Trip> findByPeriod(final String country, final String start, final String end) {

		final Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":val1", new AttributeValue().withS(country));
		eav.put(":val2", new AttributeValue().withS(start));
		eav.put(":val3", new AttributeValue().withS(end));

		final DynamoDBQueryExpression<Trip> queryExpression = new DynamoDBQueryExpression<Trip>()
				.withKeyConditionExpression("country = :val1 and dateTrip between :val2 and :val3")
				.withExpressionAttributeValues(eav);

		final List<Trip> trips = mapper.query(Trip.class, queryExpression);

		return trips;
	}



}