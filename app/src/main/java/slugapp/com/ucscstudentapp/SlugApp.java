package slugapp.com.ucscstudentapp;

import android.app.Application;

import slugapp.com.ucscstudentapp.http.Request;

/**
 * Created by simba on 7/31/15.
 */
public class SlugApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Request.init(this);
    }
}
