package com.example.vtec.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.widget.AbsListView.OnScrollListener;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by VTEC on 3/10/2016.
 */
public class NutritionActivity extends Activity implements OnScrollListener {
    private static final String TAG = "iphone";
    private static final String PRE = "IphoneExpandableListActivity--";
    private LinkedHashMap<String, HeaderInfo> myPlans = new LinkedHashMap<String, HeaderInfo>();
    private ArrayList<HeaderInfo> deptList = new ArrayList<HeaderInfo>();
    private ExpandableListView listView;
    private MyExpandableListAdapter mAdapter;
    private FrameLayout indicatorGroup;
    private int indicatorGroupId = -1;
    private int indicatorGroupHeight;
    private LayoutInflater mInflater;
    private DatabaseHelper dbHelper;
    private String[] groups, food_list;
    //    private String[] groups = {Config.M1, Config.M2, Config.M3, Config.M4, Config.M5, Config.M6};
//    private String[] food_list = {Food.protein, Food.oats, Food.banana, Food.bbread, Food.omelet, Food.kipfilet, Food.tonijn, Food.kom, Food.tomaat, Food.b_protein, Food.amandel, Food.l_c, Food.egg, Food.gf, Food.veg, Food.meat, Food.fish, Food.turky, Food.beef, Food.kwark, Food.walnoten, Food.water};
    private int bcolor = Color.BLACK, tcolor = Color.WHITE;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView = (ExpandableListView) findViewById(R.id.expandableListView);
        indicatorGroup = (FrameLayout) findViewById(R.id.topGroup);
        groups = getResources().getStringArray(R.array.meals);
        food_list = getResources().getStringArray(R.array.food);

        dbHelper = new DatabaseHelper(this);

        loadData(dbHelper.getNutPlans());
        //attach the adapter to the list
        mAdapter = new MyExpandableListAdapter(NutritionActivity.this, deptList);
        listView.setAdapter(mAdapter);
        listView.setOnScrollListener(this);
        listView.setGroupIndicator(null);
        // copy group view to indicator Group
        mInflater.inflate(R.layout.list_item, indicatorGroup, true);
        listView.setOnChildClickListener(myListItemClicked);
        listView.setOnItemLongClickListener(myListItemLongClicked);

