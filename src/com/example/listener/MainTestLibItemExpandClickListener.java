package com.example.listener;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.coolgrammer.R;

public class MainTestLibItemExpandClickListener implements OnClickListener{
	
	private RelativeLayout relativeLayout;
	private Context context;
	
	public MainTestLibItemExpandClickListener(RelativeLayout relativeLayout, Context context) {
		this.relativeLayout = relativeLayout;
		this.context = context;
	}
	
	@Override
	public void onClick(View arg0){
		
		if(relativeLayout.getVisibility() == View.GONE){
			relativeLayout.setVisibility(View.VISIBLE);
			((ImageButton)arg0).setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_collapse));
		}
		else if(relativeLayout.getVisibility() == View.VISIBLE){
			relativeLayout.setVisibility(View.GONE);
			((ImageButton)arg0).setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_expand));
		}
		else {
			System.out.println("Something wrong in the lib_Imgbtn_Explistener!");
		}
	}
}
