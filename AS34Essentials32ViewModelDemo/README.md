# Simple Fragment View Model Integration
The 1st version was based on chapter 32 of the book Android Studion 3.4
Essentials/Java. Here the event handler of the button click is an
anonymous object. The application/controller logic is not separated from
the UI intricacies. View Model is fine for business logic, nevertheless.

` mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);  
mViewModel = new MainViewModel()`; 

When the ViewModel object is created with the normal way, rotating the
phone preserves the result text on the screen. When created with the
plain `new` operator, the result text is not preserved upon rotating the
phone. It was interesting to see that a ViewModel object can be
instantiated with the new operator.

