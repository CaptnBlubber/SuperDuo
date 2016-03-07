package barqsoft.footballscores.service;

import android.content.Context;

import java.io.IOException;

import barqsoft.footballscores.R;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Angelo Rüggeberg <s3xy4ngc@googlemail.com>
 */

public class GeneralRequestInterceptor implements Interceptor {

    private static final String AUTH_TOKEN = "X-Auth-Token";
    private static final String RESPONE_CONTROL = "X-Response-Control";

    private String mApiKey;

    public GeneralRequestInterceptor(Context context) {
        mApiKey = context.getResources().getString(R.string.api_key);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request newRequest = originalRequest
                .newBuilder()
                .addHeader(AUTH_TOKEN, mApiKey)
                .addHeader(RESPONE_CONTROL, "minified")
                .build();

        return chain.proceed(newRequest);
    }
}
