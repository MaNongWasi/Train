package com.example.vtec.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {
    ListView actionListView;
    List<Map<String, Object>> list;
    ArrayList<DetailInfo> plan_today;
    private DatabaseHelper dbHelper;
    private static final int TAKE_PICTURE = 0, CHOOSE_PICTURE = 1, WP = 2, TEXT = 3;
    private ActionAdapter adapter;
    private String chosen_day = "";
    private int bcolor = Color.BLACK, tcolor = Color.WHITE;
    private Intent timeService = null;
    public static TextView timer_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(Color.RED);

        final com.github.clans.fab.FloatingActionButton scheduleB = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_train);
//        final com.getbase.floatingactionbutton.FloatingActionButton scheduleB = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.schedule_b);
        scheduleB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scheduleIntent = new Intent(MainActivity.this, ScheduleActivity.class);
                startActivity(scheduleIntent);
            }
        });

        final com.github.clans.fab.FloatingActionButton nutB = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.fab_nut);
//        final com.getbase.floatingactionbutton.FloatingActionButton nutB = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.nut_b);
        nutB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nutIntent = new Intent(MainActivity.this, NutritionActivity.class);
                startActivity(nutIntent);
            }
        });

        final com.github.clans.fab.FloatingActionButton setB = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.fab_set);
//        final com.getbase.floatingactionbutton.FloatingActionButton setB = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.setwp_b);
        setB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicturePicker(MainActivity.this);
            }
        });

        final com.github.clans.fab.FloatingActionButton exportB = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.fab_exp);
//        final com.getbase.floatingactionbutton.FloatingActionButton exportB = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.export_b);
        exportB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //export csv
            }
        });

        timer_tv = (TextView)findViewById(R.id.timer);

        dbHelper = new DatabaseHelper(this);
        plan_today = getIntent().getParcelableArrayListExtra(Config.ACTIONLIST);
        chosen_day = getIntent().getStringExtra(Config.TODAY);

        getPhoto();

        if (plan_today != null) {
            createListView(plan_today);
        } else {
            chooseDay(getDayNumber());
        }

        addLocalValue();

        StartService();
    }

    private void StartService(){
        timeService = new Intent(this,TimerService.class);
        this.startService(timeService);
    }

    private void addLocalValue(){
        SharedPreferences preferences = getSharedPreferences(Config.DB, Context.MODE_PRIVATE);
        Boolean added = preferences.getBoolean(Config.ADDED, false);
        if (!added){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Config.ADDED, true);
            editor.commit();
            dbHelper.addMealsInfo();
        }

    }
    private void chooseDay(int day) {
        switch (day) {
            case 1:
                plan_today = dbHelper.getDayPlan(Config.SUN);
                chosen_day = Config.SUN;
//                System.out.println("Sunday Rest cheat day");
                break;
            case 2:
                plan_today = dbHelper.getDayPlan(Config.MON);
                chosen_day = Config.MON;
//                System.out.println("Monday Spinning, HIIT");
                break;
            case 3:
                plan_today = dbHelper.getDayPlan(Config.TUE);
                chosen_day = Config.TUE;
//                System.out.println("Tuesday Borst Chest");
                break;
            case 4:
                plan_today = dbHelper.getDayPlan(Config.WED);
                chosen_day = Config.WED;
//                System.out.println("Wednesday rest potato");
                break;
            case 5:
                plan_today = dbHelper.getDayPlan(Config.THU);
                chosen_day = Config.THU;
//                System.out.println("Thursday Benen Leg");
                break;
            case 6:
                plan_today = dbHelper.getDayPlan(Config.FRI);
                chosen_day = Config.FRI;
//                System.out.println("Friday should shouder back rug");
                break;
            case 7:
                plan_today = dbHelper.getDayPlan(Config.SAT);
                chosen_day = Config.SAT;
//                System.out.println("Saturday Pump Bootcamp");
                break;
            default:
                plan_today = new ArrayList<>();
//                System.out.println("Unknown day");
                break;
        }

        createListView(plan_today);

    }

    private void createListView(ArrayList<DetailInfo> plan) {
        adapter = new ActionAdapter(this, R.layout.alist, plan);
        adapter.setColor(tcolor);
//        adapter = new SimpleAdapter(this, getData(plan), R.layout.alist,
//                new String[]{"title", "info", "img"},
//                new int[]{R.id.title, R.id.info, R.id.img});
        actionListView = (ListView) findViewById(R.id.action_lists);
        actionListView.setAdapter(adapter);
        actionListView.setOnItemClickListener(mItemClickListener);
    }

    public int getDayNumber() {
        Calendar c = Calendar.getInstance();
        int number = c.get(Calendar.DAY_OF_WEEK);
        return number;
    }

