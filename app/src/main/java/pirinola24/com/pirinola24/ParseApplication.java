package pirinola24.com.pirinola24;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ParseApplication extends Application
{
    public final String APPID="9jYgRqNvDzx0xI1Z8ogHy6F1I30FuB1RN0Zm73HN";
    public final String CLIENTKEY="y2QVHeVu4G8SWbahv8S6EdWjc2eMvVIo0ZS53Aqt";

    @Override
    public void onCreate()
    {
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, APPID, CLIENTKEY);
        ParseFacebookUtils.initialize(this);
        FacebookSdk.sdkInitialize(this);
    }
}