package com.example.vtec.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

/**
 * Created by VTEC on 3/10/2016.
 */
public class ScheduleActivity extends Activity implements OnScrollListener {
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
    private String[] action_list, info_list, groups;
    //    String[] action_list = {Actions.side_cr, Actions.dum_sr, Actions.dum_sp, Actions.ma_fp, Actions.cr_fr, Actions.sm_sh, Actions.ab_rc, Actions.leg_ra, Actions.lat_pd, Actions.loopband, Actions.vr_bo, Actions.dum_rb, Actions.v_pd, Actions.bar_dr, Actions.oarm_ex, Actions.rp, Actions.dipjes, Actions.ro_sc, Actions.tw_w, Actions.dum_fly_in, Actions.smi_che_in, Actions.peck_fly, Actions.ch_pr, Actions.push_up, Actions.bal_cr, Actions.leg_up, Actions.crosstrainer, Actions.leg_curl, Actions.leg_ex, Actions.leg_press, Actions.leg_press_in, Actions.sm_sq, Actions.box_spring, Actions.dum_curl, Actions.bar_curl, Actions.ham_curl, Actions.lig_cr, Actions.fietsen};
//    String[] info_list = {Actions.five_times, Actions.three_fifteen, Actions.three_ten, Actions.four_fifteen, Actions.ten_twelve, Actions.three_ts, Actions.fst_seven, Actions.twenty_min, Actions.loop_de, Actions.three_twelve};
//    private String[] groups = {Config.MON, Config.TUE, Config.WED, Config.THU, Config.FRI, Config.SAT, Config.SUN};
    private int bcolor = Color.BLACK, tcolor = Color.WHITE;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView = (ExpandableListView) findViewById(R.id.expandableListView);
        indicatorGroup = (FrameLayout) findViewById(R.id.topGroup);
        action_list = getResources().getStringArray(R.array.actions);
        info_list = getResources().getStringArray(R.array.action_info);
        groups = getResources().getStringArray(R.array.weekday);

        dbHelper = new DatabaseHelper(this);

        loadData(dbHelper.getActionPlans());
        //attach the adapter to the list
        mAdapter = new MyExpandableListAdapter(ScheduleActivity.this, deptList);
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
        addActions(Config.MON, "+", "");
        addActions(Config.TUE, "+", "");
        addActions(Config.WED, "+", "");
        addActions(Config.THU, "+", "");
        addActions(Config.FRI, "+", "");
        addActions(Config.SAT, "+", "");
        addActions(Config.SUN, "+", "");

