# VK Editor

### Подход к реализации

При разработке я руководствовался следующими соображениями:

* Сделать как можно проще, чтобы в проекте можно было разобраться :) (и само-собой, не раздувать APK)
* Сделать масштабируемый каркас: потому что если бы это был реальный проект, то его скорее всего захочется прокачать до уровня редактора сториз
* Сделать максимально близко к ТЗ

### Архитектура

Был использован MVVM подход, где всё взаимодействие между UI и моделью происходит через ViewModel+LiveData.

В вопросе LiveData vs RX выбрал первую, поскольку стремился использовать как можно более «нативные» инструменты. Но безусловно, такую же архитектуру можно повторить и на RX. 

### Фреймворк VKCanvas

Был выделен модуль vkcanvas, который реализует базовый функционал размещения произвольных объектов на плоскости и их перетаскивания/удаления.

Основное его преимущество в том, что он довольно абстрагирован от содержания и позволяет легко расширить функционал. Например:

* Добавить несколько блоков с текстом, которые можно было бы перемещать/вращать/удалять как стикеры
* Создавать любые комбинации стилей текста (уже можно)
* Добавить объекты других типов (дата/погода/хештег/местоположение)
* Сделать видео-редактор, расположив на фоне видео вместо картинки

#### Немного об устройстве VKCanvas

В основе vkcanvas лежит сущность объекта (VKCanvasObject), которая отображается либо как View внутри VKCanvasView, либо рисуется как Bitmap на graphics.Canvas с помощью VKCanvasRenderer.

По-умочанию выделены 2 объекта: картинка и текст:
* VKCanvasBitmapObject
* VKCanvasText
 
В модуле vkeditor уже есть расширения базового VKCanvasBitmapObject: BackgroundObject & StickerObject.

Каждому объекту соответсвует собственный View внутри VKCanvasView. Сейчас это:
* VKCanvasBitmapObjectView
* VKCanvasBitmapTextObjectView

Получается, что основная задача модуля vkeditor — собрать массив объектов VKCanvasObject и «отдать» модулю vkcanvas. vkeditor так же берет на себя задачу сортировки слоёв (один фон и текст, и много стикеров).

Если нам нужно создать новый объект (например погоду), то он мог бы быть реализован в следующих классах:
* `WeatherObject` (или внутри vkcanvas VKCanvasWeatherObject)
* `WeatherObjectView` extends `ViewGroup` implements `VKCanvasObjectView`
* Реализовать наследника `VKCanvasRenderer`, который будет уметь рисовать объект на graphics.Canvas

### Картинки

Работая с картинками, я руководствовался следующими соображениями:

* Потенциально, картинки могут лежать на сервере (но раз сервера у нас нет, то я постарался сжать ресурсы по максимому)
* Картинка, которая попала в память, всегда сжимается до нужных размеров родительского View, поэтому даже на медленном устройстве отклик хороший
* Пользователь может ставить на фон картинку очень больших размеров 

В вопросе «генерировать градиентные фоны или нет» я остановился на варианте, когда абсолютно все фоны сохранены в PNG. Они при этом занимают сравнительно мало места и это не усложняет проект. Но всегда можно вернуться к генерации на лету :)

### Допущения

* Я не стал создавать фреймворк тем и стилей, и ограничился минимально необходимыми стилями для решения задачи, поскольку всё зависит от проекта, в который может быть встроен редактор
* Если загрузка данных (фонов и стикеров) будет производиться с сервера, то будет необходимо дополнительно обрабатывать состояния загрузки, но для упрощения я опустил этот момент
* Пользователю устройства намеренно оставлено право использовать кастомные шрифты