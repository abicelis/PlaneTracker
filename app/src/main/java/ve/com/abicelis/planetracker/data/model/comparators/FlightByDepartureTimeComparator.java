package ve.com.abicelis.planetracker.data.model.comparators;

import java.util.Comparator;

import ve.com.abicelis.planetracker.data.model.Flight;

/**
 * Created by abicelis on 22/9/2017.
 */

public class FlightByDepartureTimeComparator implements Comparator<Flight> {
    @Override
    public int compare(Flight flight, Flight flight2) {
        if(flight == null)
            return 1;
        if (flight2 == null)
            return -1;

        return (flight.getDeparture().getTimeInMillis() > flight2.getDeparture().getTimeInMillis() ? 1 : -1);
    }
}
