# ToDoApp

Приложение для управления задачами с синхронизацией данных через API-сервер.

## Содержание
- [О приложении](#о-приложении)
- [Скриншоты](#скриншоты)
- [Основные функции](#основные-функции)
- [Технологии](#технологии)
- [Установка](#установка)
  - [Требования](#требования)
  - [Пошаговая инструкция](#пошаговая-инструкция)
- [Настройка сервера](#настройка-сервера)
- [Использование приложения](#использование-приложения)
- [Структура проекта](#структура-проекта)
- [Лицензия](#лицензия)

## О приложении

ToDoApp - это мобильное приложение для Android, которое помогает пользователям управлять своими задачами. Приложение позволяет создавать, редактировать, помечать как выполненные и удалять задачи. Кроме того, задачи можно сортировать по категориям и приоритетам, а также устанавливать дедлайны.

Приложение синхронизирует данные с сервером, что позволяет пользователям иметь доступ к своим задачам с разных устройств.

## Скриншоты

*Здесь будут скриншоты приложения*

## Основные функции

- **Авторизация и регистрация**: Система аутентификации пользователей
- **Управление задачами**: Создание, редактирование, завершение и удаление задач
- **Категории**: Создание собственных категорий и управление стандартными категориями
- **Приоритеты**: Установка приоритетов для задач (НИЗКИЙ, СРЕДНИЙ, ВЫСОКИЙ)
- **Дедлайны**: Установка крайних сроков выполнения задач
- **Поиск**: Функция поиска с сохранением истории поисковых запросов
- **Фильтрация**: Разделение задач на активные, выполненные и удаленные
- **Темная тема**: Возможность переключения между светлой и темной темой

## Технологии

- **Kotlin**: Основной язык программирования
- **Jetpack Compose**: Современный инструментарий для разработки UI
- **MVVM**: Архитектурный паттерн Model-View-ViewModel
- **Coroutines + Flow**: Для асинхронных операций и реактивного программирования
- **Retrofit**: Для HTTP-запросов к API
- **OkHttp**: HTTP-клиент для Android
- **Material Design 3**: Для современного дизайна интерфейса

## Установка

### Требования

- Android Studio Electric Eel (2022.1.1) или новее
- JDK 11 или новее
- Android SDK 33 или новее
- Устройство или эмулятор с Android 8.0 (API 26) или выше

### Пошаговая инструкция

#### 1. Скачивание проекта

Есть два способа скачать проект:

**Вариант 1: Скачать ZIP-архив**
1. Перейдите на [страницу проекта на GitHub](https://github.com/your-username/ToDoApp)
2. Нажмите на зеленую кнопку "Code"
3. В выпадающем меню выберите "Download ZIP"
4. Разархивируйте скачанный файл в удобное для вас место

**Вариант 2: Клонирование через Git**
1. Установите Git с [официального сайта](https://git-scm.com/downloads)
2. Откройте командную строку или терминал
3. Перейдите в директорию, где хотите сохранить проект
4. Выполните команду:
   ```
   git clone https://github.com/your-username/ToDoApp.git
   ```

#### 2. Открытие проекта в Android Studio

1. Запустите Android Studio
2. Выберите "Open an existing project"
3. Найдите директорию, куда вы сохранили проект, и выберите её

#### 3. Настройка проекта

1. После открытия проекта Android Studio начнет загрузку необходимых зависимостей и синхронизацию Gradle
2. Дождитесь завершения этого процесса (прогресс отображается в нижней части экрана)

#### 4. Настройка подключения к серверу

1. Откройте файл `app/src/main/java/com/example/todoapp/api/ApiClient.kt`
2. Найдите строку с `private const val BASE_URL`
3. Измените URL на адрес вашего сервера (по умолчанию "http://10.0.2.2:8080/" для эмулятора)

#### 5. Запуск приложения

1. Подключите Android-устройство к компьютеру через USB или запустите эмулятор
   - Для запуска эмулятора: в верхней панели Android Studio нажмите на "AVD Manager" и создайте или запустите виртуальное устройство
2. Нажмите на зеленую кнопку "Run" (▶️) в верхней панели Android Studio
3. Выберите устройство, на котором хотите запустить приложение
4. Дождитесь установки и запуска приложения

## Настройка сервера

Для полноценной работы приложения требуется серверная часть. Сервер доступен в отдельном репозитории:

1. Перейдите на [страницу сервера на GitHub](https://github.com/berrrchik/ToDoServer-Kotlin)
2. Следуйте инструкциям в README.md этого репозитория для установки и запуска сервера

## Использование приложения

### Регистрация и вход

1. При первом запуске приложения откроется экран входа
2. Если у вас еще нет аккаунта, нажмите на кнопку "Регистрация"
3. Заполните форму регистрации: логин, email и пароль
4. После успешной регистрации вы будете автоматически авторизованы и перенаправлены на главный экран

### Работа с задачами

1. **Создание задачи**:
   - Нажмите на кнопку "+" в нижнем правом углу главного экрана
   - Заполните форму создания задачи (название, описание, категория, приоритет, дедлайн)
   - Нажмите "Создать задачу"

2. **Редактирование задачи**:
   - Нажмите на иконку редактирования (карандаш) у нужной задачи
   - Внесите необходимые изменения
   - Нажмите "Сохранить изменения"

3. **Завершение задачи**:
   - Нажмите на иконку галочки у задачи, чтобы отметить её как выполненную
   - Задача переместится в раздел "Выполненные задачи"

4. **Удаление задачи**:
   - Нажмите на иконку корзины у задачи
   - Задача переместится в раздел "Удаленные задачи"

### Управление категориями

1. **Просмотр категорий**:
   - Нажмите на иконку категорий в верхней панели
   - Вы увидите список всех категорий

2. **Создание категории**:
   - На экране категорий нажмите кнопку "+"
   - Введите название новой категории
   - Нажмите "Добавить"

3. **Скрытие/отображение стандартных категорий**:
   - Нажмите на соответствующую иконку у категории

## Структура проекта

```
app/src/main/java/com/example/todoapp/
├── api/                  # API-клиенты и интерфейсы
├── components/           # Переиспользуемые UI-компоненты
├── model/                # Модели данных
│   ├── category/         # Модели для категорий
│   └── task/             # Модели для задач
├── navigation/           # Навигация между экранами
├── repository/           # Репозитории для доступа к данным
├── screens/              # UI-экраны приложения
├── storage/              # Локальное хранение данных
├── ui/                   # UI-темы и стили
│   └── theme/
├── utils/                # Вспомогательные утилиты
└── viewmodel/            # ViewModels для экранов
```

## Лицензия

Этот проект распространяется под лицензией MIT. Подробности можно узнать в файле LICENSE.
