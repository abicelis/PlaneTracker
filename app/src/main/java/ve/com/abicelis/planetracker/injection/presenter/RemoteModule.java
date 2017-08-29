package ve.com.abicelis.planetracker.injection.presenter;


import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.data.remote.FlightawareApi;
import ve.com.abicelis.planetracker.injection.application.ApplicationScope;

/**
 * Created by abicelis on 29/8/2017.
 */

@Module
@ApplicationScope
public class RemoteModule {

    private static final String FLIGHTAWARE_USER = "FLIGHTAWARE_USER";
    private static final String FLIGHTAWARE_API_KEY = "FLIGHTAWARE_API_KEY";
    private static final String FLIGHTAWARE_BASE_URL = "FLIGHTAWARE_BASE_URL";
    private static final String FLIGHTAWARE_RETROFIT = "FLIGHTAWARE_RETROFIT";


    /**
     * INJECTION GRAPH FOR FLIGHTAWARE FlightXML 3
     */

    @Provides
    Converter.Factory provideGsonConverter() {
        return GsonConverterFactory.create();
    }


    @Provides
    @Named(FLIGHTAWARE_USER)
    String provideFlightawareUserString() {return Constants.FLIGHTAWARE_USER;}

    @Provides
    @Named(FLIGHTAWARE_API_KEY)
    String provideFlightawarePasswordString() {return Constants.FLIGHTAWARE_API_KEY;}

    @Provides
    @Named(FLIGHTAWARE_BASE_URL)
    String provideFlightawareBaseUrlString() {return Constants.FLIGHTAWARE_BASE_URL;}

    @Provides
    @Named(FLIGHTAWARE_RETROFIT)
    Retrofit provideFlightawareRetrofit(Converter.Factory converter,
                                        @Named(FLIGHTAWARE_BASE_URL) String baseUrl,
                                        @Named(FLIGHTAWARE_USER) String user,
                                        @Named(FLIGHTAWARE_API_KEY) String key) {

        OkHttpClient.Builder client = new OkHttpClient().newBuilder();

        client.authenticator((Route route, Response response) -> {
            String credential = Credentials.basic(user, key);
            return response.request().newBuilder().header("Authorization", credential).build();
        });


        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client.build())
                //.addConverterFactory(ScalarsConverterFactory.create())    To be able to quickly check the result and just use Call<String>, no parsing.
                .addConverterFactory(converter)
                .build();
    }

    @Provides
    FlightawareApi providesFlightawareApi(@Named(FLIGHTAWARE_RETROFIT) Retrofit retrofit) {
        return retrofit.create(FlightawareApi.class);
    }


}
