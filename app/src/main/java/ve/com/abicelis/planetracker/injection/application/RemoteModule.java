package ve.com.abicelis.planetracker.injection.application;


import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.data.remote.FlightawareApi;
import ve.com.abicelis.planetracker.data.remote.OpenFlightsApi;
import ve.com.abicelis.planetracker.data.remote.QwantApi;

/**
 * Created by abicelis on 29/8/2017.
 */

@Module
@ApplicationScope
public class RemoteModule {

    private static final String CONVERTER_SCALAR = "CONVERTER_SCALAR";
    private static final String CONVERTER_GSON = "CONVERTER_GSON";

    private static final String FLIGHTAWARE_USER = "FLIGHTAWARE_USER";
    private static final String FLIGHTAWARE_API_KEY = "FLIGHTAWARE_API_KEY";
    private static final String FLIGHTAWARE_BASE_URL = "FLIGHTAWARE_BASE_URL";
    private static final String FLIGHTAWARE_RETROFIT = "FLIGHTAWARE_RETROFIT";

    private static final String OPENFLIGHTS_BASE_URL = "OPENFLIGHTS_BASE_URL";
    private static final String OPENFLIGHTS_RETROFIT = "OPENFLIGHTS_RETROFIT";

    private static final String QWANT_BASE_URL = "QWANT_BASE_URL";
    private static final String QWANT_RETROFIT = "QWANT_RETROFIT";


    /**
     * Common
     */
    @Provides
    @Named(CONVERTER_SCALAR)    //Scalars Converter so Retrofit can return Strings
    Converter.Factory provideScalarConverter() {return ScalarsConverterFactory.create();}

    @Provides
    @Named(CONVERTER_GSON)
    Converter.Factory provideGsonConverter() {
        return GsonConverterFactory.create();
    }

    @Provides
    RxJava2CallAdapterFactory provideRxJavaFactory() {
        return RxJava2CallAdapterFactory.create();
    }





    /**
     * INJECTION GRAPH FOR FLIGHTAWARE FlightXML 3
     */
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
    Retrofit provideFlightawareRetrofit(@Named(CONVERTER_GSON) Converter.Factory converter,
                                        RxJava2CallAdapterFactory factory,
                                        @Named(FLIGHTAWARE_BASE_URL) String baseUrl,
                                        @Named(FLIGHTAWARE_USER) String user,
                                        @Named(FLIGHTAWARE_API_KEY) String key) {

        OkHttpClient.Builder client = new OkHttpClient().newBuilder();

        //Basic Auth
        client.authenticator((Route route, Response response) -> {
            String credential = Credentials.basic(user, key);
            return response.request().newBuilder().header("Authorization", credential).build();
        });

        // Logging
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        // add logging as last interceptor
        client.addInterceptor(logging);

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client.build())
                .addConverterFactory(converter)
                .addCallAdapterFactory(factory)
                .build();
    }

    @Provides
    FlightawareApi providesFlightawareApi(@Named(FLIGHTAWARE_RETROFIT) Retrofit retrofit) {
        return retrofit.create(FlightawareApi.class);
    }





    /**
     * INJECTION GRAPH FOR OPENFLIGHTS.ORG
     */
    @Provides
    @Named(OPENFLIGHTS_BASE_URL)
    String provideOpenflgihtsBaseUrlString() {return Constants.OPENFLIGHTS_BASE_URL;}

    @Provides
    @Named(OPENFLIGHTS_RETROFIT)
    Retrofit provideOpenflightsRetrofit(@Named(CONVERTER_SCALAR) Converter.Factory converter,
                                        RxJava2CallAdapterFactory factory,
                                        @Named(OPENFLIGHTS_BASE_URL) String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converter)
                .addCallAdapterFactory(factory)
                .build();
    }

    @Provides
    OpenFlightsApi providesOpenflightsApi(@Named(OPENFLIGHTS_RETROFIT) Retrofit retrofit) {
        return retrofit.create(OpenFlightsApi.class);
    }





    /**
     * INJECTION GRAPH FOR QWANT.ORG
     */
    @Provides
    @Named(QWANT_BASE_URL)
    String provideQwantBaseUrlString() {return Constants.QWANT_BASE_URL;}

    @Provides
    @Named(QWANT_RETROFIT)
    Retrofit provideQwantRetrofit(@Named(CONVERTER_GSON) Converter.Factory converter,
                                  RxJava2CallAdapterFactory factory,
                                  @Named(QWANT_BASE_URL) String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converter)
                .addCallAdapterFactory(factory)
                .build();
    }

    @Provides
    QwantApi providesQwantApi(@Named(QWANT_RETROFIT) Retrofit retrofit) {
        return retrofit.create(QwantApi.class);
    }
}
