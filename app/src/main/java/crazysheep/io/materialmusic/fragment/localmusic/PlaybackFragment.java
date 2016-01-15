package crazysheep.io.materialmusic.fragment.localmusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mingle.sweetpick.BlurEffect;
import com.mingle.sweetpick.CustomDelegate;
import com.mingle.sweetpick.SweetSheet;
import com.squareup.picasso.Picasso;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import crazysheep.io.materialmusic.R;
import crazysheep.io.materialmusic.adapter.SimpleSongsAdapter;
import crazysheep.io.materialmusic.bean.ISong;
import crazysheep.io.materialmusic.fragment.BaseFragment;
import crazysheep.io.materialmusic.media.MusicPlayer;
import crazysheep.io.materialmusic.service.BaseMusicService;
import crazysheep.io.materialmusic.service.MusicService;
import crazysheep.io.materialmusic.utils.Utils;
import crazysheep.io.materialmusic.widget.PlayModeImageButton;
import crazysheep.io.materialmusic.widget.PlayOrPauseImageButton;
import de.greenrobot.event.EventBus;

/**
 * playback fragment for local music
 *
 * Created by crazysheep on 16/1/2.
 */
public class PlaybackFragment extends BaseFragment {

    @Bind(R.id.playback_root) FrameLayout mRootFl;
    @Bind(R.id.song_cover_iv) ImageView mBigCoverIv;
    @Bind(R.id.song_play_or_pause_ib) PlayOrPauseImageButton mPlayOrPauseBtn;
    @Bind(R.id.song_little_cover_iv) ImageView mTopSongCoverIv;
    @Bind(R.id.song_name_tv) TextView mTopSongNameTv;
    @Bind(R.id.song_artist_tv) TextView mTopSongArtistTv;
    @Bind(R.id.top_song_info_ll) View mTopSongLl;
    @Bind(R.id.music_sb) DiscreteSeekBar mMusicSb;
    @Bind(R.id.song_play_mode_ib) PlayModeImageButton mPlayModeIb;
    @Bind(R.id.song_play_list_ib) ImageButton mPlaylistIb;

    private RecyclerView mPlaylistRv;
    private SimpleSongsAdapter mAdapter;
    private SweetSheet mSweetSheet;

    private ISong mCurSong;

    private boolean isServiceBind = false;
    private MusicService mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isServiceBind = true;
            mService = ((MusicService.MusicBinder)service).getService();

            updatePlayModeButton();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBind = false;
            mService = null;
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_local_music_playback,
                container, false);
        ButterKnife.bind(this, contentView);

        initUI();

        return contentView;
    }

    private void initUI() {
        mTopSongLl.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mTopSongLl.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTopSongLl
                                .getLayoutParams();
                        //params.topMargin = SystemUIHelper.getStatusBarSize(getActivity());
                    }
                });
        mPlayOrPauseBtn.setResources(R.drawable.ic_play_circle_fill_black,
                R.drawable.ic_pause_circle_fill_black);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.song_previous_ib)
    public void clickPreviousButton() {
        if(isServiceBind)
            mService.previous();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.song_next_ib)
    public void clickNextButton() {
        if(isServiceBind)
            mService.next();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.song_play_or_pause_ib)
    public void clickPlayOrPauseButton() {
        if(isServiceBind)
            if(mService.isPlaying())
                mService.pause();
            else
                mService.playOrResume();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.song_play_list_ib)
    public void clickPlaylistButton() {
        if(mSweetSheet == null) {
            View playlistContent = LayoutInflater.from(getActivity()).inflate(
                    R.layout.layout_recyclerview, mRootFl, false);
            mPlaylistRv = ButterKnife.findById(playlistContent, R.id.data_rv);
            mPlaylistRv.setLayoutManager(new LinearLayoutManager(getActivity()));
            mAdapter = new SimpleSongsAdapter(getActivity(), mService.getAllSongs());
            mPlaylistRv.setAdapter(mAdapter);

            mSweetSheet = new SweetSheet(mRootFl);
            mSweetSheet.setBackgroundEffect(new BlurEffect(25));
            CustomDelegate customDelegate = new CustomDelegate(true,
                    CustomDelegate.AnimationType.DuangLayoutAnimation);
            customDelegate.setContentHeight(getResources().getDisplayMetrics().heightPixels / 2);
            customDelegate.setCustomView(playlistContent);
            mSweetSheet.setDelegate(customDelegate);
        }
        mAdapter.highlight(mService.getCurrentSong().getUrl());
        mSweetSheet.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
        getActivity().bindService(new Intent(getActivity(), MusicService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(isServiceBind)
            mPlayOrPauseBtn.toggle(mService.isPlaying());
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
        if(isServiceBind)
            getActivity().unbindService(mConnection);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.song_play_mode_ib)
    public void clickPlayMode() {
        /*   __________________
        *   ↓                  ↑
        * loop -> loop one -> shuffle
        * **/
        if(isServiceBind) {
            if(mService.isLoopAll())
                mService.loopOne();
            else if(mService.isLoopOne())
                mService.shuffle();
            else if(mService.isShuffle())
                mService.loopAll();

            updatePlayModeButton();
        }
    }

    private void updateUI() {
        if(!TextUtils.isEmpty(mCurSong.getCover())) {
            Uri uri = mCurSong.isLocal()
                    ? Uri.fromFile(new File(mCurSong.getCover())) : Uri.parse(mCurSong.getCover());
            Picasso.with(getActivity())
                    .load(uri)
                    .into(mBigCoverIv);

            Picasso.with(getActivity())
                    .load(uri)
                    .fit()
                    .error(R.drawable.place_holder)
                    .into(mTopSongCoverIv);
        } else {
            mTopSongCoverIv.setImageResource(R.drawable.place_holder);
            mBigCoverIv.setImageResource(0);
        }

        mTopSongArtistTv.setText(mCurSong.getArtist());
        mTopSongNameTv.setText(mCurSong.getName());
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(@NonNull BaseMusicService.EventSongProgress event) {
        compareEventSong(event.song);

        mMusicSb.setMax(event.maxProgress);
        mMusicSb.setProgress(event.progress);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(@NonNull MusicPlayer.EventMusicPause event) {
        mPlayOrPauseBtn.toggle(false);
        compareEventSong(event.song);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(@NonNull MusicPlayer.EventMusicResume event) {
        mPlayOrPauseBtn.toggle(true);
        compareEventSong(event.song);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(@NonNull MusicPlayer.EventMusicStop event) {
        mPlayOrPauseBtn.toggle(false);
        compareEventSong(event.song);
    }

    private void compareEventSong(@NonNull ISong song) {
        if(Utils.checkNull(mCurSong) || !song.getUrl().equals(mCurSong.getUrl())) {
            mCurSong = song;
            updateUI();
        }
    }

    private void updatePlayModeButton() {
        if(mService.isLoopAll())
            mPlayModeIb.loop();
        else if(mService.isLoopOne())
            mPlayModeIb.loopOne();
        else if(mService.isShuffle())
            mPlayModeIb.shuffle();
    }

}
