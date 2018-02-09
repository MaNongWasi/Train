package com.example.vtec.myapplication;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by VTEC on 3/10/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "BodyHealthy";
    private static final int DB_VERSION = 1;
    private static final String DROP_LOCAL_USER_DATA_TABLE = "DROP TABLE IF EXISTS "
            + DbContractor.LOCAL_USER_DATA_TABLE;
    private static final String DROP_LOCAL_ACTION_DATA_TABLE = "DROP TABLE IF EXISTS "
            + DbContractor.LOCAL_ACTION_DATA_TABLE;
    private static final String DROP_LOCAL_NUTRITION_DATA_TABLE = "DROP TABLE IF EXISTS "
            + DbContractor.LOCAL_NUTRITION_DATA_TABLE;
    private static final String DROP_LOCAL_NUTRITION_TIME_TABLE = "DROP TABLE IF EXISTS "
            + DbContractor.LOCAL_NUTRITION_TIME_TABLE;
    private static final String DROP_LOCAL_USERS_DATA_TABLE = "DROP TABLE IF EXISTS "
            + DbContractor.LOCAL_USERS_DATA_TABLE;
    private static final String CREATE_LOCAL_ACTION_DATA_TABLE = "CREATE TABLE IF NOT EXISTS "
            + DbContractor.LOCAL_ACTION_DATA_TABLE
            + " ("
            + DbContractor.DAYTIME
            + " text NOT NULL, "
            + DbContractor.ACTION
            + " text NOT NULL, "
            + DbContractor.INFO
            + " text NOT NULL)";
    //     + " text NOT NULL, "
