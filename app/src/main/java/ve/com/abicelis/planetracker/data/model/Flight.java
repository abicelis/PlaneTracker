package ve.com.abicelis.planetracker.data.model;

/**
 * Created by abicelis on 26/8/2017.
 */

public class Flight {

    private String callsign;                //CMP317
    private String operatorName;            //Copa Airlines     TODO maybe change this operator to an Operator object, with a code, a friendly name and  an id...
    private String operatorCode;            //CMP
    private String depatureAirportCode;     //MAR               TODO maybe change these strings to Airport objects? With a firnedly name, code, and id for the airport
    private String arrivalAirportCode;      //PTY
    private String aircraftModel;           //Boeing 737NG 8V3/W

    private double latitude;
    private double longitude;
    private int altitude;                   //In meters
    private float speed;                    //In knots
    private float verticalSpeed;            //In ft/m
    private float heading;                  //In degrees
}
