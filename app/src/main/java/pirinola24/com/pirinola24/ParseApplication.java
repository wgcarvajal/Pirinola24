package pirinola24.com.pirinola24;

import android.app.Application;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

import com.backendless.Backendless;

public class ParseApplication extends Application
{
    public final String APPID="9jYgRqNvDzx0xI1Z8ogHy6F1I30FuB1RN0Zm73HN";
    public final String CLIENTKEY="y2QVHeVu4G8SWbahv8S6EdWjc2eMvVIo0ZS53Aqt";

    public final String YOUR_APP_ID="B53B1BEE-A372-0051-FF78-52D40FC4F000";
    public final String YOUR_SECRET_KEY="A2133936-A820-7628-FF9F-EED131092000";

    @Override
    public void onCreate()
    {
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, APPID, CLIENTKEY);
        //ParseFacebookUtils.initialize(this);
        FacebookSdk.sdkInitialize(this);
        String appVersion = "v1";
        Backendless.initApp(this, YOUR_APP_ID, YOUR_SECRET_KEY, appVersion);
    }
}