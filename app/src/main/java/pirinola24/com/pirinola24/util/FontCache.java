package pirinola24.com.pirinola24.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.Hashtable;

/**
 * Created by geovanny on 20/02/16.
 */
public class FontCache
{

    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

    public static Typeface get(String name, Context context) {
        Typeface tf = fontCache.get(name);
        if(tf == null) {
            try {
                //Log.i("info:","no esta en el cache");
                tf = Typeface.createFromAsset(context.getAssets(), name);
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        /*else
        {
            Log.i("info:","ya esta en el cache");
        }*/
        return tf;
    }
}
