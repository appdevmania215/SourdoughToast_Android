package com.gmc.sourdoughtoast.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gmc.sourdoughtoast.R;
import com.gmc.sourdoughtoast.sqlite.Vote;

public class CommentListArrayAdapter extends ArrayAdapter<ArrayList<String>>{
	protected ViewHolder viewHolder = null;
	protected LayoutInflater inflater = null;
	
	int type = 0;
	 
	protected int NAME = 0;
	protected int DATE = 1;
	protected int COMMENT = 2;

	public CommentListArrayAdapter(Context c, int textViewResourceId, 
			ArrayList<ArrayList<String>> arrays, int type) {
		super(c, textViewResourceId, arrays);
		this.inflater = LayoutInflater.from(c);
		this.type = type;
	}
	
	@Override
	public int getCount() {
		return super.getCount();
	}

	public String getItem(int position, int type) {
		return super.getItem(position).get(type);
	}

	@Override
	public long getItemId(int position) {
		return super.getItemId(position);
	}

	@Override
	public View getView(final int position, View convertview, ViewGroup parent) {
		
		View v = convertview;
		
		if ( type == Vote.FIRST ) {
			v = inflater.inflate(R.layout.list_row_first_comment, null);
		}
		else {
			v = inflater.inflate(R.layout.list_row_second_comment, null);
		}
		
		viewHolder = new ViewHolder();
		viewHolder.tvName = (TextView)v.findViewById(R.id.tvName);
		viewHolder.tvDate = (TextView)v.findViewById(R.id.tvDate);
		viewHolder.tvComment = (TextView)v.findViewById(R.id.tvComment);
		
		viewHolder.tvName.setText(getItem(position, NAME));
		viewHolder.tvDate.setText(getItem(position, DATE));
		viewHolder.tvComment.setText(getItem(position, COMMENT));
		
		v.setTag(viewHolder);
		
		return v;
	}
	
	/*
	 * ViewHolder
	 */
	class ViewHolder{
		public TextView tvName  = null;
		public TextView tvDate = null;
		public TextView tvComment = null;
	}

	@Override
	protected void finalize() throws Throwable {
		free();
		super.finalize();
	}
	
	protected void free(){
		inflater = null;
		viewHolder = null;
	}
}
