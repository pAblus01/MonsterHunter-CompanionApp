# MHW Companion

Base inicial del TFG en Android nativo con Kotlin.

## Incluye

- Single Activity con Fragments
- Navigation Component con grafo XML
- BottomNavigationView nativo
- Tres secciones base: Consulta, Sets y Camara
- Capa de red preparada con Retrofit y repositorio

## Siguiente paso recomendado

Conectar la pantalla de consulta a un ViewModel y a una lista RecyclerView cuando definas el contrato real de la API.

## Modelo YOLO en runtime

La app solo necesita en assets el modelo exportado como `app/src/main/assets/ml/my_model/my_model.tflite` y su archivo de etiquetas `app/src/main/assets/ml/my_model/labels.txt`.

Si la clase detectada corresponde al armor id `141`, `labels.txt` puede contener una sola línea con `141`.
