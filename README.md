# Urban Ride

### Описание
Навигатор для поездок на велосипеде. Отслеживает и рисует маршрут на карте. Отображает данные о поездке.

### Обзор
При запуске приложения необходимо разрешить геолококацию на устройстве.

Нажав на кнопку start приложение начинает отслеживать геолокацию и рисовать маршрут на карте.
На экране отображаются таймер, скорость, расстояние и средняя скорость поездки.
Нажав на паузу можно приостановить поездку, рисование маршрута на карте и данные при этом отображаться не будут.

Нажав на стоп поездка завершается и данные сохраняются в базу. 
Посмотреть на историю поездок можно перейдя на вкладку History.
При нажатии на поездку откроется окно с детальной информацией и изображением маршрута.

На экране History отображается график скорость/время, скорость/дистанция выбранной поездки.
Так же можно расшарить картинку с маршрутом и информацией о поездке.

Есть экран настроек, где можно выбрать темную/светлую темы, и единицы измерения мили или километры. 

### Screenshots
* [Ride screen](https://github.com/AndrewSozonov/Urban-Ride/blob/master/img/Screenshot_main.png)
* [Ride screen](https://github.com/AndrewSozonov/Urban-Ride/blob/master/img/Screenshot_main2.png)
* [History screen](https://github.com/AndrewSozonov/Urban-Ride/blob/master/img/Screenshot_history.png)

### Стек
* [GoogleMaps SDK](https://developers.google.com/maps/documentation/android-sdk/overview)
* [RxJava2](https://github.com/ReactiveX/RxJava)
* [Dagger2](https://github.com/google/dagger)
* [Glide](https://bumptech.github.io/glide/)
* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
* [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
* [Navigation](https://developer.android.com/topic/libraries/architecture/navigation/) 
* [GraphView](https://github.com/jjoe64/GraphView) 
