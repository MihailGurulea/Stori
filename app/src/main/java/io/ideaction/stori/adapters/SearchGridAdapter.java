package io.ideaction.stori.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.db.Stori;
import io.ideaction.stori.utils.Utils;
import io.realm.RealmResults;

public class SearchGridAdapter extends BaseAdapter {

    private RealmResults<Stori> mStoris;
    private final LayoutInflater inflater;
    private SearchGridAdapterListener mSearchGridAdapterListener;

    public SearchGridAdapter(Context context,SearchGridAdapterListener searchGridAdapterListener) {
        inflater = LayoutInflater.from(context);
        mSearchGridAdapterListener = searchGridAdapterListener;
    }

    @Override
    public int getCount() {
        return mStoris != null && mStoris.size() > 0 ? mStoris.size() : 0;
    }

    @Override
    public Stori getItem(int position) {
        return mStoris != null && mStoris.size() > position ? mStoris.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return mStoris != null && mStoris.size() > position ? mStoris.get(position).getId() : 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.item_stori, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        if (mStoris != null && mStoris.size() > 0) {
            Stori stori = mStoris.get(position);

            if (stori != null) {
                if (stori.getImage() != null && stori.getImage().length() > 0) {
                    Picasso.get().load(stori.getImage()).resize(1280, 720).into(holder.mIVBackground);
                } else {
                    holder.mIVBackground.setImageResource(R.drawable.suggested_stori_image);
                }
                holder.mTVTitle.setTypeface(StoriApplication.getInstance().getCeraProMedium());
                holder.mTVTitle.setText(stori.getTranslations().getName().getNameInNativeLanguages());
                holder.mTVTitleDescription.setTypeface(StoriApplication.getInstance().getAvenirBook());
                holder.mTVTitleDescription.setText(stori.getCategory().getTranslations().getName().getNameInNativeLanguages());
                if (stori.getIsPremium()) {
                    holder.mIVPremium.setVisibility(View.VISIBLE);
                } else {
                    holder.mIVPremium.setVisibility(View.GONE);
                }
                if (stori.getRead()) {
                    holder.mIVRead.setVisibility(View.VISIBLE);
                } else {
                    holder.mIVRead.setVisibility(View.GONE);
                }
                holder.mRLMain.setOnClickListener(v -> mSearchGridAdapterListener.onClickStoriItem(position));
            }
        }

        return view;
    }

    public interface SearchGridAdapterListener {
        void onClickStoriItem(int position);
    }

    public void updateStoris(RealmResults<Stori> storis) {
        mStoris = storis;
        notifyDataSetChanged();
    }

    static final class ViewHolder {
        @BindView(R.id.iv_main_image)
        ImageView mIVBackground;
        @BindView(R.id.iv_premium)
        ImageView mIVPremium;
        @BindView(R.id.tv_title)
        TextView mTVTitle;
        @BindView(R.id.tv_title_description)
        TextView mTVTitleDescription;
        @BindView(R.id.rl_main)
        RelativeLayout mRLMain;
        @BindView(R.id.tv_read_filter)
        TextView mIVRead;

        public ViewHolder(@NonNull View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
