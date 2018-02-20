package com.sensorapps.thereza.sensorapps.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.sensorapps.thereza.sensorapps.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView ;
    SensorManager sensorManager ;
    List<Sensor> listsensor;
    List<String> liststring ;
    ArrayAdapter<String> adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.listview1);

        liststring = new ArrayList<String>();

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        listsensor = sensorManager.getSensorList(Sensor.TYPE_ALL);

        for(int i=0; i<listsensor.size(); i++){

            liststring.add(listsensor.get(i).getName());
        }

        adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1, liststring
        );

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, SensorCalibrateActivity.class);
                i.putExtra("sensorName",parent.getItemAtPosition(position).toString());
                startActivity(i);
            }
        });

    }
}