//     + DbContractor.IMG
//     + " int)";
    private static final String CREATE_LOCAL_NUTITION_DATA_TABLE = "CREATE TABLE IF NOT EXISTS "
            + DbContractor.LOCAL_NUTRITION_DATA_TABLE
            + " ("
            + DbContractor.MEAL
            + " integer, "
            + DbContractor.FOOD
            + " text NOT NULL, "
            + DbContractor.INFO
            + " text NOT NULL)";
    private static final String CREATE_LOCAL_NUTITION_TIME_TABLE = "CREATE TABLE IF NOT EXISTS "
            + DbContractor.LOCAL_NUTRITION_TIME_TABLE
            + " ("
            + DbContractor.MEAL
            + " integer, "
            + DbContractor.INFO
            + " text NOT NULL)";
    private static final String CREATE_LOCAL_USERS_DATA_TABLE = "CREATE TABLE IF NOT EXISTS "
            + DbContractor.LOCAL_USERS_DATA_TABLE
            + " ("
            + DbContractor.DAYTIME
            + " text, "
            + DbContractor.ACTION
            + " text, "
            + DbContractor.INFO
            + " text, "
            + DbContractor.R1
            + " text NOT NULL, "
            + DbContractor.R2
            + " text NOT NULL, "
            + DbContractor.R3
            + " text NOT NULL, "
            + DbContractor.R4
            + " text NOT NULL, "
            + DbContractor.R5
            + " text NOT NULL, "
            + DbContractor.R6
            + " text NOT NULL, "
            + DbContractor.R7
            + " text NOT NULL, "
            + DbContractor.R8
            + " text NOT NULL, "
            + DbContractor.R9
            + " text NOT NULL, "
            + DbContractor.R10
            + " text NOT NULL)";
    private static final String CREATE_LOCAL_USER_DATA_TABLE = "CREATE TABLE IF NOT EXISTS "
            + DbContractor.LOCAL_USER_DATA_TABLE
            + " ("
            + DbContractor.DAYTIME
            + " text, "
            + DbContractor.ACTION
            + " text, "
            + DbContractor.INFO
            + " text, "
            + DbContractor.R1_1
            + " text NOT NULL, "
            + DbContractor.R1_2
            + " text NOT NULL, "
            + DbContractor.R1_3
            + " text NOT NULL, "
            + DbContractor.R1_4
            + " text NOT NULL, "
            + DbContractor.R1_5
            + " text NOT NULL, "
            + DbContractor.R2_1
            + " text NOT NULL, "
            + DbContractor.R2_2
            + " text NOT NULL, "
            + DbContractor.R2_3
            + " text NOT NULL, "
            + DbContractor.R2_4
            + " text NOT NULL, "
            + DbContractor.R2_5
            + " text NOT NULL, "
            + DbContractor.R3_1
            + " text NOT NULL, "
            + DbContractor.R3_2
            + " text NOT NULL, "
            + DbContractor.R3_3
            + " text NOT NULL, "
            + DbContractor.R3_4
            + " text NOT NULL, "
            + DbContractor.R3_5
            + " text NOT NULL, "
            + DbContractor.R4_1
            + " text NOT NULL, "
            + DbContractor.R4_2
            + " text NOT NULL, "
            + DbContractor.R4_3
            + " text NOT NULL, "
            + DbContractor.R4_4
            + " text NOT NULL, "
            + DbContractor.R4_5
            + " text NOT NULL, "
            + DbContractor.R5_1
            + " text NOT NULL, "
            + DbContractor.R5_2
            + " text NOT NULL, "
            + DbContractor.R5_3
            + " text NOT NULL, "
            + DbContractor.R5_4
            + " text NOT NULL, "
            + DbContractor.R5_5
            + " text NOT NULL, "
            + DbContractor.R6_1
            + " text NOT NULL, "
            + DbContractor.R6_2
            + " text NOT NULL, "
            + DbContractor.R6_3
            + " text NOT NULL, "
            + DbContractor.R6_4
            + " text NOT NULL, "
            + DbContractor.R6_5
            + " text NOT NULL)";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Isensit debug", "Creating database " + DbContractor.LOCAL_USER_DATA_TABLE + "...");

        db.execSQL(CREATE_LOCAL_USER_DATA_TABLE);
        db.execSQL(CREATE_LOCAL_ACTION_DATA_TABLE);
        db.execSQL(CREATE_LOCAL_NUTITION_DATA_TABLE);
        db.execSQL(CREATE_LOCAL_NUTITION_TIME_TABLE);
        db.execSQL(CREATE_LOCAL_USERS_DATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_LOCAL_USER_DATA_TABLE);
        db.execSQL(DROP_LOCAL_ACTION_DATA_TABLE);
        db.execSQL(DROP_LOCAL_NUTRITION_DATA_TABLE);
        db.execSQL(DROP_LOCAL_NUTRITION_TIME_TABLE);
        db.execSQL(DROP_LOCAL_USERS_DATA_TABLE);
        onCreate(db);
    }

    public void updateUserData(UserData data) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Content values for adding data
        ContentValues values = new ContentValues();
        values.put(DbContractor.DAYTIME, data.getDay());
        values.put(DbContractor.ACTION, data.getAction());
        values.put(DbContractor.INFO, data.getAInfo());
        values.put(DbContractor.R1_1, data.getR1_1());
        values.put(DbContractor.R1_2, data.getR1_2());
        values.put(DbContractor.R1_3, data.getR1_3());
        values.put(DbContractor.R1_4, data.getR1_4());
        values.put(DbContractor.R1_5, data.getR1_5());
        values.put(DbContractor.R2_1, data.getR2_1());
        values.put(DbContractor.R2_2, data.getR2_2());
        values.put(DbContractor.R2_3, data.getR2_3());
        values.put(DbContractor.R2_4, data.getR2_4());
        values.put(DbContractor.R2_5, data.getR2_5());
        values.put(DbContractor.R3_1, data.getR3_1());
        values.put(DbContractor.R3_2, data.getR3_2());
        values.put(DbContractor.R3_3, data.getR3_3());
        values.put(DbContractor.R3_4, data.getR3_4());
        values.put(DbContractor.R3_5, data.getR3_5());
        values.put(DbContractor.R4_1, data.getR4_1());
        values.put(DbContractor.R4_2, data.getR4_2());
        values.put(DbContractor.R4_3, data.getR4_3());
        values.put(DbContractor.R4_4, data.getR4_4());
        values.put(DbContractor.R4_5, data.getR4_5());
        values.put(DbContractor.R5_1, data.getR5_1());
        values.put(DbContractor.R5_2, data.getR5_2());
        values.put(DbContractor.R5_3, data.getR5_3());
        values.put(DbContractor.R5_4, data.getR5_4());
        values.put(DbContractor.R5_5, data.getR6_5());
        values.put(DbContractor.R6_1, data.getR6_1());
        values.put(DbContractor.R6_2, data.getR6_2());
        values.put(DbContractor.R6_3, data.getR6_3());
        values.put(DbContractor.R6_4, data.getR6_4());
        values.put(DbContractor.R6_5, data.getR6_5());

        Cursor cursor = db.rawQuery("SELECT * FROM " + DbContractor.LOCAL_USER_DATA_TABLE + " WHERE " + DbContractor.DAYTIME + "=? AND " + DbContractor.ACTION + "=? AND " + DbContractor.INFO + "=?", new String[]{data.getDay(), data.getAction(), data.getAInfo()});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                db.update(DbContractor.LOCAL_USER_DATA_TABLE, values, DbContractor.DAYTIME + "=? AND " + DbContractor.ACTION + "=? AND " + DbContractor.INFO + "=?", new String[]{data.getDay(), data.getAction(), data.getAInfo()});
            } else {
                db.insert(DbContractor.LOCAL_USER_DATA_TABLE, null, values);
            }
        }
        // Inserting row
        db.close();
    }

    public UserData readUserData(String day, String action, String info) {
        SQLiteDatabase db = this.getReadableDatabase();
        UserData user_data = new UserData();

        // need a cursor here
        // the =? , ? replaced by the next parameter as query so gere KEY_ID= String.valueOf(id);
        Cursor cursor = null;
        try {
            cursor = db.query(DbContractor.LOCAL_USER_DATA_TABLE, new String[]{
                            DbContractor.DAYTIME,
                            DbContractor.ACTION,
                            DbContractor.INFO,
                            DbContractor.R1_1,
                            DbContractor.R1_2,
                            DbContractor.R1_3,
                            DbContractor.R1_4,
                            DbContractor.R1_5,
                            DbContractor.R2_1,
                            DbContractor.R2_2,
                            DbContractor.R2_3,
                            DbContractor.R2_4,
                            DbContractor.R2_5,
                            DbContractor.R3_1,
                            DbContractor.R3_2,
                            DbContractor.R3_3,
                            DbContractor.R3_4,
                            DbContractor.R3_5,
                            DbContractor.R4_1,
                            DbContractor.R4_2,
                            DbContractor.R4_3,
                            DbContractor.R4_4,
                            DbContractor.R4_5,
                            DbContractor.R5_1,
                            DbContractor.R5_2,
                            DbContractor.R5_3,
                            DbContractor.R5_4,
                            DbContractor.R5_5,
                            DbContractor.R6_1,
                            DbContractor.R6_2,
                            DbContractor.R6_3,
                            DbContractor.R6_4,
                            DbContractor.R6_5
                    },
                    DbContractor.DAYTIME + "=? AND " + DbContractor.ACTION + "=? AND " + DbContractor.INFO + "=?", new String[]{day, action, info}, null, null, null, null);

//       Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
        } finally {
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() != 0) {
                    user_data.setDay(cursor.getString(0));
                    user_data.setAction(cursor.getString(1));
                    user_data.setAinfo(cursor.getString(2));
                    user_data.setR1_1(cursor.getString(3));
                    user_data.setR1_2(cursor.getString(4));
                    user_data.setR1_3(cursor.getString(5));
                    user_data.setR1_4(cursor.getString(6));
                    user_data.setR1_5(cursor.getString(7));
                    user_data.setR2_1(cursor.getString(8));
                    user_data.setR2_2(cursor.getString(9));
                    user_data.setR2_3(cursor.getString(10));
                    user_data.setR2_4(cursor.getString(11));
                    user_data.setR2_5(cursor.getString(12));
                    user_data.setR3_1(cursor.getString(13));
                    user_data.setR3_2(cursor.getString(14));
                    user_data.setR3_3(cursor.getString(15));
                    user_data.setR3_4(cursor.getString(16));
                    user_data.setR3_5(cursor.getString(17));
                    user_data.setR4_1(cursor.getString(18));
                    user_data.setR4_2(cursor.getString(19));
                    user_data.setR4_3(cursor.getString(20));
                    user_data.setR4_4(cursor.getString(21));
                    user_data.setR4_5(cursor.getString(22));
                    user_data.setR5_1(cursor.getString(23));
                    user_data.setR5_2(cursor.getString(24));
                    user_data.setR5_3(cursor.getString(25));
                    user_data.setR5_4(cursor.getString(26));
                    user_data.setR5_5(cursor.getString(27));
                    user_data.setR6_1(cursor.getString(28));
                    user_data.setR6_2(cursor.getString(29));
                    user_data.setR6_3(cursor.getString(30));
                    user_data.setR6_4(cursor.getString(31));
                    user_data.setR6_5(cursor.getString(32));
                }
                cursor.close();

                return user_data;
            }
        }
        return null;
    }

    public void updateActionData(String daytime, DetailInfo oldDetailInfo, DetailInfo newDetailInfo) {
// String[] action_list = {Actions.side_cr, Actions.dum_sr, Actions.dum_sp, Actions.ma_fp, Actions.cr_fr, Actions.sm_sh, Actions.ab_rc, Actions.leg_ra, Actions.lat_pd, Actions.loopband, Actions.vr_bo, Actions.dum_rb, Actions.v_pd, Actions.bar_dr, Actions.oarm_ex, Actions.rp, Actions.dipjes, Actions.ro_sc, Actions.tw_w, Actions.dum_fly_in, Actions.smi_che_in, Actions.peck_fly, Actions.ch_pr, Actions.push_up, Actions.bal_cr, Actions.leg_up, Actions.crosstrainer, Actions.leg_curl, Actions.leg_ex, Actions.leg_press, Actions.leg_press_in, Actions.sm_sq, Actions.box_spring, Actions.dum_curl, Actions.bar_curl, Actions.ham_curl, Actions.lig_cr, Actions.fietsen};
// int[] action_im_list = {Actions.side_cr_im, Actions.dum_sr_im, Actions.dum_sp_im, Actions.ma_fp_im, Actions.cr_fr_im, Actions.sm_sh_im, Actions.ab_rc_im, Actions.leg_ra_im, Actions.lat_pd_im, Actions.loopband_im, Actions.vr_bo_im, Actions.dum_rb_im, Actions.v_pd_im, Actions.bar_dr_im, Actions.oarm_ex_im, Actions.rp_im, Actions.dipjes_im, Actions.ro_sc_im, Actions.tw_w_im, Actions.dum_fly_in_im, Actions.smi_che_in_im, Actions.peck_fly_im, Actions.ch_pr_im, Actions.push_up_im, Actions.bal_cr_im, Actions.leg_up_im, Actions.crosstrainer_im, Actions.leg_curl_im, Actions.leg_ex_im, Actions.leg_press_im, Actions.leg_press_in_im, Actions.sm_sq_im, Actions.box_spring_im, Actions.dum_curl_im, Actions.bar_curl_im, Actions.ham_curl_im, Actions.lig_cr_im, Actions.fietsen_im};
// List<String> actions = new ArrayList<String>(Arrays.asList(action_list));

        SQLiteDatabase db = this.getWritableDatabase();
        // Content values for adding data
        ContentValues values = new ContentValues();
        values.put(DbContractor.DAYTIME, daytime);
        values.put(DbContractor.ACTION, newDetailInfo.getName());
        values.put(DbContractor.INFO, newDetailInfo.getInfo());
// if (actions.indexOf(action) != -1){
//     values.put(DbContractor.IMG, action_im_list[actions.indexOf(action)]);
// }else {
//     values.put(DbContractor.IMG, R.drawable.background);
// }

        Cursor cursor = db.rawQuery("SELECT * FROM " + DbContractor.LOCAL_ACTION_DATA_TABLE + " WHERE " + DbContractor.DAYTIME + "=? AND " + DbContractor.ACTION + "=? AND " + DbContractor.INFO + "=?", new String[]{daytime, oldDetailInfo.getName(), oldDetailInfo.getInfo()});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                db.update(DbContractor.LOCAL_ACTION_DATA_TABLE, values, DbContractor.DAYTIME + "=? AND " + DbContractor.ACTION + "=? AND " + DbContractor.INFO + "=?", new String[]{daytime, oldDetailInfo.getName(), oldDetailInfo.getInfo()});
            } else {
                db.insert(DbContractor.LOCAL_ACTION_DATA_TABLE, null, values);
            }
        }
        // Inserting row
        db.close();
    }

    public List<HashMap<String, DetailInfo>> getActionPlans() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<HashMap<String, DetailInfo>> plan = new ArrayList<HashMap<String, DetailInfo>>();
        HashMap<String, DetailInfo> action = new HashMap<>();
        DetailInfo detailInfo;

        // need a cursor here
        // the =? , ? replaced by the next parameter as query so gere KEY_ID= String.valueOf(id);
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM " + DbContractor.LOCAL_ACTION_DATA_TABLE, null);
//       Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
        if (cursor != null) {
            // looping through all rows and adding to list
            while (cursor.moveToNext()) {
                action = new HashMap<String, DetailInfo>();
                detailInfo = new DetailInfo();
                detailInfo.setName(cursor.getString(1));
                detailInfo.setInfo(cursor.getString(2));
                // adding User to list
                action.put(cursor.getString(0), detailInfo);
                plan.add(action);
            }
            cursor.close();
        }
        db.close();
        return plan;
    }

    public ArrayList<DetailInfo> getDayPlan(String today) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DetailInfo> actionList = new ArrayList<>();
        DetailInfo detailInfo;
        String[] action_list = {Actions.side_cr, Actions.dum_sr, Actions.dum_sp, Actions.ma_fp, Actions.cr_fr, Actions.sm_sh, Actions.ab_rc, Actions.leg_ra, Actions.lat_pd, Actions.loopband, Actions.vr_bo, Actions.dum_rb, Actions.v_pd, Actions.bar_dr, Actions.oarm_ex, Actions.rp, Actions.dipjes, Actions.ro_sc, Actions.tw_w, Actions.dum_fly_in, Actions.smi_che_in, Actions.peck_fly, Actions.ch_pr, Actions.push_up, Actions.bal_cr, Actions.leg_up, Actions.crosstrainer, Actions.leg_curl, Actions.leg_ex, Actions.leg_press, Actions.leg_press_in, Actions.sm_sq, Actions.box_spring, Actions.dum_curl, Actions.bar_curl, Actions.ham_curl, Actions.lig_cr, Actions.fietsen, Actions.leg_ex_one, Actions.fs, Actions.wlun_step, Actions.dl_sm_step, Actions.hip_thrust, Actions.leg_cur_stand, Actions.up_row, Actions.la_bar_press, Actions.cardio, Actions.dum_cp_in, Actions.pd_re, Actions.pu_step, Actions.dl_half, Actions.dum_up, Actions.wide_grip, Actions.back_ex, Actions.peck_deck, Actions.wave, Actions.cycle, Actions.wl, Actions.cp_pl, Actions.pectoral_fly, Actions.in_cp, Actions.cable_cross, Actions.flat_dp, Actions.s_lc, Actions.hs, Actions.s_vp, Actions.multi_hip, Actions.cr_fly, Actions.s_np, Actions.wg_ur, Actions.bo_fr, Actions.sp_pl, Actions.dum_shrug, Actions.pd_pl, Actions.vr_pl, Actions.wg_r_pl, Actions.po, Actions.bo_cvr, Actions.deadlift, Actions.dum_cp, Actions.abd, Actions.dum_cf, Actions.sl_fly, Actions.jump_s, Actions.add};
        int[] action_im_list = {Actions.side_cr_im, Actions.dum_sr_im, Actions.dum_sp_im, Actions.ma_fp_im, Actions.cr_fr_im, Actions.sm_sh_im, Actions.ab_rc_im, Actions.leg_ra_im, Actions.lat_pd_im, Actions.loopband_im, Actions.vr_bo_im, Actions.dum_rb_im, Actions.v_pd_im, Actions.bar_dr_im, Actions.oarm_ex_im, Actions.rp_im, Actions.dipjes_im, Actions.ro_sc_im, Actions.tw_w_im, Actions.dum_fly_in_im, Actions.smi_che_in_im, Actions.peck_fly_im, Actions.ch_pr_im, Actions.push_up_im, Actions.bal_cr_im, Actions.leg_up_im, Actions.crosstrainer_im, Actions.leg_curl_im, Actions.leg_ex_im, Actions.leg_press_im, Actions.leg_press_in_im, Actions.sm_sq_im, Actions.box_spring_im, Actions.dum_curl_im, Actions.bar_curl_im, Actions.ham_curl_im, Actions.lig_cr_im, Actions.fietsen_im, Actions.leg_ex_one_im, Actions.fs_im, Actions.wlun_step_im, Actions.dl_sm_step_im, Actions.hip_thrust_im, Actions.leg_cur_stand_im, Actions.up_row_im, Actions.la_bar_press_im, Actions.cardio_im, Actions.dum_cp_in_im, Actions.pd_re_im, Actions.pu_step_im, Actions.dl_half_im, Actions.dum_up_im, Actions.wide_grip_im, Actions.back_ex_im, Actions.peck_deck_im, Actions.wave_im, Actions.cycle_im, Actions.wl_im, Actions.cp_pl_im, Actions.pectoral_fly_im, Actions.in_cp_im, Actions.cable_cross_im, Actions.flat_dp_im, Actions.s_lc_im, Actions.hs_im, Actions.s_vp_im, Actions.multi_hip_im, Actions.cr_fly_im, Actions.s_np_im, Actions.wg_ur_im, Actions.bo_fr_im, Actions.sp_pl_im, Actions.dum_shrug_im, Actions.pd_pl_im, Actions.vr_pl_im, Actions.wg_r_pl_im, Actions.po_im, Actions.bo_cvr_im, Actions.deadlift_im, Actions.dum_cp_im, Actions.abd_im, Actions.dum_cf_im, Actions.sl_fly_im, Actions.jump_s_im, Actions.add_im};
        List<String> actions = new ArrayList<String>(Arrays.asList(action_list));
        // need a cursor here
        // the =? , ? replaced by the next parameter as query so gere KEY_ID= String.valueOf(id);
