package com.example.dandan.gesturepassword;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button reset;
    private Gesture gesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gesture = (Gesture) findViewById(R.id.gesture);
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
        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gesture.reset();
            }
        });
    }
}
