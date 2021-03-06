package crazysheep.io.materialmusic.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import crazysheep.io.materialmusic.R;
import crazysheep.io.materialmusic.fragment.localmusic.AlbumsFragment;
import crazysheep.io.materialmusic.fragment.localmusic.ArtistsFragment;
import crazysheep.io.materialmusic.fragment.localmusic.PlaylistFragment;
import crazysheep.io.materialmusic.fragment.localmusic.SongsFragment;

/**
 * view pager adapter for LocalMusicAdapter
 *
 * Created by crazysheep on 15/12/28.
 */
public class MusicPagerAdapter extends FragmentPagerAdapter {

    private static String[] mTitles;
    @SuppressWarnings("unchecked")
    private static Class<? extends Fragment>[] mFts = new Class[] {
            PlaylistFragment.class, ArtistsFragment.class,
            AlbumsFragment.class, SongsFragment.class
    };

    private Context mContext;

    public MusicPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;

        mTitles = new String[] {
                mContext.getString(R.string.tv_my_playlist),
                mContext.getString(R.string.tv_artists),
                mContext.getString(R.string.tv_albums),
                mContext.getString(R.string.tv_songs)
        };
    }

    @Override
    public Fragment getItem(int position) {
        return Fragment.instantiate(mContext, mFts[position].getName());
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
