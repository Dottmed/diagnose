package com.dingbei.diagnose.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dingbei.diagnose.R;
import com.dingbei.diagnose.bean.HospitalBean;
import com.dingbei.diagnose.view.recyc.CommonAdapter;
import com.dingbei.diagnose.view.recyc.ViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Dayo
 * @desc 选择医院
 */

public class HospitalPop extends BasePop implements View.OnClickListener {

    private OnDialogListener mListener;
    private View mView;
    private ArrayList<HospitalBean> mList;
    private CommonAdapter<HospitalBean> mAdapter;

    public HospitalPop(Context context, OnDialogListener listener) {
        super(context);
        mListener = listener;
    }

    @Override
    public View getContentView() {
        mView = View.inflate(mContext, R.layout.dingbei_pop_hospital, null);
        return mView;
    }

    @Override
    public void initView() {
        mView.findViewById(R.id.ly_outside).setOnClickListener(this);
        mView.findViewById(R.id.img_close).setOnClickListener(this);

        mList = new ArrayList<>();
        mAdapter = new CommonAdapter<HospitalBean>(mContext, R.layout.dingbei_item_pop_hospital, mList) {
            @Override
            protected void convert(ViewHolder holder, HospitalBean bean, int position) {
                holder.setText(R.id.tx_name, bean.getName());

                holder.setOnClickListener(R.id.ly_item, v -> {
                    if(mListener != null) {
                        mListener.onSelected(bean.getName(), bean.getId());
                        dismiss();
                    }
                });
            }
        };
        RecyclerView recycler = mView.findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(false);
        recycler.setAdapter(mAdapter);
    }

    public void setData(List<HospitalBean> list) {
        if(list != null && list.size() > 0) {
            mList.clear();
            mList.addAll(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.ly_outside || id == R.id.img_close) {
            dismiss();
        }
    }


    public interface OnDialogListener {
        void onSelected(String name, String id);
    }

}
