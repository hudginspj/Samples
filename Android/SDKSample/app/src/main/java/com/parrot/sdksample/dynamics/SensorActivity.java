package com.parrot.sdksample.dynamics;

        import android.app.Activity;

        import android.hardware.Sensor;

        import android.hardware.SensorEvent;

        import android.hardware.SensorEventListener;

        import android.hardware.SensorManager;

        import android.os.Bundle;

        import android.util.Log;

        import android.widget.TextView;

/**

 * Created by root on 4/1/17.

 */

public class SensorActivity extends Activity implements SensorEventListener {

    private  SensorManager mSensorManager;

    private  Sensor mSensor;

    private String TAG;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_sensor);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        mSensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_FASTEST);

    }

    public SensorActivity() {

    }

    protected void onResume() {

        super.onResume();

        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);

    }

    protected void onPause() {

        super.onPause();

        mSensorManager.unregisterListener(this);

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {

        Log.d(TAG, "onSensorChanged: X rot: "+String.format("%f",event.values[0]));

        Log.d(TAG, "onSensorChanged: Y rot: "+String.format("%f",event.values[1]));

        Log.d(TAG, "onSensorChanged: Z rot: "+String.format("%f",event.values[2]));

        //((TextView)findViewById(R.id.X)).setText("X rot: "+(Math.asin(event.values[0])*2));

        //((TextView)findViewById(R.id.Y)).setText("Y rot: "+(Math.asin(event.values[1])*2));

        //((TextView)findViewById(R.id.Z)).setText("Z rot: "+(Math.asin(event.values[2])*2));

    }

}