//    private List<Map<String, Object>> getData(List<DetailInfo> groups) {
//        list = new ArrayList<Map<String, Object>>();
//        Map<String, Object> map;
//        DetailInfo detailInfo;
//
//        if (groups.size() > 0) {
//            for (int i = 0; i < groups.size(); i++) {
//                map = new HashMap<String, Object>();
//                detailInfo = groups.get(i);
//                map.put(Config.title, detailInfo.getName());
//                map.put(Config.info, detailInfo.getInfo());
//                map.put(Config.img, R.drawable.background);
//                list.add(map);
//            }
//        }
//        return list;
//    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @SuppressLint("NewApi")
        @Override
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // TODO Auto-generated method stub

//            Map<String, Object> map = list.get(arg2);
            DetailInfo map = plan_today.get(arg2);

            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
//            Intent intent = new Intent(MainActivity.this, DetailDataActivity.class);
            intent.putExtra(Config.TODAY, chosen_day);
//            intent.putExtra(Config.title, map.getName());
//            intent.putExtra(Config.info, map.getInfo());
//            intent.putExtra(Config.title, map.get(Config.title).toString());
//            intent.putExtra(Config.info, map.get(Config.info).toString());
            intent.putExtra(Config.ACTION, map);
            intent.putParcelableArrayListExtra(Config.ACTIONLIST, plan_today);
            startActivity(intent);
            finish();
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showPicturePicker(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(Config.PIC);
        builder.setNegativeButton(Config.CANCEL, null);
        builder.setItems(new String[]{Config.TAKE_PHOTO, Config.GALLERY, Config.WP, Config.TEXT}, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case TAKE_PICTURE:
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
                        //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;

                    case CHOOSE_PICTURE:
                        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;

                    case WP:
                        openDialog(true, bcolor, WP);
                        break;

                    case TEXT:
                        openDialog(true, tcolor, TEXT);
                        break;

                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    //将保存在本地的图片取出并缩小后显示在界面上
                    Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/image.jpg");
                    //Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);

//                    Toast.makeText(this, "kuan" + bitmap.getWidth() + "gao" + bitmap.getHeight(), Toast.LENGTH_SHORT).show();
                    Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 3);
                    //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                    bitmap.recycle();

//                     saveBitmapToSharedPreferences(bitmap);
                    savePhoto(newBitmap);

                    Canvas canvas = new Canvas(newBitmap);
                    canvas.save(Canvas.ALL_SAVE_FLAG);
                    BitmapDrawable bitdra = new BitmapDrawable(newBitmap);
                    //将处理过的图片显示在界面上，并保存到本地
                    getWindow().getDecorView().setBackgroundDrawable(bitdra);
//                    ImageTools.savePhotoToSDCard(newBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), String.valueOf(System.currentTimeMillis()));
                    //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常

//                    bitmap.recycle();
                    ;
                    //将处理过的图片显示在界面上，并保存到本地
//				iv_image.setImageBitmap(newBitmap);
                    //ImageTools.savePhotoToSDCard(newBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), String.valueOf(System.currentTimeMillis()));

                    break;

                case CHOOSE_PICTURE:
                    ContentResolver resolver = getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        if (photo != null) {
                            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / 2, photo.getHeight() / 3);
                            photo.recycle();

                            //saveBitmapToSharedPreferences(smallBitmap);

//                            canvas = new Canvas(smallBitmap);
//                            canvas.save(Canvas.ALL_SAVE_FLAG);
                            bitdra = new BitmapDrawable(smallBitmap);
                            savePhoto(smallBitmap);

                            //将处理过的图片显示在界面上，并保存到本地
                            getWindow().getDecorView().setBackgroundDrawable(bitdra);

                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        }
    }

//    private void saveBitmapToSharedPreferences(Bitmap bitmap) {
//        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);
//        //第一步:将Bitmap压缩至字节数组输出流ByteArrayOutputStream
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
//        //第二步:利用Base64将字节数组输出流中的数据转换成字符串String
//        byte[] byteArray = byteArrayOutputStream.toByteArray();
//        String imageString = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
//        //第三步:将String保持至SharedPreferences
//        SharedPreferences sharedPreferences = getSharedPreferences(Config.img, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(Config.PIC, imageString);
//        editor.commit();
//    }
//
//
//    private void getBitmapFromSharedPreferences() {
//        SharedPreferences sharedPreferences = getSharedPreferences(Config.img, Context.MODE_PRIVATE);
//        //第一步:取出字符串形式的Bitmap
//        String imageString = sharedPreferences.getString(Config.PIC, "");
//        //第二步:利用Base64将字符串转换为ByteArrayInputStream
//        if (!imageString.equals("")) {
//            byte[] byteArray = Base64.decode(imageString, Base64.DEFAULT);
//            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
//            //第三步:利用ByteArrayInputStream生成Bitmap
//            Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream).copy(Bitmap.Config.ARGB_8888, true);
//            Canvas canvas = new Canvas(bitmap);
//            canvas.save(Canvas.ALL_SAVE_FLAG);
//            BitmapDrawable bitdra = new BitmapDrawable(bitmap);
//            //将处理过的图片显示在界面上，并保存到本地
//            getWindow().getDecorView().setBackgroundDrawable(bitdra);
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sun:
                plan_today = dbHelper.getDayPlan(Config.SUN);
                chosen_day = Config.SUN;
                adapter = new ActionAdapter(this, R.layout.alist, plan_today);
                adapter.setColor(tcolor);
                actionListView.setAdapter(adapter);
                return true;
            case R.id.mon:
                plan_today = dbHelper.getDayPlan(Config.MON);
                chosen_day = Config.MON;
