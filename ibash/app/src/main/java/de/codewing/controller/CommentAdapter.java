package de.codewing.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.codewing.ibash.R;
import de.codewing.model.Comment;
import de.codewing.view.CommentActivity;

/**
 * Created by codewing on 12.12.2015.
 */
public class CommentAdapter extends BaseAdapter {
    private CommentActivity commentActivity;
    private final LayoutInflater mInflater;

	public CommentAdapter(CommentActivity commentActivity) {
        this.commentActivity = commentActivity;
        mInflater = (LayoutInflater) commentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

	public int getCount() {
        return commentActivity.commentlist.size();
    }

	public Comment getItem(int position) {
        return commentActivity.commentlist.get(position);
    }

	public long getItemId(int position) {
        return (long) position;
    }

	public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout itemView = (LinearLayout) mInflater.inflate(R.layout.listitem_comment, parent, false);
        bindView(itemView, position);
        return itemView;
    }

	private void bindView(LinearLayout view, int position) {
        Comment comment = getItem(position);
        view.setId((int) getItemId(position));
        TextView tv_nick = (TextView) view.findViewById(R.id.Comment_Nickname);
        TextView tv_ts = (TextView) view.findViewById(R.id.Comment_Datum);
        TextView tv_text = (TextView) view.findViewById(R.id.Comment_text);
        tv_nick.setText(comment.getNick());
        tv_ts.setText(comment.getTs());
        tv_text.setText(comment.getText());

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(commentActivity);
		boolean greyText = sharedPref.getBoolean("pref_key_use_grey_text", true);
		if(!greyText){
			tv_text.setTextColor(Color.BLACK);
		}
    }
}
