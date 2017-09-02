package ve.com.abicelis.planetracker.data.model.flightaware;

/**
 * Created by abicelis on 29/8/2017.
 */

public class AirlineFlightSchedulesFlights {
    private String ident;           //Flight Id                 Ex: CMP713
    private String fa_ident;        //FlightAware flight id     Ex: CMP713-1503827111-airline-0176
    private long departuretime;     //UNIX EPOCH (GMT)
    private long arrivaltime;       //UNIX EPOCH (GMT)
    private String origin;          //ICAO code for airport, not IATA. So thats SVMC for Maracaibo airport, not MAR
    private String destination;     //ICAO code for airport, not IATA.
    private String aircrafttype;    //Model of aircraft
    private String meal_service;    //Meal service


    public String getIdent() {
        return ident;
    }

    public String getFaIdent() {return fa_ident;}

    public long getDeparturetime() {
        return departuretime;
    }

    public long getArrivaltime() {
        return arrivaltime;
    }

    public String getOrigin() {return origin;}

    public String getDestination() {
        return destination;
    }

    public String getAircraftType() {return aircrafttype;}

    public String getMealService() {return meal_service;}

    @Override
    public String toString() {
        return    "ident= " + ident
                + ", fa_ident= " + fa_ident
                + ", departuretime= " + departuretime
                + ", arrivaltime= " + arrivaltime
                + ", origin= " + origin
                + ", destination= " + destination
                + ", aircrafttype= " + aircrafttype
                + ", meal_service= " + meal_service;
    }
}
