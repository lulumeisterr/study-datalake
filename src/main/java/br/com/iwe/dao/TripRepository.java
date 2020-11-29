package br.com.iwe.dao;
import br.com.iwe.model.Trip;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;

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


}