package com.example.gesturedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Integer> colorList;
    private int index = 0;  //List<Integer>的索引值
    private LinearLayout linearLayout01;
    private LinearLayout linearLayout02;
    //private GestureOverlayView gestureOverlay;
    private TextView tv_message;
    private GestureLibrary gestureLibrary;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createColorList();

        //呼叫fromRawResource()指定欲載入的手勢資料庫
        gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);

        //呼叫load()真正載入
        if (!gestureLibrary.load()) {

            //退出當前的Activity
            finish();
        }

        findViews();
    }

    private void createColorList() {
        colorList = new ArrayList<>();
        colorList.add(Color.RED);
        colorList.add(Color.YELLOW);
        colorList.add(Color.GREEN);
        colorList.add(Color.BLUE);
        colorList.add(Color.GRAY);
        colorList.add(Color.CYAN);
    }

    private void findViews() {
        linearLayout01 = (LinearLayout) findViewById(R.id.linearLayout01);
        linearLayout02 = (LinearLayout) findViewById(R.id.linearLayout02);
        tv_message = (TextView) findViewById(R.id.tv_message);
        GestureOverlayView gestureOverlay = (GestureOverlayView) findViewById(R.id.gestureOverlayView);

        linearLayout02.setBackgroundColor(colorList.get(index));

        gestureOverlay.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
            @Override
            public void onGesturePerformed(GestureOverlayView gestureOverlayView, Gesture gesture) {

                //呼叫recognize(gesture)比對手勢資料庫與使用者畫的手勢
                //比對結果們(每個比對結果包含手勢的名稱與分數)會依照分數高至低順序儲存在ArrayList<Prediction>
                //在此範例，ArrayList<Prediction>內有4個Prediction(比對結果)，比對結果的數量與手勢資料庫內的手勢數量相同
                ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);

                if (predictions == null || predictions.size() <= 0) {
                    tv_message.setText("Cannot recognize your gesture!");

                    //結束此方法
                    return;
                }

                //呼叫get(0)取得第一個索引，分數最高的手勢
                String gestureName = predictions.get(0).name;
                double gestureScore = predictions.get(0).score;

                String message = String.format("name: %s%nscore: %.1f", gestureName, gestureScore);
                tv_message.setText(message);

                switch (gestureName) {

                    //向左滑索引值+1，取用下一個顏色.
                    //當索引值超過臨界時將其設定為0，變回第一個顏色繼續循環.
                    case "swipe_left":
                        if (gestureScore > 10) {
                            index++;
                            if (index >= colorList.size()) {
                                index = 0;
                            }
                            linearLayout02.setBackgroundColor(colorList.get(index));
                        }
                        break;

                    //向右滑索引值-1，取用上一個顏色.
                    //當索引值為負時，變回最後一個顏色繼續循環.
                    case "swipe_right":
                        if (gestureScore > 10) {
                            index--;
                            if (index < 0) {
                                index = colorList.size() - 1;
                            }
                            linearLayout02.setBackgroundColor(colorList.get(index));
                        }
                        break;

                    case "circle":
                        if (gestureScore >= 3) {
                            linearLayout01.setBackgroundColor(Color.DKGRAY);
                        }
                        break;

                    case "check":
                        if (gestureScore >= 3) {
                            linearLayout01.setBackgroundColor(Color.MAGENTA);
                        }
                        break;
                }

            }
        });
    }


}