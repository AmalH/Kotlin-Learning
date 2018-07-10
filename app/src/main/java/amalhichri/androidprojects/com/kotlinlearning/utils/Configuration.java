package amalhichri.androidprojects.com.kotlinlearning.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Odil on 15/11/2017.
 */

public class Configuration {

    //public final static String IP="http://ikotlin.000webhostapp.com/web/";
    public final static String IP="http://41.226.11.243:10080/ikotlin/public_html/web/app.php/";

    // TCP/HTTP/DNS (depending on the port, 53=DNS, 80=HTTP, etc.)
    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
