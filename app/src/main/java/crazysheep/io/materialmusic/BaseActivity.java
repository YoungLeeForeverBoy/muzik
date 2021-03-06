package crazysheep.io.materialmusic;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;

import crazysheep.io.materialmusic.net.NetClient;
import crazysheep.io.materialmusic.utils.Utils;
import retrofit2.Retrofit;

/**
 * base activity
 *
 * Created by crazysheep on 15/11/25.
 */
public class BaseActivity extends AppCompatActivity {

    protected static String TAG = BaseActivity.class.getSimpleName();

    protected Retrofit mRetrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getCurrentTheme());

        TAG = this.getClass().getSimpleName();

        mRetrofit = NetClient.retrofit();
    }

    protected final Activity getActivity() {
        return this;
    }

    /**
     * return theme for current activity
     * */
    @StyleRes
    protected int getCurrentTheme() {
        // day theme by default
        return R.style.AppTheme_Day;
    }

    public void hideSupportActionBar() {
        if(!Utils.checkNull(getSupportActionBar()))
            getSupportActionBar().hide();
    }

    public void showSupportActionbar() {
        if(!Utils.checkNull(getSupportActionBar()))
            getSupportActionBar().show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_right_out);
    }
}
