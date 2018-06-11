package test.com.potherdabai.potherdabi;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.com.potherdabai.potherdabi.Model.Opening_hours;
import test.com.potherdabai.potherdabi.Model.Photos;
import test.com.potherdabai.potherdabi.Model.PlaceDetails;
import test.com.potherdabai.potherdabi.Remote.IGoogleApiService;

public class ViewPlace extends AppCompatActivity {

    ImageView photo;
    IGoogleApiService iGoogleApiService;
    TextView place_name,place_adress, open_hour;
    RatingBar ratingBar;
    Button buttonMapView, buttonDirection;
    PlaceDetails mPlace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);

        iGoogleApiService = Common.getGoogleApiService();
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        place_adress = (TextView)findViewById(R.id.place_adress);
        place_name = (TextView)findViewById(R.id.place_name);
        open_hour = (TextView)findViewById(R.id.place_open_hour);
        buttonMapView = (Button)findViewById(R.id.button_show_map);
        buttonDirection = (Button)findViewById(R.id.button_direction);


        photo = (ImageView)findViewById(R.id.photo);

        //empty all view
        place_adress.setText("");
        place_name.setText("");
        open_hour.setText("");

        buttonMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapInetnt = new Intent(Intent.ACTION_VIEW, Uri.parse(mPlace.getResult().getUrl()));
                startActivity(mapInetnt);
            }
        });

        buttonDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent mapInetnt = new Intent(ViewPlace.this,ViewDirection.class);
//                startActivity(mapInetnt);
            }
        });
//photo
        if (Common.currentResult.getPhotos()!=null && Common.currentResult.getPhotos().length>0){
            Picasso.with(this)
                    .load(getPhotoOfPlace(Common.currentResult.getPhotos()[0].getPhoto_reference(),1000))
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_error_black_24dp)
                    .into(photo);
        }
//rating
        if (Common.currentResult.getRating()!= null && TextUtils.isEmpty(Common.currentResult.getRating()))
        {
            ratingBar.setRating(Float.parseFloat(Common.currentResult.getRating()));
        }
        else {
            ratingBar.setVisibility(View.GONE);
        }

        //opening_hour
        if (Common.currentResult.getOpening_hours()!= null)
        {
            open_hour.setText("Open Now: "+ Common.currentResult.getOpening_hours().getOpen_now());
        }
        else {
            open_hour.setVisibility(View.GONE);
        }

        //user service to fetch adress to name
        iGoogleApiService.getDetailsPlaces(getPlaceDetailurl(Common.currentResult.getPlace_id()))
                .enqueue(new Callback<PlaceDetails>() {
                    @Override
                    public void onResponse(Call<PlaceDetails> call, Response<PlaceDetails> response) {
                        mPlace = response.body();
                        place_adress.setText(mPlace.getResult().getFormatted_address());
                        place_name.setText(mPlace.getResult().getName());
                    }

                    @Override
                    public void onFailure(Call<PlaceDetails> call, Throwable t) {

                    }
                });



    }

    private String getPlaceDetailurl(String place_id) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json");
        url.append("?placeid="+place_id);
        url.append("&key="+getResources().getString(R.string.browse_key));
        return  url.toString();
    }

    private String getPhotoOfPlace(String photo_ref,int maxWidth) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        url.append("?maxwidth="+maxWidth);
        url.append("&photoreference="+photo_ref);
        url.append("&key="+getResources().getString(R.string.browse_key));
        return url.toString();


    }
}
