package com.dingbei.diagnose.view.recyc;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Dayo
 * @time 2017/8/8 10:56
 */

public class ListItemDecoration extends RecyclerView.ItemDecoration {

    private boolean mHasHead;

    public ListItemDecoration(boolean hasHead) {
        mHasHead = hasHead;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);

        if(mHasHead) {
            if(position == 0) {
                outRect.set(0, 0, 0, 0);
            }else if(position == 1) {
                outRect.set(0, 42, 0, 28);
            }else {
                outRect.set(0, 0, 0, 28);
            }

        }else {
            if(position == 0) {
                outRect.set(0, 42, 0, 28);
            }else {
                outRect.set(0, 0, 0, 28);
            }
        }
    }
}
