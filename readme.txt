Descripción

Este proyecto es una aplicación de ajedrez en Java que ofrece una interfaz gráfica de usuario (GUI) interactiva para jugar al ajedrez. La aplicación incluye funcionalidades como selección y movimiento de piezas, detección de jaque y jaque mate, enroque, promoción de peones, y la capacidad de resolver problemas de mate predefinidos. Además, cuenta con una inteligencia artificial básica para movimientos automáticos de las piezas negras.


Características
	•	Interfaz Gráfica Intuitiva: Tablero de ajedrez visualmente atractivo con piezas representadas por íconos.
	•	Movimiento de Piezas: Permite seleccionar y mover piezas blancas manualmente.
	•	Inteligencia Artificial Básica: Las piezas negras realizan movimientos automáticos.
	•	Detección de Jueces y Jaques Mate: El programa identifica situaciones de jaque, jaque mate y tablas.
	•	Problemas de Mate Predefinidos: Resuelve problemas de mate con un número limitado de movimientos.
	•	Enroque y Promoción de Peones: Soporta movimientos especiales del ajedrez.
	•	Guardar y Cargar Partidas: Permite guardar el estado actual de la partida y cargarlo posteriormente.


Requisitos
	•	Java Development Kit (JDK) 8 o superior
	•	Sistema Operativo: Windows, macOS o Linux
	•	Recursos de Imagen: Asegúrate de tener las imágenes de las piezas de ajedrez en la carpeta assets/images/.


Compilación

Para compilar todos los archivos Java del proyecto, sigue estos pasos:
	1.	Abrir la Terminal
Abre la terminal o línea de comandos en tu sistema operativo.
	2.	Navegar al Directorio del Proyecto
Si no estás ya en el directorio del proyecto, navega hasta la carpeta src del proyecto:

	diego@atlitec Desktop % cd ChessGame/src
	diego@atlitec src % ls
	assets		chess		readme.txt	savegame.txt


Compilar los Archivos Java
Ejecuta el siguiente comando para compilar todos los archivos .java dentro del paquete chess:

	javac chess/*.java


Ejecución

Para ejecutar la aplicación, utiliza el siguiente comando:

	java chess.ChessMainMenu


Uso
	1.	Iniciar la Aplicación
		•	Ejecuta el comando de ejecución mencionado anteriormente para iniciar la aplicación. Se abrirá la interfaz gráfica del menú principal.
	2.	Seleccionar un Modo de Juego
		•	Partida contra la IA (Negras Automáticas): Juega contra la inteligencia artificial que controla las piezas negras.
		•	Resolución de Problemas de Mate: Selecciona un problema de mate predefinido para resolverlo en un número limitado de movimientos.
		•	Guardar/Cargar Partidas: Guarda el estado actual de la partida para continuar más tarde o carga una partida previamente guardada.
	3.	Mover Piezas
		•	Seleccionar una Pieza: Haz clic en la pieza blanca que deseas mover.
		•	Seleccionar Destino: Haz clic en la casilla de destino para mover la pieza.
		•	IA Realiza su Movimiento: Después de tu movimiento, las piezas negras realizarán automáticamente su movimiento.
	4.	Detección de Jueces y Jaque Mate
		•	El programa te notificará cuando tu rey esté en jaque, en jaque mate o si la partida termina en tablas.
	5.	Reiniciar o Salir
		•	Reiniciar Problema: Reinicia el problema actual para intentarlo nuevamente.
		•	Volver al Menú Principal: Regresa al menú principal para seleccionar otro modo de juego.
		•	Salir: Cierra la aplicación.

GRACIAS.