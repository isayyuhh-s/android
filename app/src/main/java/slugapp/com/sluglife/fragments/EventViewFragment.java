package slugapp.com.sluglife.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import slugapp.com.sluglife.R;
import slugapp.com.sluglife.enums.FragmentEnum;
import slugapp.com.sluglife.models.Event;
import slugapp.com.sluglife.http.ImageHttpRequest;

/**
 * Created by isaiah on 6/27/2015.
 * <p/>
 * This file displays all of the contents for each individual mEvent.
 */
public class EventViewFragment extends BaseViewFragment {
    private FragmentEnum fragmentEnum = FragmentEnum.EVENT;

    private Event mEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = this.getArguments();

        this.mEvent = this.mCallback.getGson().fromJson(b.getString(this.mContext.getString(R.string.bundle_json)), Event.class);
        this.mName = this.mEvent.name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_event, container, false);

        this.setDetailFragment(view, this.fragmentEnum, this.mName);

        return view;
    }

    @Override
    protected void setFields(View view) {
    }

    @Override
    protected void setView(View view) {
        TextView day = (TextView) view.findViewById(R.id.day);
        TextView start = (TextView) view.findViewById(R.id.start);
        TextView end = (TextView) view.findViewById(R.id.end);
        TextView description = (TextView) view.findViewById(R.id.description);
        ImageView image = (ImageView) view.findViewById(R.id.image);

        if (mEvent.date.defined) {
            String dayString = "When: " + mEvent.date.month.getVal() + " " +
                    mEvent.date.day;
            String startString = "Starts: " + String.valueOf(mEvent.date.startTime) +
                    mEvent.date.startTOD;
            String endString = "Ends: " + String.valueOf(mEvent.date.endTime) +
                    mEvent.date.endTOD;

            day.setText(dayString);
            start.setText(startString);
            end.setText(endString);
        } else day.setText("When: " + mEvent.date.string);

        description.setText(mEvent.summary);
        new ImageHttpRequest(mEvent.image).execute(image);
    }
}
