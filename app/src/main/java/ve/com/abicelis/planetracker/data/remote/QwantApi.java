package ve.com.abicelis.planetracker.data.remote;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by abicelis on 1/9/2017.
 */

public interface QwantApi {

    @GET("images?count=1")
    Single<String> getImage(@Query("q") String query);

}
