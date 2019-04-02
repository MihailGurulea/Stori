package io.ideaction.stori.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.ideaction.stori.R;
import io.ideaction.stori.db.TranslatedWord;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.ViewHolder> {

    private ArrayList<TranslatedWord> mVocabularies;
    private VocabularyAdapterListener mVocabularyAdapterListener;

    public VocabularyAdapter(ArrayList<TranslatedWord> vocabularies, VocabularyAdapterListener vocabularyAdapterListener) {
        mVocabularies = vocabularies;
        mVocabularyAdapterListener = vocabularyAdapterListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VocabularyAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_view_item_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        TranslatedWord translatedWord = mVocabularies.get(i);

        viewHolder.mTVWord.setText(translatedWord.getTranslations().getWord().getWordInLanguagesToLearn() +
                " - " + translatedWord.getTranslations().getWord().getWordInNativeLanguages() +
                " (" + translatedWord.getTranslations().getType().getTypeInNativeLanguages() + ")");
        viewHolder.mTVPronuntation.setText("/" + translatedWord.getTranslations().getPronunciation().getPronunciationInLanguagesToLearn() + "/");
        viewHolder.mTVDescription.setText(translatedWord.getTranslations().getDefinition().getDefinitionInNativeLanguages());
    }

    @Override
    public int getItemCount() {
        return mVocabularies.size();
    }

    public void remove(int position) {
        mVocabularies.remove(position);
        notifyItemRemoved(position);
    }

    public void update(ArrayList<TranslatedWord> translatedWords) {
        mVocabularies.clear();
        mVocabularies = null;
        mVocabularies = translatedWords;
        notifyDataSetChanged();
    }

    public int getId(int position) {
        return mVocabularies.get(position).getId();
    }

    public void removeAll() {
        mVocabularies.clear();
        notifyDataSetChanged();
    }

    public interface VocabularyAdapterListener {
        void onClickSound(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_view_item_title)
        TextView mTVWord;
        @BindView(R.id.list_view_item_subtitle)
        TextView mTVPronuntation;
        @BindView(R.id.tv_description)
        TextView mTVDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.iv_stream_word)
        void onClickSound() {
            mVocabularyAdapterListener.onClickSound(getAdapterPosition());
        }
    }
}