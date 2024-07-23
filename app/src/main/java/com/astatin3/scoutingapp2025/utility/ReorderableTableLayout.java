package com.astatin3.scoutingapp2025.utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.List;

public class ReorderableTableLayout extends TableLayout {
    private boolean reorderingEnabled = false;
    private int draggedRowIndex = -1;
    private float lastY;
    private List<View> originalRows;
    private int rowHeight;

    public ReorderableTableLayout(Context context) {
        super(context);
        init();
    }

    public ReorderableTableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        originalRows = new ArrayList<>();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!reorderingEnabled) {
            return super.onInterceptTouchEvent(ev);
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = ev.getY();
                draggedRowIndex = getRowIndexAtY(lastY);
                if (draggedRowIndex != -1) {
                    View draggedRow = getChildAt(draggedRowIndex);
                    rowHeight = draggedRow.getHeight();
                    saveOriginalOrder();
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!reorderingEnabled || draggedRowIndex == -1) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float currentY = event.getY();
                int targetIndex = getRowIndexAtY(currentY);
                if (targetIndex != -1 && targetIndex != draggedRowIndex) {
                    updateRowOrder(draggedRowIndex, targetIndex);
                    draggedRowIndex = targetIndex;
                }
                lastY = currentY;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                draggedRowIndex = -1;
                break;
        }
        return true;
    }

    private int getRowIndexAtY(float y) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (y >= child.getTop() && y <= child.getBottom()) {
                return i;
            }
        }
        return -1;
    }

    private void saveOriginalOrder() {
        originalRows.clear();
        for (int i = 0; i < getChildCount(); i++) {
            originalRows.add(getChildAt(i));
        }
    }

    private void updateRowOrder(int fromIndex, int toIndex) {
        if (fromIndex < toIndex) {
            for (int i = fromIndex; i < toIndex; i++) {
                Collections.swap(originalRows, i, i + 1);
            }
        } else {
            for (int i = fromIndex; i > toIndex; i--) {
                Collections.swap(originalRows, i, i - 1);
            }
        }

        removeAllViewsInLayout();
        for (View view : originalRows) {
            addViewInLayout(view, -1, view.getLayoutParams(), true);
        }
        requestLayout();
        invalidate();
    }

    public void setReorderingEnabled(boolean enabled) {
        reorderingEnabled = enabled;
    }

    public List<Integer> getReorderedIndexes() {
        List<Integer> reorderedIndexes = new ArrayList<>();
        for (View view : originalRows) {
            reorderedIndexes.add(indexOfChild(view));
        }
        return reorderedIndexes;
    }
}