//                System.out.println("Monday Spinning, HIIT");
//                list = getData(plan_today);
                adapter = new ActionAdapter(this, R.layout.alist, plan_today);
                adapter.setColor(tcolor);
                actionListView.setAdapter(adapter);
                return true;
            case R.id.tue:
                plan_today = dbHelper.getDayPlan(Config.TUE);
                chosen_day = Config.TUE;
//                System.out.println("Tuesday Borst Chest");
                adapter = new ActionAdapter(this, R.layout.alist, plan_today);
                adapter.setColor(tcolor);
                actionListView.setAdapter(adapter);
                return true;
            case R.id.wed:
                plan_today = dbHelper.getDayPlan(Config.WED);
                chosen_day = Config.WED;
//                System.out.println("Wednesday rest potato");
                adapter = new ActionAdapter(this, R.layout.alist, plan_today);
                adapter.setColor(tcolor);
                actionListView.setAdapter(adapter);
                return true;
            case R.id.thu:
                plan_today = dbHelper.getDayPlan(Config.THU);
                chosen_day = Config.THU;
//                System.out.println("Thursday Benen Leg");
                adapter = new ActionAdapter(this, R.layout.alist, plan_today);
                adapter.setColor(tcolor);
                actionListView.setAdapter(adapter);
                return true;
            case R.id.fri:
                plan_today = dbHelper.getDayPlan(Config.FRI);
                chosen_day = Config.FRI;
//                System.out.println("Friday should shouder back rug");
                adapter = new ActionAdapter(this, R.layout.alist, plan_today);
                adapter.setColor(tcolor);
                actionListView.setAdapter(adapter);
                return true;
            case R.id.sat:
                plan_today = dbHelper.getDayPlan(Config.SAT);
                chosen_day = Config.SAT;
//                System.out.println("Saturday Pump Bootcamp");
                adapter = new ActionAdapter(this, R.layout.alist, plan_today);
                adapter.setColor(tcolor);
                actionListView.setAdapter(adapter);
                return true;
            default:
                plan_today = new ArrayList<>();
//                System.out.println("Unknown day");
                return super.onOptionsItemSelected(item);
        }
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        return imageEncoded;
    }

    public void savePhoto(Bitmap yourbitmap) {
        SharedPreferences myPreference = getSharedPreferences(Config.img, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPreference.edit();
        editor.putInt(Config.wp, Config.PIC_ID);
        editor.putString(Config.im_ref, encodeTobase64(yourbitmap));
        editor.commit();
    }

    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public void getPhoto() {
        SharedPreferences myPreference = getSharedPreferences(Config.img, Context.MODE_PRIVATE);
        int wp = myPreference.getInt(Config.wp, 0);
        tcolor = myPreference.getInt(Config.text_ref, tcolor);
        timer_tv.setTextColor(tcolor);
        if (wp == Config.PIC_ID){
            String pic = myPreference.getString(Config.im_ref, null);
            if (pic != null) {
                Bitmap bitmap = decodeBase64(pic).copy(Bitmap.Config.ARGB_8888, true);
//            background = bitmap;
                BitmapDrawable bitdra = new BitmapDrawable(bitmap);
                //将处理过的图片显示在界面上，并保存到本地
                getWindow().getDecorView().setBackgroundDrawable(bitdra);
            }
        }else if (wp == Config.COLOR_ID){
            bcolor = myPreference.getInt(Config.im_ref, bcolor);
            getWindow().getDecorView().setBackgroundColor(bcolor);
        }else {
            getWindow().getDecorView().setBackgroundColor(bcolor);
        }
    }

    void openDialog(boolean supportsAlpha, int color, final int color_type) {
        final SharedPreferences myPreference = getSharedPreferences(Config.img, 0);
        final SharedPreferences.Editor editor = myPreference.edit();
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(MainActivity.this, color, supportsAlpha, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                switch (color_type){
                    case WP:
                        getWindow().getDecorView().setBackgroundColor(color);
                        editor.putInt(Config.wp, Config.COLOR_ID);
                        editor.putInt(Config.im_ref, color);
                        editor.commit();
                        break;
                    case  TEXT:
                        editor.putInt(Config.text_ref, color);
                        editor.commit();
                        adapter.setColor(color);
                        timer_tv.setTextColor(color);
                        actionListView.setAdapter(adapter);
                        break;
                }

            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
//                Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

}
