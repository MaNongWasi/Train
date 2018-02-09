package com.example.vtec.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;
import android.os.Handler;
/**
 * Created by VTEC on 3/9/2016.
 */
public class DetailActivity extends Activity {
    private TextView title_tv, process_tv, detail_tv;
    private TextView before_tv, next_tv;
    private EditText data_et;
    private ImageView img;
    private DatabaseHelper dbHelper;
    private String title = "", chosen_day = "", info = "";
    private ArrayList<DetailInfo> actionlist = new ArrayList<DetailInfo>();
    private int bcolor = Color.BLACK, tcolor = Color.WHITE;
    public boolean different = false;
    private int groups = 0, c_count = 1;
    private List<String> counts = new ArrayList<>();
    private DetailInfo detailInfo;
    private List<String> last_weight;
    private int[] cardio = {R.drawable.jumping_jack, R.drawable.rest, R.drawable.high_knee, R.drawable.rest, R.drawable.houti, R.drawable.rest, R.drawable.squat_jumps, R.drawable.rest, R.drawable.plank_jack, R.drawable.rest, R.drawable.jump_lunges, R.drawable.rest, R.drawable.mountain_climbe, R.drawable.rest, R.drawable.side_plank, R.drawable.rest, R.drawable.side_plank, R.drawable.rest, R.drawable.burpee, R.drawable.rest};
    private int min = 0, sec = 60;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dbHelper = new DatabaseHelper(this);

        getPhoto();
        init_Detail_UI();
        getDetail();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        last_weight.set(c_count - 1, data_et.getText().toString());
        updateData();
    }

    private void init_Detail_UI() {
        title_tv = (TextView) findViewById(R.id.action_title);
        detail_tv = (TextView) findViewById(R.id.detail_tv);
        process_tv = (TextView) findViewById(R.id.proces_tv);
        data_et = (EditText) findViewById(R.id.data_et);
        img = (ImageView) findViewById(R.id.img_info);
        before_tv = (TextView) findViewById(R.id.before_tv);
        next_tv = (TextView) findViewById(R.id.next_tv);

        before_tv.setText("<<");
        detailInfo = getIntent().getParcelableExtra(Config.ACTION);
        title = detailInfo.getName();
        info = detailInfo.getInfo();
        title_tv.setText(title);
        img.setImageResource(detailInfo.getImg());
        title_tv.setTextColor(tcolor);
        process_tv.setTextColor(tcolor);
        detail_tv.setTextColor(tcolor);
        data_et.setTextColor(tcolor);
        data_et.setHintTextColor(tcolor);
        before_tv.setTextColor(tcolor);
        next_tv.setTextColor(tcolor);
        chosen_day = getIntent().getStringExtra(Config.TODAY);

        actionlist = getIntent().getParcelableArrayListExtra(Config.ACTIONLIST);

        if (info.contains("min")) {
            detail_tv.setText(info);
            process_tv.setVisibility(View.INVISIBLE);
            before_tv.setVisibility(View.INVISIBLE);
            next_tv.setVisibility(View.INVISIBLE);
            data_et.setVisibility(View.INVISIBLE);

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (title.equals(Actions.cardio)) {
                        detail_tv.setTextSize(100);
                        img.setImageResource(cardio[min]);
                        handler.postDelayed(runnable, 1000);
                    } else {
                        next();
                    }
                }
            });
        } else {
            if (info.contains("x")) {
                String[] times = info.replaceAll(" ", "").split("x");
                if (times.length > 1) {
                    groups = Integer.valueOf(times[0]);
                    for (int i = 0; i < groups; i++) {
                        counts.add(times[1]);
                    }
                }
            } else if (info.contains("-"))

            {
                String[] times = info.replaceAll(" ", "").split("-");
                different = true;
                if (times.length > 0) {
                    if (times[0].equals(Actions.fst)) {
                        groups = Integer.valueOf(times[1]);
                        for (int i = 0; i < groups; i++) {
                            counts.add(Actions.ten);
                        }
                    } else {
                        groups = times.length;
                        for (int i = 0; i < groups; i++) {
                            counts.add(times[i]);
                        }
                    }
                }
            }

            process_tv.setText(String.valueOf(c_count) + " / " + String.valueOf(groups));
            detail_tv.setText(counts.get(c_count - 1));
            before_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    before();
                }
            });
            next_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    next();
                }
            });
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            intent.putExtra(Config.TODAY, chosen_day);
            intent.putParcelableArrayListExtra(Config.ACTIONLIST, actionlist);
            startActivity(intent);
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void getDetail() {
        last_weight = dbHelper.readUsersData(chosen_day, title, info);
        data_et.setText(last_weight.get(c_count - 1));
    }

    public void updateData() {
        dbHelper.updateUsersData(chosen_day, title, info, last_weight);
    }

    public void getPhoto() {
        SharedPreferences myPreference = getSharedPreferences(Config.img, Context.MODE_PRIVATE);
        int wp = myPreference.getInt(Config.wp, 0);
        tcolor = myPreference.getInt(Config.text_ref, tcolor);
        if (wp == Config.PIC_ID) {
            String pic = myPreference.getString(Config.im_ref, null);
            if (pic != null) {
                Bitmap bitmap = decodeBase64(pic).copy(Bitmap.Config.ARGB_8888, true);
//            background = bitmap;
                BitmapDrawable bitdra = new BitmapDrawable(bitmap);

                getWindow().getDecorView().setBackgroundDrawable(bitdra);
            }
        } else if (wp == Config.COLOR_ID) {
            bcolor = myPreference.getInt(Config.im_ref, bcolor);
            getWindow().getDecorView().setBackgroundColor(bcolor);
        } else {
            getWindow().getDecorView().setBackgroundColor(bcolor);
        }
    }

    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    // public void before(View view) {
    public void before() {
        last_weight.set(c_count - 1, data_et.getText().toString());
        if (c_count > 1) {
            c_count--;
            process_tv.setText(String.valueOf(c_count) + " / " + String.valueOf(groups));
            detail_tv.setText(counts.get(c_count - 1));
            data_et.setText(last_weight.get(c_count - 1));
        }
    }

    //public void next(View view) {
    public void next() {
        last_weight.set(c_count - 1, data_et.getText().toString());
        if (c_count < groups) {
            c_count++;
            process_tv.setText(String.valueOf(c_count) + " / " + String.valueOf(groups));
            detail_tv.setText(counts.get(c_count - 1));
            if (last_weight.get(c_count - 1).equals("")) {
                data_et.setText(last_weight.get(c_count - 2));
            } else {
                data_et.setText(last_weight.get(c_count - 1));
            }
        } else {
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            intent.putExtra(Config.TODAY, chosen_day);
            for (int i = 0; i < actionlist.size(); i++) {
                if (actionlist.get(i).getName().equals(title)) actionlist.remove(i);
            }
            intent.putParcelableArrayListExtra(Config.ACTIONLIST, actionlist);
            startActivity(intent);
            finish();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sec--;
            if (sec > 9) {
                detail_tv.setText(String.format("0:%d", sec));
            }else {
                detail_tv.setText(String.format("0:0%d", sec));
            }
            System.out.println("sec " + sec + " " + + min + " " + (min<cardio.length - 1) + " " + cardio.length);
            if (min < cardio.length) {
                System.out.println("min " + min);
                if (sec == 0) {
                    img.setImageResource(cardio[min]);
                    min++;
                    sec = 60;
                }
                handler.postDelayed(this, 1000);

            }else {
                next();
            }
        }
    };
}
