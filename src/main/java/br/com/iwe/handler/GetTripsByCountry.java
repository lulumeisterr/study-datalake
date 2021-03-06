package br.com.iwe.handler;

import br.com.iwe.dao.TripRepository;
import br.com.iwe.model.HandlerRequest;
import br.com.iwe.model.HandlerResponse;
import br.com.iwe.model.Trip;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.List;

public class GetTripsByCountry implements RequestHandler<HandlerRequest, HandlerResponse> {

    private final TripRepository repository = new TripRepository();

    @Override
    public HandlerResponse handleRequest(HandlerRequest request, Context context) {

        final String country = request.getPathParameters().get("country");

        context.getLogger().log("Searching for registered trips for " + country);

        try {
            final List<Trip> trips = this.repository.findByCountry(country);
            if (trips == null || trips.isEmpty()) {
                return HandlerResponse.builder().setStatusCode(404).build();
            }
            return HandlerResponse.builder().setStatusCode(200).setObjectBody(trips).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}