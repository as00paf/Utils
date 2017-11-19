Utils - Activities
====

The *Utils Library* offers **Activities** that are often used in Android projects. Most of the
Activities require very small efforts to implements and customize for your own Applications.

##ImageGalleryActivity
This Activity uses a ViewPager to display Fragments that contains the custom view GestureImageView.
This View is basically an ImageView with extended capabilities like zooming. When launching the Activity,
you can pass it 2 bundle extras inside the Intent : 

**Images** : The key used for passing the list of images to show is found in the constants class (AppConstants.IMAGES)
and the value should be an ArrayList of IImages from the ImageGalleryFragment class (ArrayList<ImageGalleryFragment.IImageItem>) 

**Position** : The key used for passing the starting position of the ViewPager is also found in the constants
class (AppConstants.POSITION) and the value should be an integer that is between 0 and the IImages count.

##OnBoardingActivity
This Activity uses a ViewPager to display Fragments that contains text and images to introduce the users to your application and
to make them grant permissions for the specific features of your application. To use it in your project,
you will have to extend the abstract *OnBoardingActivity*, implement the abstract methods and add the Activity to your Manifest.
The ViewPager contains 3 items, an introduction Fragment, a GPS Fragment (for location services) and a
Storage Fragment (for sharing local content).
 