package br.com.fiap.handler;

import br.com.fiap.dao.TripRepository;
import br.com.fiap.model.HandlerRequest;
import br.com.fiap.model.HandlerResponse;
import br.com.fiap.model.Trip;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class CreateTripsRecord implements RequestHandler<HandlerRequest, HandlerResponse> {

	private final TripRepository repository = new TripRepository();

	@Override
	public HandlerResponse handleRequest(final HandlerRequest request, final Context context) {
		Trip trip = null;
		System.out.println(CreateTripsRecord.class.getName() + ": handling request {{ " + request.getBody() + " }}");
		try {
			trip = new ObjectMapper().readValue(request.getBody(), Trip.class);
		} catch (IOException e) {
			e.printStackTrace();
			return HandlerResponse.builder().setStatusCode(400).setRawBody("There is a error in your Trip!").build();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
			return HandlerResponse.builder().setStatusCode(500).setRawBody("{'error': '"+e.getMessage()+"'}").build();
		}
		context.getLogger().log("Creating a new trip record for the Country " + trip.getCountry()+".\n");
		final Trip tripRecorded = repository.save(trip);
		return HandlerResponse.builder().setStatusCode(201).setObjectBody(tripRecorded).build();
	}
}
