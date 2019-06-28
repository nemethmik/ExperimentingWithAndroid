# Android Studio Essentials 3.4 (46) Card and Recycler View Demo

I used the Project Structure screen to define dependencies. On that
screen in the search box when I entered recyclerview it didn't show
desired com.android.support:recyclerview, so the only I entered
com.android.support in the search box and I picked the recyclerview and
cardview libraries. But I think this is absolutely not necessary, since
when we use the layout designer and place a recyclerview or cardview
onto the layout, AS 3.4 automatically includes the necessary libraries
and it doesn't change the dependencies files at all. So including
explicitly recyclerview and cardview library dependencies in the gradle
files is not needed, maybe it gives more harm then help, since AS
automatically uses the latest versions of the necessary libraries.

To reuse what I've learned from the data binding tutorials, I have
applied some data binding preparatory steps.
- I have added `layout` top level tag to all XML files
- In the app module build.gradle within the android section I added
  `dataBinding.enabled = true;`
- In the MainActivity `onCreate` I started using the binding class. The
  section on Google android developer site
  [Layout and binding expressions](https://developer.android.com/topic/libraries/data-binding/expressions)
  gives excellent description what to do.
  
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);
        binding.fab.setOnClickListener(new View.OnClickListener() { ...
        
- Getting a layout inflater in the adapter's onCreateHolder(viewGroup,i)
  was tricky and I found a solution in the Kotlin example
  [How to bind a list of items to a RecyclerView with Android Data Binding](https://android.jlelse.eu/how-to-bind-a-list-of-items-to-a-recyclerview-with-android-data-binding-1bd08b4796b4)
  `LayoutInflater.from(viewGroup.getContext())`
  
  
