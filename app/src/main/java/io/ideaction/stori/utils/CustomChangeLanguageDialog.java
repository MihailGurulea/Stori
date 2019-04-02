package io.ideaction.stori.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.Locale;

import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.adapters.LanguageChangeAdapter;

public class CustomChangeLanguageDialog extends Dialog {

    private Context mContext;
    private ImageView mIVCancel;
    private LinearLayout mRLDialog;
    private ListView mLVLanguageList;
    private LanguageChangeAdapter mLanguageChangeAdapter;
    private DialogLanguageListener mDialogLanguageListener;


    public CustomChangeLanguageDialog(Context context, DialogLanguageListener dialogLanguageListener) {
        super(context);
        mContext = context;
        mDialogLanguageListener = dialogLanguageListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Locale locale = new Locale(Languages.values()[StoriApplication.getInstance().getNativeLang()].getCodeLowerCase());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        mContext.getResources().updateConfiguration(config, mContext.getResources().getDisplayMetrics());

        setContentView(R.layout.dialog_choose_language);

        mIVCancel = findViewById(R.id.iv_cancel_language_layout);
        mRLDialog = findViewById(R.id.rl_dialog);
        mLVLanguageList = findViewById(R.id.language_list);

        initViews();
        initListeners();
    }

    private void initViews() {
        mLanguageChangeAdapter = new LanguageChangeAdapter(mContext);
        mLVLanguageList.setAdapter(mLanguageChangeAdapter);
    }

    private void initListeners() {
        mLVLanguageList.setOnItemClickListener((parent, view, position, id) -> mDialogLanguageListener.onClickItem(position));

        mIVCancel.setOnClickListener(v -> mDialogLanguageListener.onClickClose());
    }

    public interface DialogLanguageListener {
        void onClickClose();
        void onClickItem(int position);
    }
}
