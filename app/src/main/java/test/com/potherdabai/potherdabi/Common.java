package test.com.potherdabai.potherdabi;

import test.com.potherdabai.potherdabi.Model.MyPlaces;
import test.com.potherdabai.potherdabi.Model.Results;
import test.com.potherdabai.potherdabi.Remote.IGoogleApiService;
import test.com.potherdabai.potherdabi.Remote.RetrfitClient;

public class Common {

    public  static Results currentResult;

    private static  final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static IGoogleApiService getGoogleApiService(){
        return RetrfitClient.getClient(GOOGLE_API_URL).create(IGoogleApiService.class);

    }
}

