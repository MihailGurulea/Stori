package io.ideaction.stori.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.ideaction.stori.R;
import io.ideaction.stori.fragments.probation.ExamFragment;

public class ExamAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private ArrayList<String> mExamenContent;
    private int mSelectedPosition;
    private int mCorrectAnswerPosition;
    private ExamAdapterListener mExamAdapterListener;

    public ExamAdapter(Context context, ArrayList<String> examenContent, ExamAdapterListener examAdapterListener) {
        inflater = LayoutInflater.from(context);
        mExamenContent = examenContent;
        mExamAdapterListener = examAdapterListener;
        mSelectedPosition = -1;
        mCorrectAnswerPosition = -1;
    }

    @Override
    public int getCount() {
        return mExamenContent.size();
    }

    @Override
    public String getItem(int position) {
        return mExamenContent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.cell_exam_option, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        holder.word.setText(mExamenContent.get(position));

        if (position == mSelectedPosition) {
            holder.rlMain.setBackgroundResource(R.drawable.background_exam_selected);
            holder.ivCheck.setImageResource(R.drawable.selected_oval);
        } else {
            holder.rlMain.setBackgroundResource(R.drawable.background_exam_options);
            holder.ivCheck.setImageResource(R.drawable.oval);
        }

        if (mCorrectAnswerPosition == position) {
            holder.rlMain.setBackgroundResource(R.drawable.background_exam_correct_option);
            holder.ivCheck.setImageResource(R.drawable.correct_oval);
            mCorrectAnswerPosition = -1;
        }

        holder.rlMain.setOnClickListener(v -> mExamAdapterListener.onClickItem(position));

        return view;
    }

    public void changeUiByClick(int position) {
        mSelectedPosition = (mSelectedPosition == position) ? -1 : position;
        mExamAdapterListener.changeButtonUi((mSelectedPosition == -1) ? ExamFragment.NOT_SELECTED : ExamFragment.SELECTED);
        notifyDataSetChanged();
    }

    public void next(ArrayList<String> examenContent) {
        mExamenContent = examenContent;
        mSelectedPosition = -1;
        notifyDataSetChanged();
    }
    public void setCorrectAnswer(int position) {
        mCorrectAnswerPosition = position;
        notifyDataSetChanged();
    }

    public interface ExamAdapterListener {
        void onClickItem(int position);
        void changeButtonUi(int status);
    }

    static final class ViewHolder {
        @BindView(R.id.iv_simple_oval)
        ImageView ivCheck;
        @BindView(R.id.tv_option_word)
        TextView word;
        @BindView(R.id.cell_exam_option)
        RelativeLayout rlMain;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}