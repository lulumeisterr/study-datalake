package br.com.iwe.handler;

import br.com.iwe.dao.TripRepository;
import br.com.iwe.model.HandlerRequest;
import br.com.iwe.model.HandlerResponse;
import br.com.iwe.model.Trip;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.List;

public class GetTripsByCity implements RequestHandler<HandlerRequest, HandlerResponse> {

    private final TripRepository repository = new TripRepository();

    @Override
    public HandlerResponse handleRequest(HandlerRequest request, Context context) {

        final String country = request.getPathParameters().get("country");
        final String city = request.getQueryStringParameters().get("city");

        context.getLogger().log("Searching for registered trip for " + country + " and city equals " + city);

        final List<Trip> citys = this.repository.findByCity(country, city);

        if (citys == null || citys.isEmpty()) {
            return HandlerResponse.builder().setStatusCode(404).build();
        }

        return HandlerResponse.builder().setStatusCode(200).setObjectBody(citys).build();

    }
}
