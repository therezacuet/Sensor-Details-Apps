package com.sensorapps.thereza.sensorapps.activity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
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
    final String proximity = "AMD";
    final String rotationVector = "Rotation Vector";
    final String gravity = "Gravity";
    LinearLayout magnetometerLayout,accelerometerLayout;
    private float mLastX, mLastY, mLastZ;
    private boolean mInitialized;
    private final float NOISE = (float) 1.0;
    private Sensor mProximity,rotationVectorSensor;
    Bundle extras;
    String sensorName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_calibrate);
        initView();
        //mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mInitialized = false;
        extras = getIntent().getExtras();
        sensorName = null;
        if (extras != null) {
            sensorName = extras.getString("sensorName");
        }
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        rotationVectorSensor =
                mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        switch (sensorName){
            case accelarator:
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
                break;
            case magnetometer:
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);
                break;
            case gyroscope:
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_GAME);
                break;
            case proximity:
                mSensorManager.registerListener(this,mProximity,2 * 1000 * 1000);
                break;
            case rotationVector:
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),SensorManager.SENSOR_DELAY_NORMAL);
                break;
            case gravity:
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),SensorManager.SENSOR_DELAY_NORMAL);
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

        switch (sensorName){
            case accelarator:
                AccelaratorSensor(event);
                break;
            case magnetometer:
                MagnetometerSensor(event);
                break;
            case gyroscope:
                GyroscopeSensor(event);
                break;
            case proximity:
                ProximitySensor(event);
                break;
            case rotationVector:
                RotationMatrixSensor(event);
                break;
            case gravity:
                GravitySensor(event);
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

    public void GyroscopeSensor(SensorEvent sensorEvent){
        if(sensorEvent.values[2] > 0.5f) { // anticlockwise
            getWindow().getDecorView().setBackgroundColor(Color.BLUE);
        } else if(sensorEvent.values[2] < -0.5f) { // clockwise
            getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
        }

        /*float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        tvX.setText(Float.toString(x));
        tvY.setText(Float.toString(y));
        tvY.setText(Float.toString(z));

        accelerometerLayout.setVisibility(View.VISIBLE);*/
    }

    public void ProximitySensor(SensorEvent sensorEvent){
        TextView textView = new TextView(this);
        if(sensorEvent.values[0] < mProximity.getMaximumRange()) {
            // Detected something nearby
            getWindow().getDecorView().setBackgroundColor(Color.RED);
            textView.setText("Detected something nearby");
        } else {
            // Nothing is nearby
            getWindow().getDecorView().setBackgroundColor(Color.GREEN);
            textView.setText("Nothing is nearby");
        }
    }

    public void RotationMatrixSensor(SensorEvent sensorEvent){
        float[] rotationMatrix = new float[16];
        SensorManager.getRotationMatrixFromVector(
                rotationMatrix, sensorEvent.values);

        // Remap coordinate system
        float[] remappedRotationMatrix = new float[16];
        SensorManager.remapCoordinateSystem(rotationMatrix,
                SensorManager.AXIS_X,
                SensorManager.AXIS_Z,
                remappedRotationMatrix);
        // Convert to orientations
        float[] orientations = new float[3];
        SensorManager.getOrientation(remappedRotationMatrix, orientations);
        for(int i = 0; i < 3; i++) {
            orientations[i] = (float)(Math.toDegrees(orientations[i]));
        }
        if(orientations[2] > 45) {
            getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
        } else if(orientations[2] < -45) {
            getWindow().getDecorView().setBackgroundColor(Color.BLUE);
        } else if(Math.abs(orientations[2]) < 10) {
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        }
    }

    public void GravitySensor(SensorEvent sensorEvent){
        float standardGravity;
        float thresholdGraqvity;
        standardGravity = SensorManager.STANDARD_GRAVITY;
        thresholdGraqvity = standardGravity/2;
        float z = sensorEvent.values[2];
        //textZValue.setText("Z value = " + z);

        if (z >= thresholdGraqvity){
            //textFace.setText("Face UP");
        }else if(z <= -thresholdGraqvity){
            //textFace.setText("Face DOWN");
        }else{
            //textFace.setText("");
        }
    }

}
