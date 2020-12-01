package br.com.fiap.handler;
import br.com.fiap.dao.TripRepository;
import br.com.fiap.model.HandlerRequest;
import br.com.fiap.model.HandlerResponse;
import br.com.fiap.model.Trip;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.List;

public class GetTripsByPeriod implements RequestHandler<HandlerRequest, HandlerResponse> {

    private final TripRepository repository = new TripRepository();

    @Override
    public HandlerResponse handleRequest(HandlerRequest request, Context context) {

        final String country = request.getPathParameters().get("country");
        final String start = request.getQueryStringParameters().get("start");
        final String end = request.getQueryStringParameters().get("end");

        context.getLogger().log("Searching for registered trips starts  " + start + " and ends " + end);

        try {
            final List<Trip> trips = this.repository.findByPeriod(country, start, end);
            if (trips == null || trips.isEmpty()) {
                return HandlerResponse.builder().setStatusCode(200).build();
            }
            return HandlerResponse.builder().setStatusCode(200).setObjectBody(trips).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
