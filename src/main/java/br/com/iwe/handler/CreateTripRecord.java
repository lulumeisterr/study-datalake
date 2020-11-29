package br.com.iwe.handler;

import br.com.iwe.dao.TripRepository;
import br.com.iwe.model.HandlerRequest;
import br.com.iwe.model.HandlerResponse;
import br.com.iwe.model.Trip;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class CreateTripRecord implements RequestHandler<HandlerRequest, HandlerResponse> {
	
	private final TripRepository repository = new TripRepository();

	@Override
	public HandlerResponse handleRequest(final HandlerRequest request, final Context context) {
		Trip trip = null;
		try {
			trip = new ObjectMapper().readValue(request.getBody(), Trip.class);
		} catch (IOException e) {
			e.printStackTrace();
			return HandlerResponse.builder().setStatusCode(400).setRawBody("There is a error in your Trip!").build();
		}catch(ResourceNotFoundException e){
			e.printStackTrace();
		}
		context.getLogger().log("Creating a new trip record for the Country " + trip.getCountry());
		final Trip studyRecorded = repository.save(trip);
		return HandlerResponse.builder().setStatusCode(201).setObjectBody(studyRecorded).build();
	}
}