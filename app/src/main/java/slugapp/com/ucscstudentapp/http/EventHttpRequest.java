package slugapp.com.ucscstudentapp.http;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import slugapp.com.ucscstudentapp.event.Event;
import slugapp.com.ucscstudentapp.event.EventWrapper;

/**
 * Created by simba on 7/31/15.
 */
public class EventHttpRequest extends HttpRequest {
    private static final String url =
            "http://ec2-52-8-25-141.us-west-1.compute.amazonaws.com/events/get/v1";
    public EventHttpRequest() {
        super(url, Method.GET);
    }

    public void execute(final Callback<List<Event>> callback) {
        rawExecute(new Callback<String>() {
            @Override
            public void onSuccess(String val) {
                try {
                    JSONArray arr = new JSONArray(val);
                    List<Event> events = new ArrayList<>(arr.length());
                    for(int i = 0; i < arr.length(); ++i) {
                        events.add(new EventWrapper(arr.getJSONObject(i)));
                    }
                    callback.onSuccess(events);
                } catch(JSONException je) {
                    callback.onError(je);
                }
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }
}
