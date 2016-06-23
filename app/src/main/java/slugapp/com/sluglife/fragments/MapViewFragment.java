package slugapp.com.sluglife.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import slugapp.com.sluglife.R;
import slugapp.com.sluglife.enums.FragmentEnum;

/**
 * Created by isayyuhh on 6/20/16
 */
public class MapViewFragment extends BaseViewFragment {
    private static final FragmentEnum fragmentEnum = FragmentEnum.MAP;

    private View mSearchBar;
    private String mQuery;
    private EditText mSearchEditText;

    private boolean searchShowing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_map, container, false);

        this.setViewFragment(view, fragmentEnum);

        return view;
    }

    @Override
    protected void setFields(View view) {
        this.mSearchBar = view.findViewById(R.id.search);

        this.searchShowing = false;
    }

    @Override
    protected void setView(final View view) {
        MapFragment fragment = new MapFragment();

        FragmentManager fm = this.getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.map_view, fragment);
        ft.commit();

        this.mSearchEditText = (EditText) view.findViewById(R.id.search_edit_text);
        this.mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mQuery = s.toString();

                if (!mQuery.isEmpty()) {
                    FacilityListFragment fragment = new FacilityListFragment();

                    Bundle b = new Bundle();
                    b.putString(mContext.getString(R.string.bundle_query), mQuery);
                    fragment.setArguments(b);

                    FragmentManager fm = getChildFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.map_view, fragment);
                    ft.commit();
                } else {
                    MapFragment fragment = new MapFragment();

                    FragmentManager fm = getChildFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.map_view, fragment);
                    ft.commit();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_map_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                return true;
            case R.id.search:
                if (!this.searchShowing) {
                    this.mSearchBar.setVisibility(View.VISIBLE);
                    this.searchShowing = true;
                } else {
                    this.mSearchBar.setVisibility(View.GONE);
                    this.searchShowing = false;
                    this.mSearchEditText.setText("");

                    if (!this.mQuery.isEmpty()) {
                        MapFragment fragment = new MapFragment();

                        FragmentManager fm = this.getChildFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.map_view, fragment);
                        ft.commit();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}