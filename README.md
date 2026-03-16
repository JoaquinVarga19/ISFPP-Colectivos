Sistema De Colectivos
================================================

Este sistema permite al usuario ingresar parada en la que sube (origen) y en la que baja (destino), el dia de la semana (Lunes-Domingo/Feriado) en el dia que se va a tomar el colectivo y en que horario. Esta aplicacion calcula las rutas mediante tres tipos de recorridos: Dijkstra que se utiliza para calcular la ruta mas corta con transbordos, Directo que es una sola lina y Caminando y muestra el recorrido mediante un mapa en una interfaz de usuario amigable.

El sistema es flexible respecto de la persistencia, ya que el usuario puede utilizar el archivo de texto con la informacion de linas, paradas, tramos y frecuencias tanto por archivos de texto o bases de datos (Preferiblemente PostGreSQL).

Modelo de la aplicacion: 
================================================
Por interfaz grafica:
-------------------------------

<img width="1364" height="696" alt="Captura de pantalla 2026-03-14 154623" src="https://github.com/user-attachments/assets/0d7f33dc-fa09-4b11-a9e5-3820ee8135bd" />

Por consola:
-------------------------------

<img width="571" height="356" alt="Captura de pantalla 2026-03-16 134540" src="https://github.com/user-attachments/assets/1c7cf846-4138-4395-acb5-cef88e926f6a" />  
<img width="466" height="259" alt="Captura de pantalla 2026-03-16 134611" src="https://github.com/user-attachments/assets/b7c3e5c2-4a5a-4c13-a944-5dcedd969fd9" />

Funcionalidades basicas:
================================================
Que es lo que hace el sistema:
-------------------------------

