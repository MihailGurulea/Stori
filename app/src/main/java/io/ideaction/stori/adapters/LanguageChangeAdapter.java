package io.ideaction.stori.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import io.ideaction.stori.R;

public class LanguageChangeAdapter extends BaseAdapter {

    private Context mContext;

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

    public LanguageChangeAdapter(Context context) {
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

        ImageView ivFlag = convertView.findViewById(R.id.iv_flag);
        TextView tvTitle = convertView.findViewById(R.id.tv_language);

        ivFlag.setImageResource(mImages[position]);
        tvTitle.setText(mTitles[position]);


        return convertView;
    }
}

