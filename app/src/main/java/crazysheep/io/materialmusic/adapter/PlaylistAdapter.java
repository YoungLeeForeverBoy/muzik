package crazysheep.io.materialmusic.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.rebound.SpringSystem;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import crazysheep.io.materialmusic.R;
import crazysheep.io.materialmusic.bean.IPlaylist;
import crazysheep.io.materialmusic.fragment.localmusic.PlaylistFragment;

/**
 * adapter for playlist at {@link PlaylistFragment}
 *
 * Created by crazysheep on 15/12/17.
 */
public class PlaylistAdapter extends RecyclerViewBaseAdapter<PlaylistAdapter.PlaylistHolder,
        IPlaylist>{

    private SpringSystem mSpringSystem = SpringSystem.create();

    public PlaylistAdapter(Context context, List<IPlaylist> datas) {
        super(context, datas);
    }

    @Override
    protected PlaylistHolder onCreateHolder(ViewGroup parent, int viewType) {
        final PlaylistHolder holder = new PlaylistHolder(
                mInflater.inflate(R.layout.item_album, parent, false), mSpringSystem);
        holder.coverIv.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        holder.coverIv.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        ViewGroup.LayoutParams params = holder.coverIv.getLayoutParams();
                        params.height = holder.coverIv.getMeasuredWidth();
                        holder.coverIv.setLayoutParams(params);
                    }
                });

        return holder;
    }

    @Override
    public void onBindViewHolder(PlaylistHolder holder, int position) {
        IPlaylist item = getItem(position);

        // cancel request before
        Picasso.with(mContext)
                .cancelRequest(holder.coverIv);

        if(!TextUtils.isEmpty(item.getAvatar()))
            Picasso.with(mContext)
                    .load(new File(item.getAvatar()))
                    .fit()
                    .centerCrop()
                    .into(holder.coverIv);
        else
            holder.coverIv.setImageResource(R.drawable.place_holder);
        holder.nameTv.setText(item.getPlaylistName());
    }

    //////////////////////// view holder ///////////////////////////////

    public static class PlaylistHolder extends RecyclerViewBaseAdapter.ReboundViewHolder {

        @Bind(R.id.playlist_cover_iv) ImageView coverIv;
        @Bind(R.id.playlist_name_tv) TextView nameTv;

        public PlaylistHolder(View parent, @NonNull SpringSystem ss) {
            super(parent, ss);
            ButterKnife.bind(this, parent);
        }
    }

}
