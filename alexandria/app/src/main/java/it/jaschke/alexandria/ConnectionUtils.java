package it.jaschke.alexandria;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author s3xy4ngyc@googlemail.com
 */

public class ConnectionUtils {


    /**
     * Checks if the device has an active Internet Connection (Either WIFI or 3G)
     *
     * @param context
     * @return true if the device is connected to the internet, false otherwise
     */
    public static boolean isInternetConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