        getPhoto();
    }

    private void loadData(List<HashMap<String, DetailInfo>> plan) {
        LinkedHashMap<String, String> mealList = dbHelper.getMealInfo();
        String[] meals = getResources().getStringArray(R.array.meals);
        for (int i = 0; i < mealList.size(); i++) {
            System.out.println(mealList.get(meals[i]));
            addMeals(meals[i], mealList.get(meals[i]), "+", "");
        }

        for (int i = 0; i < plan.size(); i++) {
            HashMap<String, DetailInfo> group = plan.get(i);
            Iterator iterator = group.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                DetailInfo detailInfo = (DetailInfo) entry.getValue();
                addMeals((String) entry.getKey(), "", detailInfo.getName(), detailInfo.getInfo());
            }
        }

    }

    //here we maintain our products in various departments
    private int addMeals(String mealNo, String detail, String food, String info) {

        int groupPosition = 0;

        //check the hash map if the group already exists
        HeaderInfo headerInfo = myPlans.get(mealNo);
        //add the group if doesn't exists
        if (headerInfo == null) {
            headerInfo = new HeaderInfo();
            headerInfo.setdaytime(mealNo);
            headerInfo.setDetailTime(detail);
            myPlans.put(mealNo, headerInfo);
            deptList.add(headerInfo);
        }

        //get the children for the group
        ArrayList<DetailInfo> nutList = headerInfo.getActionList();
        //size of the children list
//        int listSize = productList.size();
//        //add to the counter
//        listSize++;

        //create a new child and add that to the group
        DetailInfo detailInfo = new DetailInfo();
        detailInfo.setInfo(info);
        detailInfo.setName(food);
        nutList.add(detailInfo);
        headerInfo.setActionList(nutList);
        //find the group position inside the list
        groupPosition = deptList.indexOf(headerInfo);
        return groupPosition;
    }

    private int updateAction(String daytime, DetailInfo oldDetailInfo, DetailInfo newDetailInfo) {
        int groupPosition = 0;

        HeaderInfo headerInfo = myPlans.get(daytime);
        if (headerInfo != null) {
            ArrayList<DetailInfo> actionList = headerInfo.getActionList();
            actionList.set(actionList.indexOf(oldDetailInfo), newDetailInfo);
        }

        return groupPosition;
    }

    private ExpandableListView.OnChildClickListener myListItemClicked = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            final HeaderInfo headerInfo = deptList.get(groupPosition);
            final DetailInfo detailInfo = headerInfo.getActionList().get(childPosition);
            final String day = headerInfo.getdaytime();
            final String detail = headerInfo.getDetailTime();
            final Dialog dialog = new Dialog(NutritionActivity.this);
            dialog.setContentView(R.layout.dialog_nutrition);
            dialog.setTitle(headerInfo.getdaytime());
            Button okbutton = (Button) dialog.findViewById(R.id.ok);
            Button cancel_bt = (Button) dialog.findViewById(R.id.cancel);
            final AutoCompleteTextView schedule = (AutoCompleteTextView) dialog.findViewById(R.id.schedule_et);
            final EditText info_et = (EditText) dialog.findViewById(R.id.info_et);
            ArrayAdapter schedule_adapter = new ArrayAdapter(NutritionActivity.this, android.R.layout.simple_list_item_1, food_list);
            schedule.setAdapter(schedule_adapter);
            schedule.setThreshold(1);
            schedule.setHint(Food.protein);
            if (detailInfo.getName().equals("+")) {
                schedule.setText("");
                info_et.setText("");
            } else {
                schedule.setText(detailInfo.getName());
                info_et.setText(detailInfo.getInfo());
            }
            okbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean contain = false;
                    ArrayList actionLists = headerInfo.getActionList();
                    String food = "";
                    String info = "";
                    if (schedule.getText() != null) food = schedule.getText().toString();
                    if (info_et.getText() != null) info = info_et.getText().toString();
                    DetailInfo newDetailInfo = new DetailInfo();
                    newDetailInfo.setName(food);
                    newDetailInfo.setInfo(info);
                    if (detailInfo.getName().equals("+")) {
                        for (int i = 0; i < actionLists.size(); i++) {
                            if (newDetailInfo.equals(actionLists.get(i))) contain = true;
                        }
                        if (contain) {
                            Toast.makeText(NutritionActivity.this, Config.exist, Toast.LENGTH_SHORT).show();
                        } else {
                            addMeals(day, detail, food, info);
							dbHelper.updateNutData(day, detailInfo, newDetailInfo);
                        }

                    } else {
                        for (int i = 0; i < actionLists.size(); i++) {
                            if (newDetailInfo.equals(actionLists.get(i))) contain = true;
                        }
                        if (contain) {
                            Toast.makeText(NutritionActivity.this, Config.exist, Toast.LENGTH_SHORT).show();
                        } else {
                            updateAction(day, detailInfo, newDetailInfo);
							dbHelper.updateNutData(day, detailInfo, newDetailInfo);
                        }
                    }

                    mAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
            cancel_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return false;
        }
    };


    private AdapterView.OnItemLongClickListener myListItemLongClicked = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            int itemType = ExpandableListView.getPackedPositionType(id);
            int childPosition = 0, groupPosition = 0;

            if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                childPosition = ExpandableListView.getPackedPositionChild(id);
                groupPosition = ExpandableListView.getPackedPositionGroup(id);
                showChildDeleteDialog(NutritionActivity.this, groupPosition, childPosition);
                //do your per-item callback here
                return true; //true if we consumed the click, false if not

            } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                groupPosition = ExpandableListView.getPackedPositionGroup(id);
                //do your per-group callback here
                HeaderInfo headerInfo = myPlans.get(groups[groupPosition]);
                showGroupDeleteDialog(NutritionActivity.this, groupPosition);

                return true; //true if we consumed the click, false if not

            } else {
                // null item; we don't consume the click
                return false;
            }
        }
    };


    /**
     * A simple adapter which maintains an ArrayList of photo resource Ids. Each
     * photo is displayed as an image. This adapter supports clearing the list
     * of photos and adding a new photo.
     */
    public class MyExpandableListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private ArrayList<HeaderInfo> deptList;

        // Sample data set. children[i] contains the children (String[]) for
        // groups[i].
        public MyExpandableListAdapter(Context context, ArrayList<HeaderInfo> deptList) {
            this.context = context;
            this.deptList = deptList;
        }

        public Object getChild(int groupPosition, int childPosition) {
            ArrayList<DetailInfo> productList = deptList.get(groupPosition).getActionList();
            return productList.get(childPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            ArrayList<DetailInfo> productList = deptList.get(groupPosition).getActionList();
            return productList.size();
        }


//	public View getChildView(int groupPosition, int childPosition,
//							 boolean isLastChild, View convertView, ViewGroup parent) {
//		DetailInfo detailInfo = (DetailInfo) getChild(groupPosition, childPosition);
//		TextView textView = getGenericView();
//		textView.setText(getChild(groupPosition, childPosition).toString());
//		return textView;
//	}

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View view, ViewGroup parent) {

            DetailInfo detailInfo = (DetailInfo) getChild(groupPosition, childPosition);
            if (view == null) {
                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = infalInflater.inflate(R.layout.child_row, null);
            }

            TextView times_tv = (TextView) view.findViewById(R.id.times);
            times_tv.setText(detailInfo.getInfo().trim());
            TextView childItem = (TextView) view.findViewById(R.id.childItem);
            childItem.setText(detailInfo.getName().trim());
            times_tv.setTextColor(tcolor);
            childItem.setTextColor(tcolor);

            return view;
        }


        @Override
        public Object getGroup(int groupPosition) {
            return groups[groupPosition];
        }


        public int getGroupCount() {
            return groups.length;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        /**
         * create group view and bind data to view
         */
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            View v;
            if (convertView == null) {
                v = mInflater.inflate(R.layout.list_item, null);
            } else {
                v = convertView;
            }
            TextView textView = (TextView) v.findViewById(R.id.textView);
            String mealNo = getGroup(groupPosition).toString();
            textView.setText(mealNo);
            textView.setTextColor(tcolor);
            TextView detail_tv = (TextView) v.findViewById(R.id.detailView);
            LinkedHashMap meals = dbHelper.getMealInfo();
            detail_tv.setText(meals.get(mealNo).toString());
            detail_tv.setTextColor(tcolor);
            return v;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

    }

    /**
     * here is very importance for indicator group
     */
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        final ExpandableListView listView = (ExpandableListView) view;

        int npos = view.pointToPosition(0, 0);
        if (npos == AdapterView.INVALID_POSITION)
            return;

        long pos = listView.getExpandableListPosition(npos);
        int childPos = ExpandableListView.getPackedPositionChild(pos);
        int groupPos = ExpandableListView.getPackedPositionGroup(pos);
        if (childPos == AdapterView.INVALID_POSITION) {
            View groupView = listView.getChildAt(npos
                    - listView.getFirstVisiblePosition());
            indicatorGroupHeight = groupView.getHeight();
            indicatorGroup.setVisibility(View.GONE);
        } else {
            indicatorGroup.setVisibility(View.VISIBLE);
        }
        // get an error data, so return now
        if (indicatorGroupHeight == 0) {
            return;
        }
        // update the data of indicator group view
        if (groupPos != indicatorGroupId) {
            mAdapter.getGroupView(groupPos, listView.isGroupExpanded(groupPos),
                    indicatorGroup.getChildAt(0), null);
            indicatorGroupId = groupPos;

            indicatorGroup.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    System.out.println("clidk");
                    listView.collapseGroup(indicatorGroupId);
                }
            });
        }
        if (indicatorGroupId == -1)
            return;

        int showHeight = indicatorGroupHeight;
        int nEndPos = listView.pointToPosition(0, indicatorGroupHeight);
        if (nEndPos == AdapterView.INVALID_POSITION)
            return;
        long pos2 = listView.getExpandableListPosition(nEndPos);
        int groupPos2 = ExpandableListView.getPackedPositionGroup(pos2);
        if (groupPos2 != indicatorGroupId) {
            View viewNext = listView.getChildAt(nEndPos
                    - listView.getFirstVisiblePosition());
            showHeight = viewNext.getTop();
            Log.e(TAG, PRE + "update the show part height of indicator group:"
                    + showHeight);
        }

        // update group position
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) indicatorGroup
                .getLayoutParams();
        layoutParams.topMargin = -(indicatorGroupHeight - showHeight);
        indicatorGroup.setLayoutParams(layoutParams);
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//
//            Intent intent = new Intent(NutritionActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//            return true;
//        } else {
//            return super.onKeyDown(keyCode, event);
//        }
//    }

    public void showGroupDeleteDialog(Context context, final int groupId) {
        final HeaderInfo headerInfo = myPlans.get(groups[groupId]);
        final ArrayList actions = headerInfo.getActionList();
        final String mealNo = headerInfo.getdaytime();
        String[] mItems = getResources().getStringArray(R.array.meals);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(NutritionActivity.this, android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(new String[]{Config.MOVE, Config.COPY, Config.DELETE, Config.EDIT}, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Config.MOVE_ID:
                        final Dialog move_dialog = new Dialog(NutritionActivity.this);
                        move_dialog.setContentView(R.layout.dialog_move);
                        move_dialog.setTitle(mealNo);
                        Button okbutton = (Button) move_dialog.findViewById(R.id.ok);
                        Button cancel_bt = (Button) move_dialog.findViewById(R.id.cancel);
                        final Spinner spinner = (Spinner) move_dialog.findViewById(R.id.weekday);
                        spinner.setAdapter(adapter);
                        okbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean contain = false;
                                String newM = (String) spinner.getSelectedItem();
                                HeaderInfo newHeaderInfo = myPlans.get(groups[spinner.getSelectedItemPosition()]);
                                ArrayList newActions = newHeaderInfo.getActionList();
                                for (int i = 1; i < actions.size(); i++) {
                                    DetailInfo move = (DetailInfo) actions.get(i);
                                    for (int j = 0; j < newActions.size(); j++) {
                                        if (move.equals(newActions.get(j))) contain = true;
                                    }
                                    System.out.println(contain);
                                    if (contain) {
                                        Toast.makeText(NutritionActivity.this, Config.exist, Toast.LENGTH_SHORT).show();
                                    } else {
                                        String title = move.getName();
                                        String info = move.getInfo();
                                        dbHelper.changeNutData(mealNo, title, info, newM);
                                        newActions.add(move);
                                        actions.remove(i);
                                    }
                                    contain = false;
                                }
                                mAdapter.notifyDataSetChanged();
                                move_dialog.dismiss();
                            }
                        });
                        cancel_bt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                move_dialog.dismiss();
                            }
                        });

                        move_dialog.show();
                        break;
                    case Config.COPY_ID:
                        final Dialog copy_dialog = new Dialog(NutritionActivity.this);
                        copy_dialog.setContentView(R.layout.dialog_move);
                        copy_dialog.setTitle(mealNo);
                        Button okbutton_c = (Button) copy_dialog.findViewById(R.id.ok);
                        Button cancel_bt_c = (Button) copy_dialog.findViewById(R.id.cancel);
                        final Spinner spinner_c = (Spinner) copy_dialog.findViewById(R.id.weekday);
                        spinner_c.setAdapter(adapter);
                        okbutton_c.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean contain = false;
                                String newM = (String) spinner_c.getSelectedItem();
                                HeaderInfo newHeaderInfo = myPlans.get(groups[spinner_c.getSelectedItemPosition()]);
                                ArrayList newActions = newHeaderInfo.getActionList();
                                for (int i = 1; i < actions.size(); i++) {
                                    DetailInfo move = (DetailInfo) actions.get(i);
                                    for (int j = 0; j < newActions.size(); j++) {
                                        if (move.equals(newActions.get(j))) contain = true;
                                    }
                                    if (contain) {
                                        Toast.makeText(NutritionActivity.this, Config.exist, Toast.LENGTH_SHORT).show();
                                    } else {
                                        dbHelper.copyNutritionData(mealNo, move.getName(), move.getInfo(), newM);
                                        newActions.add(move);
                                    }
                                    contain = false;
                                }
                                mAdapter.notifyDataSetChanged();
                                copy_dialog.dismiss();
                            }
                        });
                        cancel_bt_c.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                copy_dialog.dismiss();
                            }
                        });

                        copy_dialog.show();
                        break;
                    case Config.DELETE_ID:
                        for (int i = actions.size() - 1; i > 0; i--) {
                            actions.remove(i);
                        }
                        mAdapter.notifyDataSetChanged();
                        dbHelper.deleteOneMeal(headerInfo.getdaytime());
                        break;

                    case Config.EDIT_ID:
                        final Dialog time_dialog = new Dialog(NutritionActivity.this);
                        time_dialog.setContentView(R.layout.dialog_time);
                        time_dialog.setTitle(headerInfo.getdaytime());
                        Button okbutton_e = (Button) time_dialog.findViewById(R.id.ok);
                        Button cancel_bt_e = (Button) time_dialog.findViewById(R.id.cancel);
                        final EditText start_time_et = (EditText) time_dialog.findViewById(R.id.start_time_et);
                        final EditText end_time_et = (EditText) time_dialog.findViewById(R.id.end_time_et);
                        start_time_et.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Calendar mcurrentTime = Calendar.getInstance();
                                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                                int minute = mcurrentTime.get(Calendar.MINUTE);
                                TimePickerDialog mTimePickers;
                                mTimePickers = new TimePickerDialog(NutritionActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        start_time_et.setText(selectedHour + ":" + selectedMinute);
                                    }
                                }, hour, minute, true);//Yes 24 hour time
                                mTimePickers.setTitle(Config.ss_time);
                                mTimePickers.show();
                            }
                        });

                        end_time_et.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                System.out.println("onclick");
                                Calendar mcurrentTime = Calendar.getInstance();
                                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                                int minute = mcurrentTime.get(Calendar.MINUTE);
                                TimePickerDialog mTimePickere;
                                mTimePickere = new TimePickerDialog(NutritionActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        end_time_et.setText(selectedHour + ":" + selectedMinute);
                                    }
                                }, hour, minute, true);//Yes 24 hour time
                                mTimePickere.setTitle(Config.se_time);
                                mTimePickere.show();
                            }
                        });
                        okbutton_e.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String start_t = "";
                                String end_t = "";
                                if (start_time_et.getText() != null)
                                    start_t = start_time_et.getText().toString();
                                if (end_time_et.getText() != null)
                                    end_t = end_time_et.getText().toString();

                                headerInfo.setdaytime(mealNo + " " + start_t + " - " + end_t);
                                dbHelper.updateMealsInfo(mealNo, start_t + " - " + end_t);
                                mAdapter.notifyDataSetChanged();
                                time_dialog.dismiss();
                            }
                        });
                        cancel_bt_e.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                time_dialog.dismiss();
                            }
                        });
                        time_dialog.show();
                        break;

                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }

    public void showChildDeleteDialog(Context context, final int groupId, final int childId) {
        final HeaderInfo headerInfo = myPlans.get(groups[groupId]);
        final ArrayList actions = headerInfo.getActionList();
        final DetailInfo detailInfo = (DetailInfo) actions.get(childId);
        final String mealNo = headerInfo.getdaytime();
        String[] mItems = getResources().getStringArray(R.array.meals);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(NutritionActivity.this, android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(new String[]{Config.MOVE, Config.COPY, Config.DELETE}, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Config.MOVE_ID:
                        final Dialog move_dialog = new Dialog(NutritionActivity.this);
                        move_dialog.setContentView(R.layout.dialog_move);
                        move_dialog.setTitle(mealNo);
                        Button okbutton = (Button) move_dialog.findViewById(R.id.ok);
                        Button cancel_bt = (Button) move_dialog.findViewById(R.id.cancel);
                        final Spinner spinner = (Spinner) move_dialog.findViewById(R.id.weekday);
                        spinner.setAdapter(adapter);
                        okbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean contain = false;
                                String newM = (String) spinner.getSelectedItem();
                                HeaderInfo newHeaderInfo = myPlans.get(groups[spinner.getSelectedItemPosition()]);
                                ArrayList newActions = newHeaderInfo.getActionList();
                                for (int j = 0; j < newActions.size(); j++) {
                                    if (detailInfo.equals(newActions.get(j))) contain = true;
                                }
                                if (contain) {
                                    Toast.makeText(NutritionActivity.this, Config.exist, Toast.LENGTH_SHORT).show();
                                } else {
                                    dbHelper.changeNutData(mealNo, detailInfo.getName(), detailInfo.getInfo(), newM);
                                    newActions.add(detailInfo);
                                    actions.remove(childId);
                                }

                                mAdapter.notifyDataSetChanged();
                                move_dialog.dismiss();
                            }
                        });
                        cancel_bt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                move_dialog.dismiss();
                            }
                        });

                        move_dialog.show();
                        break;

                    case Config.COPY_ID:
                        final Dialog copy_dialog = new Dialog(NutritionActivity.this);
                        copy_dialog.setContentView(R.layout.dialog_move);
                        copy_dialog.setTitle(mealNo);
                        Button okbutton_c = (Button) copy_dialog.findViewById(R.id.ok);
                        Button cancel_bt_c = (Button) copy_dialog.findViewById(R.id.cancel);
                        final Spinner spinner_c = (Spinner) copy_dialog.findViewById(R.id.weekday);
                        spinner_c.setAdapter(adapter);
                        okbutton_c.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean contain = false;
                                String newD = (String) spinner_c.getSelectedItem();
                                HeaderInfo newHeaderInfo = myPlans.get(groups[spinner_c.getSelectedItemPosition()]);
                                ArrayList newActions = newHeaderInfo.getActionList();

                                for (int j = 0; j < newActions.size(); j++) {
                                    if (detailInfo.equals(newActions.get(j))) contain = true;
                                }
                                if (contain) {
                                    Toast.makeText(NutritionActivity.this, Config.exist, Toast.LENGTH_SHORT).show();
                                } else {
                                    dbHelper.updateNutData(newD, detailInfo, detailInfo);
                                    newActions.add(detailInfo);
                                }

                                mAdapter.notifyDataSetChanged();
                                copy_dialog.dismiss();
                            }
                        });
                        cancel_bt_c.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                copy_dialog.dismiss();
                            }
                        });

                        copy_dialog.show();
                        break;

                    case Config.DELETE_ID:
                        if (childId > 0) {
                            dbHelper.deleteOneDish(headerInfo.getdaytime(), detailInfo.getName(), detailInfo.getInfo());
                            actions.remove(childId);
                            mAdapter.notifyDataSetChanged();
                        }
                        break;

                    default:
                        break;
                }
            }
        });
        builder.create().show();
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
                //将处理过的图片显示在界面上，并保存到本地
                getWindow().getDecorView().setBackgroundDrawable(bitdra);
            }
        } else if (wp == Config.COLOR_ID) {
            bcolor = myPreference.getInt(Config.im_ref, bcolor);
            getWindow().getDecorView().setBackgroundColor(bcolor);
        }else {
            getWindow().getDecorView().setBackgroundColor(bcolor);
        }
    }

    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
