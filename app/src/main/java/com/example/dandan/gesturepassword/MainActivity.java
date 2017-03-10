package com.example.dandan.gesturepassword;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Gesture gesture = (Gesture) findViewById(R.id.gesture);
        gesture.setOnCompleteListener(new Gesture.OnCompleteListener() {
            @Override
            public void onComplete(List<Point> password) {
                System.out.println("aaa");
                StringBuffer sb = new StringBuffer();
                for(Point point : password)
                {
                    sb.append(point.index + ",");
                }
                System.out.println(sb.toString());
            }
        });
    }
}
