package com.pafoid.utils.activities;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.pafoid.utils.R;
import com.pafoid.utils.utils.BottomNavigationUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Abstract Activity that contains a BottomNavigation
 *
 * Extend this Activity and use it in your project paired with
 * Create the bottom_nav_items.xml menu file to define your Bottom Sheet menu
 * Change colors in nav_item_color_state
 * Once your data is all loaded, call onDataLoaded()
 */
public abstract class BottomNavActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "BottomSheetActivity";

    private static final String SELECTED_ITEM = "selectedItem";
    private static final String FRAGMENT_HOME = "home";
    private static final String FRAGMENT_OTHER = "other";

    //Views
    protected Toolbar toolbar;
    protected BottomNavigationView bottomNav;
    protected ArrayList<Fragment> fragments;

    //Data
    protected int selectedItem = 0;
    protected int previousSectionIndex = -1;
    protected ArrayList<BottomNavigationSection> sections;
    private Bundle savedInstanceState;
    protected Object data;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bottom_nav);

        initToolbar();
        sections = initSections();
        this.savedInstanceState = savedInstanceState;
        loadData();
    }

    protected void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    protected void initNavigationView(Bundle savedInstanceState) {
        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(this);

        //Disable shifting
        BottomNavigationUtils.disableShiftMode(bottomNav);

        MenuItem selectedItem;
        if (savedInstanceState != null) {
            this.selectedItem = savedInstanceState.getInt(SELECTED_ITEM, 0);
            selectedItem = bottomNav.getMenu().findItem(this.selectedItem);
        } else {
            selectedItem = bottomNav.getMenu().getItem(0);
        }
        showFragment(selectedItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
                finish();
            return true;
        }

        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        showFragment(item);
        BottomNavigationUtils.disableShiftMode(bottomNav);
        return true;
    }

    private void showFragment(MenuItem item) {
        if(item == null) {
            Log.e(TAG, "Menu item is null!");
            return;
        }

        int itemId = item.getItemId();
        BottomNavigationSection section = getSectionById(itemId);
        int index = sections.indexOf(section);
        if(section == null) throw new Error("This section id is not associated");
        Fragment frag = instantiateFragment(section);

        int enterAnimRes = R.anim.slide_in_left;
        int exitAnimRes = R.anim.slide_out_right;
        int popEnterAnimRes = 0;
        int popExitAnimRes = 0;
        if(selectedItem != 0){
            BottomNavigationSection prevSection = getSectionById(selectedItem);
            int currentIndex =  sections.indexOf(prevSection);
            int selectedIndex = index;
            int pageDelta = selectedIndex - currentIndex;
            Log.d(TAG, "currentIndex : " + currentIndex);
            Log.d(TAG, "selectedIndex : " + selectedIndex);
            Log.d(TAG, "Delta : " + pageDelta);

            if(pageDelta == 0) return;

            if(pageDelta > 0){
                enterAnimRes = R.anim.slide_in_right;
                exitAnimRes = R.anim.slide_out_left;
                popEnterAnimRes = R.anim.slide_in_left;//R.anim.slide_out_right;
                popExitAnimRes = R.anim.slide_out_right;//R.anim.slide_in_left;
            }
        }

        BottomNavigationSection previousSection = getSectionById(selectedItem);
        previousSectionIndex = sections.indexOf(previousSection);

        selectedItem = item.getItemId();

        for (int i = 0; i< bottomNav.getMenu().size(); i++) {
            MenuItem menuItem = bottomNav.getMenu().getItem(i);
            menuItem.setChecked(false);
        }
        item.setChecked(true);

        toolbar.setTitle(item.getTitle());

        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(enterAnimRes, exitAnimRes, popEnterAnimRes, popExitAnimRes);
            final int count = getSupportFragmentManager().getBackStackEntryCount();
            if(index != 0) ft.addToBackStack(FRAGMENT_OTHER);
            ft.replace(R.id.content, frag, frag.getClass().getName());

            if(index != 0){
                getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        if( getSupportFragmentManager().getBackStackEntryCount() <= count){
                            getSupportFragmentManager().removeOnBackStackChangedListener(this);
                            getSupportFragmentManager().popBackStack(FRAGMENT_OTHER, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                            //if(previousSectionIndex >= 0) showFragment(bottomNav.getMenu().getItem(previousSectionIndex));
                            showFragment(bottomNav.getMenu().getItem(0));
                        }
                    }
                });
            }

            ft.commit();
        }
    }

    private Fragment instantiateFragment(BottomNavigationSection section){
        /*if(fragments != null && fragments.size() > 0){
            for (Fragment fragment : fragments) {
                if(fragment.getClass() == section.getViewType()) return fragment;
            }
        }else{
            fragments = new ArrayList<>();
        }*/

        try {
            Method method = section.getViewType().getMethod("newInstance");
            Fragment fragment = (Fragment) method.invoke(section.getViewType().getConstructor().newInstance());
            //fragments.add(fragment);

            return fragment;
        } catch (Exception e) {
            Log.e(TAG, "Could not instantiate Fragment of type " + section.getViewType() + "\n" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    protected BottomNavigationSection getSectionById(int itemId) {
        if(sections == null || sections.size() == 0) throw new Error("Sections have not been initialized. Please implement initSections()");

        for (BottomNavigationSection section : sections) {
            if(itemId == section.getMenuItemId()){
                return section;
            }
        }

        return null;
    }


    protected void onDataLoaded(){
        initNavigationView(savedInstanceState);
    }

    //Abstract methods
    protected abstract ArrayList<BottomNavigationSection> initSections();

    protected abstract void loadData();

    //Getters/Setters
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


    //Objects
    /**
     * Helper class used to represent a section of the Bottom Navigation
     */
    public static class BottomNavigationSection{

        public static String TAG = "BottomNavSection";

        @IdRes
        private int menuItemId = -1;

        private String title;

        private Class viewType;

        private BottomNavActivity.BottomNavigationSection.Delegate delegate;

        public BottomNavigationSection(int menuItemId, String title, Class viewType) {
            this.menuItemId = menuItemId;
            this.title = title;
            this.viewType = viewType;
        }

        public BottomNavigationSection(int menuItemId, String title, Class viewType, BottomNavActivity.BottomNavigationSection.Delegate delegate) {
            this.menuItemId = menuItemId;
            this.title = title;
            this.viewType = viewType;
            this.delegate  = delegate;
        }

        //Getters/Setters
        public int getMenuItemId() {
            return menuItemId;
        }

        public void setMenuItemId(int menuItemId) {
            this.menuItemId = menuItemId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Class getViewType() {
            return viewType;
        }

        public void setViewType(Class viewType) {
            this.viewType = viewType;
        }

        public BottomNavActivity.BottomNavigationSection.Delegate getDelegate() {
            return delegate;
        }

        public void setDelegate(BottomNavActivity.BottomNavigationSection.Delegate delegate) {
            this.delegate = delegate;
        }

        public interface Delegate{
            void onSectionSelected(int id);
        }
    }
}