// Cursor cursor = db.query(DbContractor.LOCAL_ACTION_DATA_TABLE, new String[]{DbContractor.ACTION, DbContractor.INFO, DbContractor.IMG}, DbContractor.DAYTIME + "=?", new String[]{today}, null, null, null, null);
        Cursor cursor = db.query(DbContractor.LOCAL_ACTION_DATA_TABLE, new String[]{DbContractor.ACTION, DbContractor.INFO}, DbContractor.DAYTIME + "=?", new String[]{today}, null, null, null, null);

//       Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
        if (cursor != null) {
            // looping through all rows and adding to list
            while (cursor.moveToNext()) {
                detailInfo = new DetailInfo();
                detailInfo.setName(cursor.getString(0));
                detailInfo.setInfo(cursor.getString(1));
                if (actions.indexOf(detailInfo.getName()) != -1) {
                    detailInfo.setImg(action_im_list[actions.indexOf(detailInfo.getName())]);
                } else {
                    detailInfo.setImg(R.drawable.background);
                }
//  detailInfo.setImg(cursor.getInt(2));
//   adding User to list
                actionList.add(detailInfo);
            }
            cursor.close();
        }
        db.close();

        return actionList;
    }


    public void updateNutData(String mealNo, DetailInfo oldDetailInfo, DetailInfo newDetailInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Content values for adding data
        ContentValues values = new ContentValues();
        values.put(DbContractor.MEAL, mealNo);
        values.put(DbContractor.FOOD, newDetailInfo.getName());
        values.put(DbContractor.INFO, newDetailInfo.getInfo());

        Cursor cursor = db.rawQuery("SELECT * FROM " + DbContractor.LOCAL_NUTRITION_DATA_TABLE + " WHERE " + DbContractor.MEAL + "=? AND " + DbContractor.FOOD + "=? AND " + DbContractor.INFO + "=?", new String[]{mealNo, oldDetailInfo.getName(), oldDetailInfo.getInfo()});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                db.update(DbContractor.LOCAL_NUTRITION_DATA_TABLE, values, DbContractor.MEAL + "=? AND " + DbContractor.FOOD + "=? AND " + DbContractor.INFO + "=?", new String[]{mealNo, oldDetailInfo.getName(), oldDetailInfo.getInfo()});
            } else {
                db.insert(DbContractor.LOCAL_NUTRITION_DATA_TABLE, null, values);
            }
        }
        // Inserting row
        db.close();
    }

    public List<HashMap<String, DetailInfo>> getNutPlans() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<HashMap<String, DetailInfo>> plan = new ArrayList<HashMap<String, DetailInfo>>();
        HashMap<String, DetailInfo> action = new HashMap<>();
        DetailInfo detailInfo;

        // need a cursor here
        // the =? , ? replaced by the next parameter as query so gere KEY_ID= String.valueOf(id);
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM " + DbContractor.LOCAL_NUTRITION_DATA_TABLE, null);
//       Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
        if (cursor != null) {
            // looping through all rows and adding to list
            while (cursor.moveToNext()) {
                action = new HashMap<String, DetailInfo>();
                detailInfo = new DetailInfo();
                detailInfo.setName(cursor.getString(1));
                detailInfo.setInfo(cursor.getString(2));
                // adding User to list
                action.put(cursor.getString(0), detailInfo);
                plan.add(action);
            }
            cursor.close();
        }
        db.close();
        return plan;
    }

    // Delete data
    public void deleteOneAction(String daytime, String action, String info) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DbContractor.LOCAL_ACTION_DATA_TABLE, DbContractor.DAYTIME + "=? AND " + DbContractor.ACTION + "=? AND " + DbContractor.INFO + "=?", new String[]{daytime, action, info});
        db.delete(DbContractor.LOCAL_USER_DATA_TABLE, DbContractor.DAYTIME + "=? AND " + DbContractor.ACTION + "=? AND " + DbContractor.INFO + "=?", new String[]{daytime, action, info});
        db.close();
    }

    public void deleteDayAction(String daytime) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DbContractor.LOCAL_ACTION_DATA_TABLE, DbContractor.DAYTIME + "= ?", new String[]{daytime});
        db.delete(DbContractor.LOCAL_USER_DATA_TABLE, DbContractor.DAYTIME + "= ?", new String[]{daytime});
        db.close();
    }

    // Delete data
    public void deleteOneDish(String mealNo, String food, String info) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DbContractor.LOCAL_NUTRITION_DATA_TABLE, DbContractor.MEAL + "=? AND " + DbContractor.FOOD + "=? AND " + DbContractor.INFO + "=?", new String[]{mealNo, food, info});
        db.close();
    }

    public void deleteOneMeal(String mealNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DbContractor.LOCAL_NUTRITION_DATA_TABLE, DbContractor.MEAL + "= ?", new String[]{mealNo});
        db.close();
    }

    public void changeActionData(String daytime, String action, String info, String newD) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Content values for adding data
        ContentValues values = new ContentValues();
        values.put(DbContractor.DAYTIME, newD);
        values.put(DbContractor.ACTION, action);
        values.put(DbContractor.INFO, info);

        db.update(DbContractor.LOCAL_ACTION_DATA_TABLE, values, DbContractor.DAYTIME + "=? AND " + DbContractor.ACTION + "=? AND " + DbContractor.INFO + "=?", new String[]{daytime, action, info});

        db.close();
    }

    public void copyActionData(String daytime, String action, String info, String newD) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // Content values for adding data
        values.put(DbContractor.DAYTIME, newD);
        values.put(DbContractor.ACTION, action);
        values.put(DbContractor.INFO, info);

        db.insert(DbContractor.LOCAL_ACTION_DATA_TABLE, null, values);

        db.close();
    }

    public void copyNutritionData(String daytime, String action, String info, String newD) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // Content values for adding data
        values.put(DbContractor.MEAL, newD);
        values.put(DbContractor.FOOD, action);
        values.put(DbContractor.INFO, info);

        db.insert(DbContractor.LOCAL_NUTRITION_DATA_TABLE, null, values);

        db.close();
    }

	public void changeNutData(String mealNo, String food, String info, String newD) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Content values for adding data
        ContentValues values = new ContentValues();
        values.put(DbContractor.MEAL, newD);
        values.put(DbContractor.FOOD, food);
        values.put(DbContractor.INFO, info);

        Cursor cursor = db.rawQuery("SELECT * FROM " + DbContractor.LOCAL_NUTRITION_DATA_TABLE + " WHERE " + DbContractor.MEAL + "=? AND " + DbContractor.FOOD + "=? AND " + DbContractor.INFO + "=?", new String[]{mealNo, food, info});

        if (cursor != null) {
            db.update(DbContractor.LOCAL_NUTRITION_DATA_TABLE, values, DbContractor.MEAL + "=? AND " + DbContractor.FOOD + "=? AND " + DbContractor.INFO + "=?", new String[]{mealNo, food, info});
        }
        // Inserting row
        db.close();
    }


    public void addMealsInfo() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContractor.MEAL, Config.M1);
        values.put(DbContractor.INFO, "");
        db.insert(DbContractor.LOCAL_NUTRITION_TIME_TABLE, null, values);

        values = new ContentValues();
        values.put(DbContractor.MEAL, Config.M2);
        values.put(DbContractor.INFO, "");
        db.insert(DbContractor.LOCAL_NUTRITION_TIME_TABLE, null, values);

        values = new ContentValues();
        values.put(DbContractor.MEAL, Config.M3);
        values.put(DbContractor.INFO, "");
        db.insert(DbContractor.LOCAL_NUTRITION_TIME_TABLE, null, values);

        values = new ContentValues();
        values.put(DbContractor.MEAL, Config.M4);
        values.put(DbContractor.INFO, "");
        db.insert(DbContractor.LOCAL_NUTRITION_TIME_TABLE, null, values);

        values = new ContentValues();
        values.put(DbContractor.MEAL, Config.M5);
        values.put(DbContractor.INFO, "");
        db.insert(DbContractor.LOCAL_NUTRITION_TIME_TABLE, null, values);

        values = new ContentValues();
        values.put(DbContractor.MEAL, Config.M6);
        values.put(DbContractor.INFO, "");
        db.insert(DbContractor.LOCAL_NUTRITION_TIME_TABLE, null, values);

        db.close();
    }

    public void updateMealsInfo(String mealNo, String info) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContractor.MEAL, mealNo);
        values.put(DbContractor.INFO, info);

        db.update(DbContractor.LOCAL_NUTRITION_TIME_TABLE, values, DbContractor.MEAL + "=?", new String[]{mealNo});
        db.close();
    }

    public LinkedHashMap getMealInfo() {
        LinkedHashMap<String, String> meals = new LinkedHashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DbContractor.LOCAL_NUTRITION_TIME_TABLE, null);
        while (cursor.moveToNext()) {
            meals.put(cursor.getString(0), cursor.getString(1));
        }
        db.close();
        return meals;
    }

    public List readUsersData(String day, String action, String info) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> last_weights = new ArrayList<>();
        // need a cursor here
        // the =? , ? replaced by the next parameter as query so gere KEY_ID= String.valueOf(id);
        Cursor cursor = null;
        try {
            cursor = db.query(DbContractor.LOCAL_USERS_DATA_TABLE, new String[]{
                            DbContractor.R1,
                            DbContractor.R2,
                            DbContractor.R3,
                            DbContractor.R4,
                            DbContractor.R5,
                            DbContractor.R6,
                            DbContractor.R7,
                            DbContractor.R8,
                            DbContractor.R9,
                            DbContractor.R10,
                    },
                    DbContractor.ACTION + "=? AND " + DbContractor.INFO + "=?", new String[]{action, info}, null, null, null, null);

//       Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
        } finally {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    for (int i = 0; i < 10; i++) {
                        last_weights.add(cursor.getString(i));
                    }
                } else {
                    for (int i = 0; i < 10; i++) {
                        last_weights.add("");
                    }
                }

                cursor.close();

                return last_weights;
            }
        }
        return null;
    }

    public void updateUsersData(String day, String action, String info, List<String> data) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Content values for adding data
        ContentValues values = new ContentValues();
        values.put(DbContractor.DAYTIME, day);
        values.put(DbContractor.ACTION, action);
        values.put(DbContractor.INFO, info);
        values.put(DbContractor.R1, data.get(0));
        values.put(DbContractor.R2, data.get(1));
        values.put(DbContractor.R3, data.get(2));
        values.put(DbContractor.R4, data.get(3));
        values.put(DbContractor.R5, data.get(4));
        values.put(DbContractor.R6, data.get(5));
        values.put(DbContractor.R7, data.get(6));
        values.put(DbContractor.R8, data.get(7));
        values.put(DbContractor.R9, data.get(8));
        values.put(DbContractor.R10, data.get(9));

        Cursor cursor = db.rawQuery("SELECT * FROM " + DbContractor.LOCAL_USERS_DATA_TABLE + " WHERE " + DbContractor.DAYTIME + "=? AND " + DbContractor.ACTION + "=? AND " + DbContractor.INFO + "=?", new String[]{day, action, info});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                db.update(DbContractor.LOCAL_USERS_DATA_TABLE, values, DbContractor.DAYTIME + "=? AND " + DbContractor.ACTION + "=? AND " + DbContractor.INFO + "=?", new String[]{day, action, info});
            } else {
                db.insert(DbContractor.LOCAL_USERS_DATA_TABLE, null, values);
            }
        }
        // Inserting row
        db.close();
    }

    // Delete data
    public void deleteOneActionImg(String daytime, String action, String info) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DbContractor.LOCAL_ACTION_DATA_TABLE, DbContractor.DAYTIME + "=? AND " + DbContractor.ACTION + "=? AND " + DbContractor.INFO + "=?", new String[]{daytime, action, info});
        db.delete(DbContractor.LOCAL_USERS_DATA_TABLE, DbContractor.DAYTIME + "=? AND " + DbContractor.ACTION + "=? AND " + DbContractor.INFO + "=?", new String[]{daytime, action, info});
        db.close();
    }

    public void deleteDayActionImg(String daytime) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DbContractor.LOCAL_ACTION_DATA_TABLE, DbContractor.DAYTIME + "= ?", new String[]{daytime});
        db.delete(DbContractor.LOCAL_USERS_DATA_TABLE, DbContractor.DAYTIME + "= ?", new String[]{daytime});
        db.close();
    }
}