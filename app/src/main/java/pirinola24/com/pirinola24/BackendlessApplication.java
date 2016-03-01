package pirinola24.com.pirinola24;

import android.app.Application;
import com.facebook.FacebookSdk;

import com.backendless.Backendless;

public class BackendlessApplication extends Application
{

    public final String YOUR_APP_ID="B53B1BEE-A372-0051-FF78-52D40FC4F000";
    public final String YOUR_SECRET_KEY="A2133936-A820-7628-FF9F-EED131092000";

    @Override
    public void onCreate()
    {

        FacebookSdk.sdkInitialize(this);
        String appVersion = "v1";
        Backendless.initApp(this, YOUR_APP_ID, YOUR_SECRET_KEY, appVersion);
    }
}