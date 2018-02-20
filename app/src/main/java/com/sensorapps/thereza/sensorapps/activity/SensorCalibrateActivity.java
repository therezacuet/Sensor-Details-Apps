package com.sensorapps.thereza.sensorapps.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensorapps.thereza.sensorapps.R;

public class SensorCalibrateActivity extends AppCompatActivity implements SensorEventListener {


    private ImageView image;
    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    TextView tvHeading,tvX,tvY,tvZ;
    ImageView iv;
    final String accelarator = "LSM6DS3 Accelerometer";
    final String magnetometer = "YAS537 Magnetometer";
    final String gyroscope = "LSM6DS3 Gyroscope";
    final String proximity = "LSM6DS3 Gyroscope";
    LinearLayout magnetometerLayout,accelerometerLayout;
    private float mLastX, mLastY, mLastZ;
    private boolean mInitialized;
    private Sensor mAccelerometer;
    private final float NOISE = (float) 1.0;
    Bundle extras;
    String sensorname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_calibrate);
        initView();
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mInitialized = false;
        extras = getIntent().getExtras();
        sensorname = null;
        if (extras != null) {
            sensorname = extras.getString("sensorName");
        }
    }

    public void initView(){
        image = (ImageView) findViewById(R.id.imageViewCompass);
        tvHeading = (TextView) findViewById(R.id.tvHeading);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magnetometerLayout = findViewById(R.id.magnetometerLayout);
        accelerometerLayout = findViewById(R.id.accelerometerLayout);
        tvX= (TextView)findViewById(R.id.x_axis);
        tvY= (TextView)findViewById(R.id.y_axis);
        tvZ= (TextView)findViewById(R.id.z_axis);
        iv = (ImageView)findViewById(R.id.image);
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (sensorname){
            case accelarator:
                mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                break;
            case magnetometer:
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);
                break;
            case gyroscope:

                break;
            default:
                break;
        }
        //

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (sensorname){
            case accelarator:
                AccelaratorSensor(event);
                break;
            case magnetometer:
                MagnetometerSensor(event);
                break;
            case gyroscope:

                break;
            default:
                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void MagnetometerSensor(SensorEvent event){
        float degree = Math.round(event.values[0]);
        tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);
        image.startAnimation(ra);
        currentDegree = -degree;
        magnetometerLayout.setVisibility(View.VISIBLE);
    }

    public void AccelaratorSensor(SensorEvent event){

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            tvX.setText("0.0");
            tvY.setText("0.0");
            tvZ.setText("0.0");
            mInitialized = true;
        } else {
            float deltaX = Math.abs(mLastX - x);
            float deltaY = Math.abs(mLastY - y);
            float deltaZ = Math.abs(mLastZ - z);
            if (deltaX < NOISE) deltaX = (float)0.0;
            if (deltaY < NOISE) deltaY = (float)0.0;
            if (deltaZ < NOISE) deltaZ = (float)0.0;
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            tvX.setText(Float.toString(deltaX));
            tvY.setText(Float.toString(deltaY));
            tvZ.setText(Float.toString(deltaZ));
            iv.setVisibility(View.VISIBLE);
            if (deltaX > deltaY) {
                iv.setImageResource(R.drawable.horizontal);
            } else if (deltaY > deltaX) {
                iv.setImageResource(R.drawable.vertical);
            } else {
                iv.setVisibility(View.INVISIBLE);
            }
        }

        accelerometerLayout.setVisibility(View.VISIBLE);

    }

}
