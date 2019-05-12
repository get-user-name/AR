package com.mraon.arstorysns;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // for AR
    ArFragment arFragment;
    private ViewRenderable textRenderable;
    private ModelRenderable item1Renderable,
                            item2Renderable,
                            item3Renderable,
                            item4Renderable;

    ImageView item0, item1, item2, item3, item4;

    View arrayView[];

    int selected = 1;

    // for firebase database
    private DatabaseReference mDatabase;

    // for gps
    private LocationManager locationManager;
    private LocationListener locationListener;

    double lat, lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        // for AR
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_ux_fragment);

        // View
        item0 = findViewById(R.id.item0);
        item1 = findViewById(R.id.item1);
        item2 = findViewById(R.id.item2);
        item3 = findViewById(R.id.item3);
        item4 = findViewById(R.id.item4);

        setArrayView();
        setClickListener();
        setupModel();

        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                //where user tap on plane, add model
                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());
                createModel(anchorNode, selected);
            }
        });

        // for Firebase database

    }


    private void setArrayView() {
        arrayView = new View[]{
                item0, item1, item2, item3, item4
        };
    }

    private void setClickListener() {
        for(int i=0;i<arrayView.length;i++)
            arrayView[i].setOnClickListener(this);
    }

    private void setupModel(){
        ViewRenderable.builder()
                .setView(this, R.layout.user_text)
                .build()
                .thenAccept(renderable -> textRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load the text", Toast.LENGTH_SHORT).show();
                            return null;
                        }
                );
        ModelRenderable.builder()
                .setSource(this, R.raw.item1)
                .build().thenAccept(renderable -> item1Renderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load the model", Toast.LENGTH_SHORT).show();
                            return null;
                        }
                );
        ModelRenderable.builder()
                .setSource(this, R.raw.item2)
                .build().thenAccept(renderable -> item2Renderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load the model", Toast.LENGTH_SHORT).show();
                            return null;
                        }
                );
        ModelRenderable.builder()
                .setSource(this, R.raw.item3)
                .build().thenAccept(renderable -> item3Renderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load the model", Toast.LENGTH_SHORT).show();
                            return null;
                        }
                );
        ModelRenderable.builder()
                .setSource(this, R.raw.item4)
                .build().thenAccept(renderable -> item4Renderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load the model", Toast.LENGTH_SHORT).show();
                            return null;
                        }
                );

    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.item0){
            selected = 0;
            setBackground(view.getId());
        }
        else if(view.getId() == R.id.item1) {
            selected = 1;
            setBackground(view.getId());
        }
        else if(view.getId() == R.id.item2) {
            selected = 2;
            setBackground(view.getId());
        }
        else if(view.getId() == R.id.item3) {
            selected = 3;
            setBackground(view.getId());
        }
        else if(view.getId() == R.id.item4) {
            selected = 4;
            setBackground(view.getId());
        }
    }

    private void setBackground(int id){
        for(int i=0;i<arrayView.length;i++){
            if(arrayView[i].getId() == id)
                arrayView[i].setBackgroundResource(R.drawable.border);
            else
                arrayView[i].setBackgroundColor(Color.WHITE);
        }
    }

    private void createModel(AnchorNode anchorNode, int selected){
        if(selected == 0){
            // text를 gps 좌표로 설정
            TextView textView = (TextView)textRenderable.getView();

            String lats = Double.toString(lat);
            String lngs = Double.toString(lng);

            textView.setText("lat: "+lats+"\n"+"lng: "+lngs);


            TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
            node.setParent(anchorNode);
            node.setRenderable(textRenderable);
            node.select();


            //addCloseButton(anchorNode, node);

        }
        if(selected == 1){
            TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
            node.setParent(anchorNode);
            node.setRenderable(item1Renderable);
            node.select();

            //addCloseButton(anchorNode, node);

        }
        else if(selected == 2){
            TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
            node.setParent(anchorNode);
            node.setRenderable(item2Renderable);
            node.select();

            //addCloseButton(anchorNode, node);

        }
        else if(selected == 3){
            TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
            node.setParent(anchorNode);
            node.setRenderable(item3Renderable);
            node.select();

            //addCloseButton(anchorNode, node);

        }
        else if(selected == 4){
            TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
            node.setParent(anchorNode);
            node.setRenderable(item4Renderable);
            node.select();

            //addCloseButton(anchorNode, node);

        }

    }

    private void addCloseButton(AnchorNode anchorNode, TransformableNode model){

        ViewRenderable.builder()
                .setView(this, R.layout.close)
                .build()
                .thenAccept(viewRenderable -> {
                    TransformableNode nameView = new TransformableNode(arFragment.getTransformationSystem());
                    nameView.setLocalPosition(new Vector3(0f,model.getLocalPosition().y+0.5f, 0));
                    nameView.setParent(anchorNode);
                    nameView.setRenderable(viewRenderable);
                    nameView.select();

                    ImageView view = (ImageView)viewRenderable.getView();

                    // 클릭하면 remove
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            anchorNode.setParent(null);
                        }
                    });
                });



    }
}
