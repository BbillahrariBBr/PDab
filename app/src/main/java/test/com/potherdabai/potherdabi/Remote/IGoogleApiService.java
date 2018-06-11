package test.com.potherdabai.potherdabi.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;
import test.com.potherdabai.potherdabi.Model.MyPlaces;
import test.com.potherdabai.potherdabi.Model.PlaceDetails;

public interface IGoogleApiService {
    @GET
    Call<MyPlaces> getNearByPlaces(@Url String url);

    @GET
    Call<PlaceDetails> getDetailsPlaces(@Url String url);
}


