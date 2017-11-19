Utils - DefaultAdapter
====

The Utils Library offers a *DefaultAdapter* that can be used to display items in a list easily.
It can be configured to display a single or multiple item types which is useful when a list needs to have headers before items.

##Usage
Using the *DefaultAdapter* is made to be very easy. As stated previously, it can be used with a single or multiple item types.
It is meant to be used with the DefaultViewHolder class and DefaultAdapter.ViewType.

###Single Item Type

```java
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (RecyclerView) findViewById(R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(layoutManager);
        
        //Single item type
        DefaultAdapter<TestViewHolder> adapter = new DefaultAdapter(R.layout.list_item, Arrays.asList(getResources().getStringArray(R.array.items)), TestViewHolder.class);
        list.setAdapter(adapter);
    }
}
```

##Multiple Item Types
```java
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (RecyclerView) findViewById(R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(layoutManager);
        
        //Multiple item types
        DefaultAdapter.ViewType[] viewTypes = new DefaultAdapter.ViewType[]{
                new DefaultAdapter.ViewType(R.layout.list_header, DefaultAdapter.TYPE_HEADER, TestViewHolder.class, new Integer[]{0, 6}),
                new DefaultAdapter.ViewType(R.layout.list_item, DefaultAdapter.TYPE_ITEM, TestViewHolder.class)
        };

        DefaultAdapter<TestViewHolder> adapter = new DefaultAdapter(viewTypes, Arrays.asList(getResources().getStringArray(R.array.items)));
        list.setAdapter(adapter);
       }
    }
```