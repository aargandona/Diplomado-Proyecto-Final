package bo.ara.com.kidstravel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;

import bo.ara.com.kidstravel.model.Person;
import bo.ara.com.kidstravel.model.Travel;

public class CardActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView qrImageView;
    private String qrContent;

    private TextView route;
    private TextView startDate;
    private TextView endDate;
    private TextView minors;
    private TextView parents;

    private Button printBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        route = (TextView) findViewById(R.id.card_route);
        startDate = (TextView) findViewById(R.id.card_startDate);
        endDate = (TextView) findViewById(R.id.card_endDate);
        minors = (TextView) findViewById(R.id.card_minors);
        parents = (TextView) findViewById(R.id.card_parents);
        printBtn = (Button) findViewById(R.id.btn_print);
        printBtn.setOnClickListener(this);

        loadTravelData();
        setupQRImageCode();
    }

    private void loadTravelData() {

        SharedPreferences sp = this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE);

        String travelJson = sp.getString("lastTravel", "");
        Gson gson = new Gson();
        Travel travel = gson.fromJson(travelJson, Travel.class);

        if(travel != null){
            route.setText(travel.getTravelRoute());
            startDate.setText(travel.getStartDate());
            endDate.setText(travel.getEndDate());

            String qrSep = "|";
            String separator = "\n";

            String sMinors = "";
            String sMinorsQR = "";
            for(Person person : travel.getMinors()){
                //sMinors = sMinors + person.getFullName() + separator;
                sMinors = String.format("%s%n%s", sMinors, person.getFullName());
                sMinorsQR = sMinorsQR + person.getFullName() + qrSep;
            }
            sMinors = sMinors.trim();
            minors.setText(sMinors);

            //String sParents = travel.getApplicant().getFullName() + separator + travel.getAuthorizer().getFullName();
            String sParents = String.format("%s%n%s", travel.getApplicant().getFullName(), travel.getAuthorizer().getFullName());
            String sParentsQR = travel.getApplicant().getFullName() + qrSep + travel.getAuthorizer().getFullName();
            parents.setText(sParents);

            //qr Content
            qrContent = travel.get_id() + qrSep + travel.getTravelRoute() + qrSep + travel.getStartDate() + qrSep + travel.getEndDate() + qrSep +
                        sMinorsQR + sParentsQR;
        }
    }

    private void setupQRImageCode(){

        try {
            //String content = "Kids Travel App, the best of world...";
            Bitmap bitmap = createQRImageCode(qrContent);

            //Add to imageView
            qrImageView = (ImageView) findViewById(R.id.card_QRCode);
            qrImageView.setImageBitmap(bitmap);

            Log.d("QR", "Add image QR success");
        }
        catch(Exception ex){
            Log.d("QR", "Add image QR fail = " + ex.getMessage());
        }

//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        byte[] bitmapData = stream.toByteArray();
    }

    //QR Code
    public static Bitmap createQRImageCode(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 300, 300, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }
    //>>

    @Override
    public void onClick(View v) {
        if(v == printBtn) {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
        }
    }
}
