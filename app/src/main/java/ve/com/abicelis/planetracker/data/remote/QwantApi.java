package ve.com.abicelis.planetracker.data.remote;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ve.com.abicelis.planetracker.data.model.qwant.QwantResponse;

/**
 * Created by abicelis on 1/9/2017.
 */

public interface QwantApi {

    @GET("images?count=1")
    Single<QwantResponse> getImage(@Query("q") String query);

    @GET("images")
    Single<QwantResponse> getImages(@Query("q") String query, @Query("count") int amount);

}
