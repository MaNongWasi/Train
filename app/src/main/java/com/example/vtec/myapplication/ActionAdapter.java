package com.example.vtec.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by VTEC on 3/18/2016.
 */
public class ActionAdapter extends ArrayAdapter<DetailInfo> {
    int color;
    // declaring our ArrayList of items
    private ArrayList<DetailInfo> objects;
//    String[] action_list = {Actions.side_cr, Actions.dum_sr, Actions.dum_sp, Actions.ma_fp, Actions.cr_fr, Actions.sm_sh, Actions.ab_rc, Actions.leg_ra, Actions.lat_pd, Actions.loopband, Actions.vr_bo, Actions.dum_rb, Actions.v_pd, Actions.bar_dr, Actions.oarm_ex, Actions.rp, Actions.dipjes, Actions.ro_sc, Actions.tw_w, Actions.dum_fly_in, Actions.smi_che_in, Actions.peck_fly, Actions.ch_pr, Actions.push_up, Actions.bal_cr, Actions.leg_up, Actions.crosstrainer, Actions.leg_curl, Actions.leg_ex, Actions.leg_press, Actions.leg_press_in, Actions.sm_sq, Actions.box_spring, Actions.dum_curl, Actions.bar_curl, Actions.ham_curl, Actions.lig_cr, Actions.fietsen};
//    int[] action_im_list = {Actions.side_cr_im, Actions.dum_sr_im, Actions.dum_sp_im, Actions.ma_fp_im, Actions.cr_fr_im, Actions.sm_sh_im, Actions.ab_rc_im, Actions.leg_ra_im, Actions.lat_pd_im, Actions.loopband_im, Actions.vr_bo_im, Actions.dum_rb_im, Actions.v_pd_im, Actions.bar_dr_im, Actions.oarm_ex_im, Actions.rp_im, Actions.dipjes_im, Actions.ro_sc_im, Actions.tw_w_im, Actions.dum_fly_in_im, Actions.smi_che_in_im, Actions.peck_fly_im, Actions.ch_pr_im, Actions.push_up_im, Actions.bal_cr_im, Actions.leg_up_im, Actions.crosstrainer_im, Actions.leg_curl_im, Actions.leg_ex_im, Actions.leg_press_im, Actions.leg_press_in_im, Actions.sm_sq_im, Actions.box_spring_im, Actions.dum_curl_im, Actions.bar_curl_im, Actions.ham_curl_im, Actions.lig_cr_im, Actions.fietsen_im};
//    private List<String> actions = new ArrayList<String>(Arrays.asList(action_list));

    /* here we must override the constructor for ArrayAdapter
    * the only variable we care about now is ArrayList<Item> objects,
    * because it is the list of objects we want to display.
    */
    public  ActionAdapter(Context context, int textViewResourceId, ArrayList<DetailInfo> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }

    /*
         * we are overriding the getView method here - this is what defines how each
         * list item will look.
         */
    public View getView(int position, View convertView, ViewGroup parent){

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.alist, null);
        }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        DetailInfo i = objects.get(position);

        if (i != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

            TextView title = (TextView) v.findViewById(R.id.title);
            TextView info = (TextView) v.findViewById(R.id.info);
            ImageView image = (ImageView) v.findViewById(R.id.img);
            // check to see if each individual textview is null.
            // if not, assign some text!
            if (title != null){
                title.setTextColor(getColor());
                title.setText(i.getName());
//                if (actions.indexOf(i.getName()) != -1){
//                    i.setImg(action_im_list[actions.indexOf(i.getName())]);
//                }else {
//                    i.setImg(R.drawable.background);
//                }
            }
            if (info != null){
                info.setTextColor(getColor());
                info.setText(i.getInfo());
            }

//            if (image != null && i.getImg() != 0){
            if (image != null){
//                if (actions.indexOf(i.getName()) != -1){
//                    image.setImageResource(action_im_list[actions.indexOf(i.getName())]);
//                }else {
//                    image.setImageResource(R.drawable.background);
//                }
                image.setImageResource(i.getImg());
            }
        }

        // the view must be returned to our activity
        return v;

    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }


}
