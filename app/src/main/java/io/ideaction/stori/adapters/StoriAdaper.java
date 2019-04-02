package io.ideaction.stori.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.db.Stori;
import io.ideaction.stori.utils.Utils;

public class StoriAdaper extends RecyclerView.Adapter<StoriAdaper.ViewHolder> {

    private ArrayList<Stori> mStoris;
    private StoriAdapterListener mStoriAdapterListener;

    public StoriAdaper(ArrayList<Stori> storis, StoriAdapterListener storiAdapterListener) {
        mStoris = storis;
        mStoriAdapterListener = storiAdapterListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_stori, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (mStoris != null && mStoris.size() > 0) {
            Stori stori = mStoris.get(i);

            if (stori != null) {
                if (stori.getImage() != null && stori.getImage().length() > 0) {
                    Picasso.get().load(stori.getImage()).resize(1280, 720).into(viewHolder.mIVBackground);
                } else {
                    viewHolder.mIVBackground.setImageResource(R.drawable.suggested_stori_image);
                }
                viewHolder.mTVTitle.setTypeface(StoriApplication.getInstance().getCeraProMedium());
                viewHolder.mTVTitle.setText(stori.getTranslations().getName().getNameInNativeLanguages());
                viewHolder.mTVTitleDescription.setTypeface(StoriApplication.getInstance().getAvenirBook());
                viewHolder.mTVTitleDescription.setText(stori.getCategory().getTranslations().getName().getNameInNativeLanguages());
                if (stori.getIsPremium()) {
                    viewHolder.mIVPremium.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mIVPremium.setVisibility(View.GONE);
                }
                if (stori.getRead()) {
                    viewHolder.mIVRead.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mIVRead.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mStoris.size();
    }

    public void updateStori(ArrayList<Stori> storis) {
        mStoris = storis;
        notifyDataSetChanged();
    }

    public interface StoriAdapterListener {
        void onClickStori(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_main_image)
        ImageView mIVBackground;
        @BindView(R.id.iv_premium)
        ImageView mIVPremium;
        @BindView(R.id.tv_title)
        TextView mTVTitle;
        @BindView(R.id.tv_title_description)
        TextView mTVTitleDescription;
        @BindView(R.id.tv_read_filter)
        TextView mIVRead;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.rl_main)
        void onClickStori() {
            mStoriAdapterListener.onClickStori(getAdapterPosition());
        }
    }
}
