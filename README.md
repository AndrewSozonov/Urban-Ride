# Urban Ride

### Описание
Навигатор для поездок на велосипеде. Отслеживает и рисует маршрут на карте. Отображает данные о поездке.

### Обзор
При запуске приложения необходимо разрешить геолококацию на устройстве.

Нажав на кнопку start приложение начинает отслеживать геолокацию и рсовать маршрут на карте.
При этом на экране отображаются таймер, скорость, расстояние и средняя скорость поездки.
Нажав на паузу можно приостановить рисование маршрута на карте, данные при этом приходить тоже не будут.

Нажав на стоп поездка завершается и данные сохраняются в базу. 
Посмотреть на историю поездок можно перейдя на вкладку History.
При нажатии на поездку откроется окно с детальной информацией и изображением маршрута.

Так же есть экран настроек где можно выбрать темную/светлую темы, и единицы измерения мили или километры. 

### Стек
* [GoogleMaps SDK](https://developers.google.com/maps/documentation/android-sdk/overview)
* [RxJava2](https://github.com/ReactiveX/RxJava)
* [Dagger2](https://github.com/google/dagger)
* [Glide](https://bumptech.github.io/glide/)
* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
* [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
* [Navigation](https://developer.android.com/topic/libraries/architecture/navigation/) 
