package crazysheep.io.materialmusic.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * base adapter for RecyclerView
 *
 * Created by crazysheep on 15/12/17.
 */
public abstract class RecyclerViewBaseAdapter<VH extends RecyclerView.ViewHolder, DT>
        extends RecyclerView.Adapter<VH> {

    //////////////////////// api /////////////////////////

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }

    //////////////////////////////////////////////////////

    protected Context mContext;
    protected List<DT> mItems;
    protected LayoutInflater mInflater;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public RecyclerViewBaseAdapter(@NonNull Context context, List<DT> items) {
        mContext = context;
        mItems = items;
        if(mItems == null)
            mItems = new ArrayList<>();
        mInflater = LayoutInflater.from(mContext);
    }

    public void setData(List<DT> items) {
        mItems = items;
        if(mItems == null)
            mItems = new ArrayList<>();

        notifyDataSetChanged();
    }

    public List<DT> getData() {
        return mItems;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

    protected boolean isFooter(int position) {
        return getItemCount() - 1 == position;
    }

    protected final void removeItem(int position) {
        if(position >= 0 && position < getItemCount()) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    protected final void removeItem(@NonNull DT item) {
        int index = mItems.indexOf(item);
        if(index >= 0 && index < getItemCount()) {
            mItems.remove(index);
            notifyItemRemoved(index);
        }
    }

    protected final int findItemPosition(@NonNull DT item) {
        return mItems.indexOf(item);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        VH holder = onCreateHolder(parent, viewType);
        updateClickListener(holder);

        return holder;
    }

    protected abstract VH onCreateHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(VH holder, int position);

    @Override
    public void onViewRecycled(VH holder) {
        super.onViewRecycled(holder);
        updateClickListener(holder);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public DT getItem(int position) {
        return mItems.get(position);
    }

    private void updateClickListener(final VH holder) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null)
                    mOnItemLongClickListener.onItemLongClick(holder.itemView,
                            holder.getAdapterPosition());

                return true;
            }
        });
    }

    /////////////////////// base ViewHolder with facebook's "rebound" //////////////////////////

    public static class ReboundViewHolder extends RecyclerView.ViewHolder
            implements View.OnTouchListener, SpringListener {

        private SpringSystem mSpringSystem;
        private Spring mSpring;

        public ReboundViewHolder(@NonNull  View itemView, @NonNull SpringSystem ss) {
            super(itemView);
            mSpringSystem = ss;

            mSpring = mSpringSystem.createSpring();
            mSpring.addListener(this);
            mSpring.setOvershootClampingEnabled(true);

            itemView.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mSpring.setEndValue(1);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mSpring.setEndValue(0);
                    break;
            }
            return false;
        }

        @Override
        public void onSpringUpdate(Spring spring) {
            float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(
                    spring.getCurrentValue(), 0, 1, 1, 0.8);
            itemView.setScaleX(mappedValue);
            itemView.setScaleY(mappedValue);
        }

        @Override
        public void onSpringAtRest(Spring spring) {}

        @Override
        public void onSpringActivate(Spring spring) {}

        @Override
        public void onSpringEndStateChange(Spring spring) {}
    }

}
