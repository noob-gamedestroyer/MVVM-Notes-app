# MVVM-Notes-app
This is simple notes app that follows MVVM architectural design pattern and uses android jetpack components.

## MVVM Architecture

__MVVM__ - MVVM stands for Model, View, ViewModel. MVVM is one of the architectural patterns which enhances separation of concerns, it allows separating the user interface logic from the business (or the back-end) logic. Its target is to achieve the following principle “Keeping UI code simple and free of app logic in order to make it easier to manage”.          


![mvvm_architecture](https://user-images.githubusercontent.com/60071765/94697016-50584e00-0355-11eb-924e-4ea28814b94e.png)


## Android Jetpack components:-
1. __Navigation Components__ - Navigation component helps you implement navigation, from simple button clicks to more complex patterns, such as app bars and the navigation drawer. The Navigation component also ensures a consistent and predictable user experience by adhering to an established set of principles.

2. __Android Room Persistence__ - It is a SQLite object mapping library. Use it to Avoid boilerplate code and easily convert SQLite table data to Java objects. Room provides compile time checks of SQLite statements and can return RxJava, Flowable and LiveData observables.

3. __Kotlin Coroutines__ - A coroutine is a concurrency design pattern that you can use on Android to simplify code that executes asynchronously. On Android, coroutines help to manage long-running tasks that might otherwise block the main thread and cause your app to become unresponsive.

4. __ViewModel__ - It manages UI-related data in a lifecycle-conscious way. It stores UI-related data that isn't destroyed on app rotations.

5. __LiveData__ - It notifies views of any database changes. Use LiveData to build data objects that notify views when the underlying database changes.

6. __Kotlin__ - Kotlin is a modern statically typed programming language used by over 60% of professional Android developers that helps boost productivity, developer satisfaction, and code safety.

          It also uses RecyclerView with DiffUtill to improves overall app performances

## Features:-
1. Save Note In a Local db
2. Update
3. Swipe To Delete
4. Search
5. Color Picker (Colorful notes)
5. RecyclerView Animations

## App ScreenShots:-

![ezgif com-gif-maker](https://user-images.githubusercontent.com/60071765/94704102-302c8d00-035d-11eb-9035-e04487341b14.gif)
![ezgif com-gif-maker (1)](https://user-images.githubusercontent.com/60071765/94704994-28211d00-035e-11eb-859a-7ed354b1e2be.gif)




![screenshot_20200930-192853_not](https://user-images.githubusercontent.com/60071765/94698129-9feb4980-0356-11eb-836b-859bfc29bd01.png)
![screenshot_20200930-192914_not](https://user-images.githubusercontent.com/60071765/94698702-33bd1580-0357-11eb-873e-df51a27ff3e7.png)

## Libraries Used:-
   <h4>Library used</h4>
<ul>
<li><a href="https://developer.android.com/topic/libraries/architecture/room" target="_blank">Room</a></li>
<li><a href="https://developer.android.com/topic/libraries/architecture/viewmodel" target="_blank">Viewmodel</a></li>
<li><a href="https://developer.android.com/topic/libraries/architecture/livedata">Livedata</a></li>
<li><a href="https://developer.android.com/kotlin/coroutines" target="_blank">Coroutines</a></li>
<li><a href="https://material.io/develop/android/docs/getting-started/" target="_blank">Material library</a></li>
<li><a href="https://developer.android.com/guide/navigation/navigation-getting-started" target="_blank">Navigation Component</a></li>
   
</ul>

