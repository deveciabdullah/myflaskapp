# Yapılacaklar (TodoApp)

Kotlin ve Jetpack Compose ile yazılmış, verilerini cihazda Room veritabanında saklayan
basit bir yapılacaklar listesi uygulaması.

## Özellikler

- Görev ekleme, tamamlandı olarak işaretleme ve silme
- Silinen görevi Snackbar üzerinden **geri alma**
- Tümü / Bekleyen / Biten filtreleri, her birinde sayaç
- Tamamlanan görevleri toplu temizleme
- Uygulama kapansa da kalıcı kayıt (Room)
- Material 3, açık/koyu tema ve Android 12+ üzerinde Material You dinamik renkler

## Teknolojiler

| Katman | Seçim |
| --- | --- |
| Dil | Kotlin 2.0.21 |
| Arayüz | Jetpack Compose, Material 3 |
| Mimari | MVVM — `ViewModel` + `StateFlow` + Repository |
| Veri | Room 2.6.1 (KSP ile) |
| Derleme | Gradle 8.11.1, AGP 8.7.3, sürüm kataloğu (`gradle/libs.versions.toml`) |

`minSdk 26`, `targetSdk 35`, JDK 17.

## Çalıştırma

Android Studio ile projeyi açıp **Run**'a basmak yeterli. Komut satırından:

```bash
./gradlew assembleDebug          # APK üret -> app/build/outputs/apk/debug/
./gradlew installDebug           # bağlı cihaza/emülatöre kur
./gradlew test                   # birim testleri
./gradlew lint                   # Android Lint
```

Android SDK'nın kurulu olması ve `ANDROID_HOME` ortam değişkeninin ya da
`local.properties` içindeki `sdk.dir` satırının SDK dizinini göstermesi gerekir.
`local.properties` sürüm kontrolüne dahil edilmez; Android Studio ilk açılışta üretir.

## Proje yapısı

```
app/src/main/java/com/deveciabdullah/todo/
├── MainActivity.kt            # tek Activity, Compose giriş noktası
├── TodoApplication.kt         # Application + basit bağımlılık konteyneri
├── data/
│   ├── Task.kt                # Room entity
│   ├── TaskDao.kt             # sorgular, Flow döner
│   ├── TodoDatabase.kt        # veritabanı tanımı
│   └── TaskRepository.kt      # DAO üzerinde iş kuralları
└── ui/
    ├── TaskViewModel.kt       # UI durumu, filtreleme, geri alma
    ├── TaskListScreen.kt      # Compose ekranı
    └── theme/                 # Material 3 tema ve tipografi
```

## Testler

`app/src/test/` altında JVM üzerinde çalışan birim testleri var; `FakeTaskDao`
Room yerine bellek içi bir uygulama sağladığı için emülatör gerekmez.

```bash
./gradlew test
```
