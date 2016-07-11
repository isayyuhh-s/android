package slugapp.com.sluglife.fragments;

import slugapp.com.sluglife.enums.FragmentEnum;

/**
 * Created by isayyuhh on 2/21/16
 */
public abstract class BaseViewFragment extends BaseFragment {
    protected String mName;

    protected void setViewFragment(FragmentEnum fragmentEnum) {
        this.setFragment(fragmentEnum);
        this.setView();
    }

    protected void setViewFragment(FragmentEnum fragmentEnum, String name) {
        this.setFragment(fragmentEnum, name);
        this.setView();
    }

    protected abstract void setView();
}