        for (int i = 0; i < plan.size(); i++) {
            HashMap<String, DetailInfo> group = plan.get(i);
            Iterator iterator = group.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                DetailInfo detailInfo = (DetailInfo) entry.getValue();
                addActions((String) entry.getKey(), detailInfo.getName(), detailInfo.getInfo());
            }
        }

    }

    //here we maintain our products in various departments
    private int addActions(String daytime, String action, String info) {

        int groupPosition = 0;

        //check the hash map if the group already exists
        HeaderInfo headerInfo = myPlans.get(daytime);
        //add the group if doesn't exists
        if (headerInfo == null) {
            headerInfo = new HeaderInfo();
            headerInfo.setdaytime(daytime);
            myPlans.put(daytime, headerInfo);
            deptList.add(headerInfo);
        }

        //get the children for the group
        ArrayList<DetailInfo> actionList = headerInfo.getActionList();
        //size of the children list
//        int listSize = productList.size();
//        //add to the counter
//        listSize++;

        //create a new child and add that to the group
        DetailInfo detailInfo = new DetailInfo();
        detailInfo.setInfo(info);
        detailInfo.setName(action);
        actionList.add(detailInfo);
        headerInfo.setActionList(actionList);
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
            final Dialog dialog = new Dialog(ScheduleActivity.this);
            dialog.setContentView(R.layout.dialog_action);
            dialog.setTitle(headerInfo.getdaytime());
            Button okbutton = (Button) dialog.findViewById(R.id.ok);
            Button cancel_bt = (Button) dialog.findViewById(R.id.cancel);
            final AutoCompleteTextView schedule = (AutoCompleteTextView) dialog.findViewById(R.id.schedule_et);
            final AutoCompleteTextView info_et = (AutoCompleteTextView) dialog.findViewById(R.id.info_et);
            ArrayAdapter schedule_adapter = new ArrayAdapter(ScheduleActivity.this, android.R.layout.simple_list_item_1, action_list);
            ArrayAdapter info_adapter = new ArrayAdapter(ScheduleActivity.this, android.R.layout.simple_list_item_1, info_list);
            schedule.setAdapter(schedule_adapter);
            info_et.setAdapter(info_adapter);
            schedule.setThreshold(1);
            info_et.setThreshold(1);
            schedule.setHint(Actions.leg_press_in);
            info_et.setHint(Actions.three_fifteen);
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
                    String group_action = "";
                    String info = "";
                    if (schedule.getText() != null) group_action = schedule.getText().toString();
                    if (info_et.getText() != null) info = info_et.getText().toString();
                    DetailInfo newDetailInfo = new DetailInfo();
                    newDetailInfo.setName(group_action);
                    newDetailInfo.setInfo(info);
                    if (detailInfo.getName().equals("+")) {
                        for (int i = 0; i < actionLists.size(); i++) {
                            if (newDetailInfo.equals(actionLists.get(i))) contain = true;
                        }
                        if (contain) {
                            Toast.makeText(ScheduleActivity.this, Config.exist, Toast.LENGTH_SHORT).show();
                        } else {
                            addActions(day, group_action, info);
							dbHelper.updateActionData(day, detailInfo, newDetailInfo);
                        }

                    } else {
                        for (int i = 0; i < actionLists.size(); i++) {
                            if (newDetailInfo.equals(actionLists.get(i))) contain = true;
                        }
                        if (contain) {
                            Toast.makeText(ScheduleActivity.this, Config.exist, Toast.LENGTH_SHORT).show();
                        } else {
                            updateAction(day, detailInfo, newDetailInfo);
							dbHelper.updateActionData(day, detailInfo, newDetailInfo);
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
                showChildDeleteDialog(ScheduleActivity.this, groupPosition, childPosition);
                //do your per-item callback here
                return true; //true if we consumed the click, false if not

            } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                groupPosition = ExpandableListView.getPackedPositionGroup(id);
                //do your per-group callback here
                HeaderInfo headerInfo = myPlans.get(groups[groupPosition]);
                showGroupDeleteDialog(ScheduleActivity.this, groupPosition);

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
        private int color;

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

            textView.setText(getGroup(groupPosition).toString());
            textView.setTextColor(tcolor);
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
//            Intent intent = new Intent(ScheduleActivity.this, MainActivity.class);
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
        final String day = headerInfo.getdaytime();
        final int size = actions.size();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(new String[]{Config.MOVE, Config.COPY, Config.DELETE}, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Config.MOVE_ID:
                        final Dialog move_dialog = new Dialog(ScheduleActivity.this);
                        move_dialog.setContentView(R.layout.dialog_move);
                        move_dialog.setTitle(day);
                        Button okbutton = (Button) move_dialog.findViewById(R.id.ok);
                        Button cancel_bt = (Button) move_dialog.findViewById(R.id.cancel);
                        final Spinner spinner = (Spinner) move_dialog.findViewById(R.id.weekday);

                        okbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean contain = false;
                                String newD = (String) spinner.getSelectedItem();
                                HeaderInfo newHeaderInfo = myPlans.get(groups[spinner.getSelectedItemPosition()]);
                                ArrayList newActions = newHeaderInfo.getActionList();
                                for (int i = 1; i < actions.size(); i++) {
                                    DetailInfo move = (DetailInfo) actions.get(i);
                                    for (int j = 0; j < newActions.size(); j++) {
                                        if (move.equals(newActions.get(j))) contain = true;
                                    }
                                    if (contain) {
                                        Toast.makeText(ScheduleActivity.this, Config.exist, Toast.LENGTH_SHORT).show();
                                    } else {
                                        String title = move.getName();
                                        String info = move.getInfo();
                                        dbHelper.changeActionData(day, title, info, newD);
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
                        final Dialog copy_dialog = new Dialog(ScheduleActivity.this);
                        copy_dialog.setContentView(R.layout.dialog_move);
                        copy_dialog.setTitle(day);
                        Button okbutton_c = (Button) copy_dialog.findViewById(R.id.ok);
                        Button cancel_bt_c = (Button) copy_dialog.findViewById(R.id.cancel);
                        final Spinner spinner_c = (Spinner) copy_dialog.findViewById(R.id.weekday);
                        okbutton_c.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean contain = false;
                                String newD = (String) spinner_c.getSelectedItem();
                                HeaderInfo newHeaderInfo = myPlans.get(groups[spinner_c.getSelectedItemPosition()]);
                                ArrayList newActions = newHeaderInfo.getActionList();
                                for (int i = 1; i < actions.size(); i++) {
                                    DetailInfo move = (DetailInfo) actions.get(i);
                                    for (int j = 0; j < newActions.size(); j++) {
                                        if (move.equals(newActions.get(j))) contain = true;
                                    }
                                    if (contain) {
                                        Toast.makeText(ScheduleActivity.this, Config.exist, Toast.LENGTH_SHORT).show();
                                    } else {
                                        String title = move.getName();
                                        String info = move.getInfo();
                                        dbHelper.copyActionData(day, title, info, newD);
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
                        dbHelper.deleteDayActionImg(headerInfo.getdaytime());
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
        final String day = headerInfo.getdaytime();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(new String[]{Config.MOVE, Config.COPY, Config.DELETE}, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Config.MOVE_ID:
                        final Dialog move_dialog = new Dialog(ScheduleActivity.this);
                        move_dialog.setContentView(R.layout.dialog_move);
                        move_dialog.setTitle(day);
                        Button okbutton = (Button) move_dialog.findViewById(R.id.ok);
                        Button cancel_bt = (Button) move_dialog.findViewById(R.id.cancel);
                        final Spinner spinner = (Spinner) move_dialog.findViewById(R.id.weekday);
//                        String[] mItems = getResources().getStringArray(R.array.weekday);
//                        ArrayAdapter<String> adapter=new ArrayAdapter<String>(ScheduleActivity.this,android.R.layout.simple_spinner_item, mItems);
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        spinner.setAdapter(adapter);
                        okbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean contain = false;
                                String newD = (String) spinner.getSelectedItem();
                                HeaderInfo newHeaderInfo = myPlans.get(groups[spinner.getSelectedItemPosition()]);
                                ArrayList newActions = newHeaderInfo.getActionList();
                                for (int j = 0; j < newActions.size(); j++) {
                                    if (detailInfo.equals(newActions.get(j))) contain = true;
                                }
                                if (contain) {
                                    Toast.makeText(ScheduleActivity.this, Config.exist, Toast.LENGTH_SHORT).show();
                                } else {
                                    dbHelper.changeActionData(day, detailInfo.getName(), detailInfo.getInfo(), newD);
                                    newActions.add(detailInfo);
                                    actions.remove(childId);

                                    mAdapter.notifyDataSetChanged();
                                }
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
                        final Dialog copy_dialog = new Dialog(ScheduleActivity.this);
                        copy_dialog.setContentView(R.layout.dialog_move);
                        copy_dialog.setTitle(day);
                        Button okbutton_c = (Button) copy_dialog.findViewById(R.id.ok);
                        Button cancel_bt_c = (Button) copy_dialog.findViewById(R.id.cancel);
                        final Spinner spinner_c = (Spinner) copy_dialog.findViewById(R.id.weekday);
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
                                    Toast.makeText(ScheduleActivity.this, Config.exist, Toast.LENGTH_SHORT).show();
                                } else {
                                    dbHelper.copyActionData(day, detailInfo.getName(), detailInfo.getInfo(), newD);
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
                            dbHelper.deleteOneActionImg(headerInfo.getdaytime(), detailInfo.getName(), detailInfo.getInfo());
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
