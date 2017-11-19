package com.pafoid.utils.adapters;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pafoid.utils.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * FragmentStatePagerAdapter class used to display items in a ViewPager
 * Adds an abstraction layer to Android's ViewPager and PagerAdapter
 */
public class DefaultStateTabbedPagerAdapter extends TabbedPagerAdapter {

    private static final String TAG = "DefaultStTbdPgrAdptr";

    //Objects
    protected Context context;

    //Data
    protected FragmentType[] fragmentTypes;
    protected ArrayList<Fragment> fragments = new ArrayList<>();

    public DefaultStateTabbedPagerAdapter(FragmentManager fm, Context context, FragmentType[] fragmentTypes) {
        super(fm);
        this.context = context;
        this.fragmentTypes = fragmentTypes;
        validateFragmentTypes();
    }

    private void validateFragmentTypes() {
        ArrayList<Integer> indices = new ArrayList<>();
        for (FragmentType type : fragmentTypes) {
            for (int index : type.getIndicesList()) {
                if(indices.contains(index)){
                    throw new Error("FragmentType index " + index + " is referenced more than once.");
                }else{
                    indices.add(index);
                }
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        //Check if already instantiated
        if(fragments.size() >= position+1 && fragments.get(position) != null) {
            return fragments.get(position);
        }else{//Need to instantiate
            Fragment fragment = instantiateFragment(fragmentTypes[position]);
            fragments.add(position, fragment);

            return fragment;
        }
    }

    private Fragment instantiateFragment(FragmentType type){
        try {
            Method method = type.getClazz().getMethod("newInstance", type.getArgumentClasses());
            return (Fragment) method.invoke(type.getClazz().getConstructor().newInstance(), type.getArguments());
        } catch (Exception e) {
            Log.e(TAG, "Could not instantiate Fragment of type " + type.getItemType() + "\n" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getCount() {
        int count = 0;
        for (FragmentType type : fragmentTypes) {
            count += type.getIndicesList().size();
        }

        return count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getFragmentTypeAtPosition(position).getTitle();
    }

    private FragmentType getFragmentTypeAtPosition(int position) {
        for (FragmentType type : fragmentTypes) {
            if(type.getIndicesList().contains(position)) return type;
        }
        return null;
    }

    @Override
    public View getTabView(int position) {
        View tab = LayoutInflater.from(context).inflate(R.layout.toolbar_tab, null);
        TextView tabText = (TextView) tab.findViewById(R.id.tab_text);
        ImageView tabImage = (ImageView) tab.findViewById(R.id.tabimage);
        tabText.setText(getPageTitle(position));

        tabImage.setImageResource(getFragmentTypeAtPosition(position).getIconRes());

        if (position == 0) {
            tab.setSelected(true);
        }
        return tab;
    }

    /**
     * Static class used to represent the different view types that should be used with the {@link DefaultAdapter}
     */
    public static class FragmentType {
        private int itemType;
        private Integer[] indexes;
        private Class[] argumentClasses;
        private Object[] arguments;
        private Class clazz;
        private String title;
        private @DrawableRes
        int iconRes;

        /**
         * Constructor for ViewType
         * @param itemType the type of item (can be anything you choose)
         * @param title the title to use for the tab item
         * @param iconRes the icon Resource to use for the tab item
         * @param clazz the class to use when instantiating the item
         * @param indexes an array of integers representing the position of this particular item
         * @param argumentClasses an array of classes representing the classes of each params for the Fragment's constructor
         * @param arguments an array of Objects that will be used to call the constructor specified in the clazz param
         */
        public FragmentType(int itemType, String title, int iconRes, Class clazz, Integer[] indexes, Class[] argumentClasses, Object[] arguments) {
            this.itemType = itemType;
            this.title = title;
            this.iconRes = iconRes;
            this.clazz = clazz;
            this.indexes = indexes;
            this.argumentClasses = argumentClasses;
            this.arguments = arguments;
        }

        //Getters/Setters
        public int getItemType() {
            return itemType;
        }

        public void setItemType(int itemType) {
            this.itemType = itemType;
        }

        public Integer[] getIndexes() {
            return indexes;
        }

        public ArrayList<Integer> getIndicesList() {
            return new ArrayList<Integer>(Arrays.asList(indexes));
        }

        public void setIndexes(Integer[] indexes) {
            this.indexes = indexes;
        }

        public Class getClazz() {
            return clazz;
        }

        public void setClazz(Class clazz) {
            this.clazz = clazz;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getIconRes() {
            return iconRes;
        }

        public void setIconRes(int iconRes) {
            this.iconRes = iconRes;
        }

        public Class[] getArgumentClasses() {
            return argumentClasses;
        }

        public ArrayList<Class> getArgumentClassesList() {
            return new ArrayList<Class>(Arrays.asList(argumentClasses));
        }

        public void setArgumentClasses(Class[] argumentClasses) {
            this.argumentClasses = argumentClasses;
        }

        public Object[] getArguments() {
            return arguments;
        }

        public void setArguments(Object[] arguments) {
            this.arguments = arguments;
        }
    }
}
