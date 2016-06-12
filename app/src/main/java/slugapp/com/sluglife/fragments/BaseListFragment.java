package slugapp.com.sluglife.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Comparator;

import slugapp.com.sluglife.R;
import slugapp.com.sluglife.adapters.BaseListAdapter;
import slugapp.com.sluglife.models.BaseObject;

/**
 * Created by isayyuhh on 2/21/16.
 */
public abstract class BaseListFragment extends BaseFragment {
    protected BaseListAdapter mAdapter;

    protected void setView(View view, BaseListAdapter adapter) {
        this.mAdapter = adapter;
        ListView listView = (ListView) view.findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListItemListener());
    }

    protected void setSearchView(Menu menu) {
        this.getActivity().findViewById(R.id.toolbar_title).setVisibility(View.VISIBLE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        SearchManager searchManager =
                (SearchManager) this.mContext.getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().findViewById(R.id.toolbar_title).setVisibility(View.GONE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getActivity().findViewById(R.id.toolbar_title).setVisibility(View.VISIBLE);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                doSearch(query);
                return true;
            }
        });
    }

    protected abstract void doSearch(String query);

    protected abstract int doSort(BaseObject lhs, BaseObject rhs);

    protected abstract void onClick(AdapterView<?> parent, View view, int position, long id);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.search || super.onOptionsItemSelected(item);
    }

    /*
     * Sorts ArrayList<Event> By Dates
     */
    protected class ListSort implements Comparator<BaseObject> {
        @Override
        public int compare(BaseObject lhs, BaseObject rhs) {
            return doSort(lhs, rhs);
        }
    }

    protected class ListItemListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCallback.hideKeyboard();
            onClick(parent, view, position, id);
        }
    }
}