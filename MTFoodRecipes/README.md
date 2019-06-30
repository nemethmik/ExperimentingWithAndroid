# Food Recipes 
This is a remake and follow-along project for the tutorial video of 
[REST API with MVVM and Retrofit2](https://codingwithmitch.com/courses/rest-api-mvvm-retrofit2/testing-retrofit-response-objects/)
I have made a series of summary comment videos, too:
- [Java Programming with Android Studio 3.4 05 Retrofit for Food2Fork API Module](https://youtu.be/OiPtszDrcp8)

I am maintaining an ongoing accompanying document, too, on my one drive space.
[Food Recipe Tutorial on MVVM](https://onedrive.live.com/edit.aspx?cid=621775897637ee27&page=view&resid=621775897637EE27!4762&parId=621775897637EE27!2681&app=Word) 

# View Model Classes and Live Data Objects
 The absolute primary and only real job of VM classes is that they
 support life-cycle of Activities and Fragments, and they survive when
 these components are recreated. The way they are instantiated is the
 key to understanding their role/job:
 `ViewModelProviders.of(this).get(MainViewModel.class);` See my other
 project
 [View Model Demo - Chapter 32](https://github.com/nemethmik/ExperimentingWithAndroid/tree/master/AS34Essentials32ViewModelDemo)
 
 LiveData on the other hand are capable of working together with VMs to
 survive state changes. Live Data is used for the members in a VM class
 to hold observable data. Sandwiching a piece of data or entire data
 structure with Live Data gives observability to the data right away.
 But Live data and View Models are totally independent of each other.
 You can use Live Data without VMs and vice versa. Live Data is
 important for data binding, too. 
 
 Most people, including MT, make a VM class for each activity or a
 fragment, which is perfectly fine, since VM in the MVVM architecture is
 actually a mediator between the model and view. Xamarin Forms has a
 brilliant MVVM architecture, it's really a shame that Microsoft let
 Xamarin die. Still the separation of VM classes into a separate package
 is key, since they must be totally independent from the UI/View layer.
 This way any time a new UI layer could be implemented reusig the VM
 classes, since VM has nothing UI specific. If and when the entire
 application architecture is significantly changed, of course, the VM
 classes cannot be reused without modifications. The collection of VM
 classes can be regarded as the Application Layer (AL). In Java/Android
 programming here are the layers:
- Layout XMLs are actually the UI layer.
- The Java classes for activity and fragments are the UI Controllers. In
  Flutter these two are not separated. Honestly, These two layers are so
  much heavily intertwinned, that this XML - Java code separation is
  practically quite useless. Especially when most advanced Android
  programmers edit XML directly.
- VM classe are the Application Logic layer
- The model classes are for serializable data structures encapsulating
  raw data throughout the entire system. They are totally independent
  from all other layers/modules. Separating them out into a dedicated
  module would be totally reasonable for a medium/large project.
- The repository concept in Android Architecture is practically a
  centralized application logic componnet that holds data for multiple
  VMs, since a VM is paired with a single fragment/activity, when we
  need application data shared between multiple screens we need a
  repository. In my Mobile Architecture it was up to the designer if she
  wanted one AL object for the entire application, or a dedicated AL for
  each screen, that is the same as VM concept in Android Architecture,
  or use multiple AL objects each handling a bunch of screens.

## MVVM vs Mobile Architecture
In my Mobile Architecture I had no observers, whenever some data was
changed in the Application Layer it called explicitly Screen Commands
via an interface, and it was the job of the Screen to update the view.
Actually, this is the case with the observer model either, when the data
is changed the observable Live Data objects call the registered
observers notifying them about the data changes. The major difference,
however, that with the Live Data / observer model this is brutally easy
from technical perspective.

On the other hand, in my model the interface pair IXxxScreen and
IXxxEvents were brutall clean, the IXxxScreen had to be implemented by
the UI Controller, the IXxxEvents by the Application Logic. These wer a
formal contract between the communicating parties, you simple had a look
on the two interfaces, and was more or less clear how these two
woul/could work together. In Android MVVM practice there are no
interfaces. The VM has public observable Live Data data elements, but
there is no contract from the UI side. 

Maybe there is no need for two interfaces, in Adroid architecture
whenever a VM wants to send a command to the UI, it exposes a publicly
observable live data object and the UI listens to that channel. At the
same time the UI can trigger commands to the VM, too, by simply calling
the VM's any public methods:
`android:onClick="@{()->mainFragmentViewModel.convertValue()}"` 

That is, for a clean deasign the architects elaborates a single
communicaton interface with observable fields (live data) and a series
of event handlers exactly the same way as with the IXxxEvents interface
of the Mobile Architecture. In data binding we can use
`@={mv.someMutableLiveData}` which gives a bi-directional communication. I
wonder how to model this with an interface? Maybe just make
`MutableLiveData<String> getSomeMutableLiveData()` and that's all? 
