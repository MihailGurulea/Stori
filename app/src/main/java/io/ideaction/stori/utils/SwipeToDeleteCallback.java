package io.ideaction.stori.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import io.ideaction.stori.R;
import io.ideaction.stori.adapters.VocabularyAdapter;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private VocabularyAdapter mAdapter;
    private SwipeToDeleteCallbackListener mSwipeToDeleteCallbackListener;
    private Drawable mIcon;

    public SwipeToDeleteCallback(Context context, VocabularyAdapter adapter, SwipeToDeleteCallbackListener swipeToDeleteCallbackListener) {
        super(0, ItemTouchHelper.LEFT);
        mAdapter = adapter;
        mSwipeToDeleteCallbackListener = swipeToDeleteCallbackListener;
        mIcon = context.getDrawable(R.drawable.ic_delete_sweep);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int position = viewHolder.getAdapterPosition();
        int id = mAdapter.getId(position);
        mAdapter.remove(position);
        mSwipeToDeleteCallbackListener.onWordRemove(id);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;

        int iconMargin = (itemView.getHeight() - mIcon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - mIcon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + mIcon.getIntrinsicHeight();

        if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - mIcon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            mIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        }

        mIcon.draw(c);
    }

    public interface SwipeToDeleteCallbackListener {
        void onWordRemove(int id);
    }
}
