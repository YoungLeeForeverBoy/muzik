package crazysheep.io.materialmusic.net;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * network layer, http request api
 *
 * Created by crazysheep on 15/12/17.
 */
public class NetClient {

    private static Retrofit mRetrofit;

    private NetClient() {
    }

    public static Retrofit retrofit() {
        if(mRetrofit == null)
            synchronized (NetClient.class) {
                OkHttpClient client = new OkHttpClient();
                client.interceptors().add(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .build();

                        return chain.proceed(request);
                    }
                });

                mRetrofit = new Retrofit.Builder()
                        .baseUrl(NetConstants.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();
            }

        return mRetrofit;
    }

}
