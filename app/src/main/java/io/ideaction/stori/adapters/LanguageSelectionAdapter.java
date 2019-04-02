package io.ideaction.stori.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.ideaction.stori.R;

public class LanguageSelectionAdapter extends BaseAdapter {

    private Context mContext;
    private int mPositionFirst = -1;
    private int mPositionSecond = -1;

    private int[] mImages = {
            R.drawable.ic_english_flag,
            R.drawable.ic_spanish_flag,
            R.drawable.ic_french_flag,
            R.drawable.ic_german_flag,
            R.drawable.ic_italian_flag,
            R.drawable.ic_russian_flag
    };

    private int[] mTitles = {
            R.string.english,
            R.string.spanish,
            R.string.french,
            R.string.german,
            R.string.italian,
            R.string.russian
    };

    public LanguageSelectionAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.language_cell, parent, false);
        }

        RelativeLayout rlMain = convertView.findViewById(R.id.rl_main);
        ImageView ivFlag = convertView.findViewById(R.id.iv_flag);
        ImageView ivCheck = convertView.findViewById(R.id.iv_check);
        TextView tvTitle = convertView.findViewById(R.id.tv_language);

        ivFlag.setImageResource(mImages[position]);
        tvTitle.setText(mTitles[position]);

        if (mPositionFirst == position) {
            ivCheck.setVisibility(View.VISIBLE);
            rlMain.setAlpha(0.2F);
            tvTitle.setBackgroundResource(R.drawable.tv_bottom_border);
        } else {
            ivCheck.setVisibility(View.GONE);
            tvTitle.setBackground(null);
            rlMain.setAlpha(1F);
        }

        if (mPositionFirst != position) {
            if (mPositionSecond == position) {
                ivCheck.setVisibility(View.VISIBLE);
                tvTitle.setBackgroundResource(R.drawable.tv_bottom_border);
            } else {
                ivCheck.setVisibility(View.GONE);
                tvTitle.setBackground(null);
            }
        }

        return convertView;
    }

    public void onClickItemFirst(int position) {
        mPositionFirst = position;
        notifyDataSetChanged();
    }

    public void onClickItemSecond(int position) {
        mPositionSecond = position;
        notifyDataSetChanged();
    }

    public int getPositionFirst() {
        return mPositionFirst;
    }

    public int getPositionSecond() {
        return mPositionSecond;
    }
}
