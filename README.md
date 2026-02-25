# Java Prototypes
A workspace for me to work on small prototypes and test Java features.
The project is organised with a Core-prototypes module architecture: there is a Core module, acting as a library for every prototype.
This way, each of them is an application, with plenty of features such as window and event handling, or even a util to easily compute launch arguments.
For small Java tests, there is the default `src/main/java` package in which I, for instance, tested basic multithreading before implementing it in the "GPS" module (`Shortest-Path-Algorithms`).

There are 3 prototypes for now:
- an RPG compass bar hud: see an RPG game? see the compass bar on top of the screen? That's it. I wanted to know the maths behind it and done my small prototype with a visual debug.
- shortest path algorithms: a basic shortest path algorithms (Dijkstra, A*) playground that became a fully functional and very efficiently multithreaded GPS library. Interact with it using the `GpsService.java` class, where you can submit a pathfinding process and configure parameters such as the thread count to be used by the executors.  
- a 3D renderer: for now only a cube renderer, and forever only rendering the edges and not filling between the vertices. A simple renderer to understand the maths between graphics programming.

**Note:** The 3D Renderer prototype is not working for now (broken since the refactor adding the Core/prototypes module architecture).

Technical details about each prototype in the [Technical details](#technical-details) section below.

## How to run the prototypes and how to use them
Just open the project in Jetbrains Intellij IDEA and launch the entrypoint corresponding to the prototype you want to launch. You can also use the following command for any of the module and launch the jar file:
```bash
./gradlew jar
```

About jar files: note that some prototypes may have launch arguments, thus needing to launch from a terminal. Launch arguments are prefixed with a hyphen ("-") and their list, for each prototype, can be found in the prototype main file.

You can also use the launch arguments inside your IDE without any problem, but for some crazy tests you may want to close your heavy-on-memory IDE to let every bit of memory for the program launched with its jar file.

- ### RpgCompassBarHud
  - Launch `CompassApp.java` in `src/main/java/fr/seeden/compass`.
  - Usage: 
    - left click to place the player ; right click to place the goal (like the quest point in an RPG)
    - move the cursor while pressing LEFT SHIFT to make the 2D player look around
    - the black line is the player direction vector (also represented as normalised in the middle of the screen as a pink line, while the dark blue line is the player-to-goal vector normalised)
    - the red lines are the player's FOV. If the black line is not between them two, the player doesn't have the goal in its sight.
    - the light blue and green lights are visual debugs to switch the goal position on the compass hud, from left to right or from right to left, when the player is not facing the goal directly. When one of those two lines crosses a red line (the fov) the goal switches between left/right bound of the compass hud.
- ### Shortest-Path-Algorithms
  - Launch `GpsTestApp.java` in `src/test/java/fr/seeden/gps`.
  - Launch arguments: find them inside the `GpsTestApp.java` file. Note that depending on the graph size, you may want to run your tests with a jar file and not with your IDE opened.
  - Note: if you want to build the jar with the tests, you will have to copy the two files in the `test` package into the `main` package, alongside the `GpsService.java` file. Otherwise, you will build the prototype as a library to use it in your projects.
  - Usage:
    - if you are using no launch arguments, it will launch the default tests in the console. It will run 4 different test groups each with a different graph and test configuration. Each group will run A* star in the 4 configurations it could handle.
    - if you are using the `-useDebugWindow` launch argument, it will launch the app window. A graph will be generated depending on the other launch arguments (or if missing, default values) and you will also have visual debugs (you can toggle them with launch arguments too).
      - left click once to set the start point, and left click another time to set the goal point. A third click will reset both and set a new start point.
      - middle click resets the process, but not regenerates the graph, while pressing ENTER will do both.
      - right click launches the process.
      - SPACE changes the algorithm between Dijkstra and A*.
- ### Cube-Renderer
  - **[MODULE NOT WORKING]** Launch `RenderAppMain.java` in `src/main/java/fr/seeden/renderer`.
  - Usage: WIP

**Note**: you can find the small test inside the `src/main/java/fr/seeden/javatest` package, outside any module, and launch them individually.

## Technical details
(and some stories)

### Core module
It features:
- an Application+Window abstraction:
  - no longer needed to write the JFrame and JPanel, it is handled by creating an Application that can be windowless. An Application provides an abstraction method `tick()` called for each app loop. And you can also limit the update rate (`setFpsLimit()`)
  - You can add multiple windows to an application, it will handle the JFrame/JPanel inside it and provide abstraction methods such as `update()` (called each app tick) and `render()` when Swing repaints the window (abstraction of `paintComponent()`).
  - You can register a class as an EventListener using the custom EventBus that dispatches Swing events abroad the Application listeners.
  - Each window can also register custom keybindings and callbacks for them.
  - Each app also has a logger (but it is not very convenient to use it, so I may change its accessibility)
- a `math` util package mostly for space related maths.
- a powerful launch arguments computing util
  - create and define every argument the application can use: their type and default values
    - if missing = not found in the given launch args String[]
    - if not set = the arg key is in the given launch args String[] but no value set with "key=value". Ex: -enableSomething vs -enableSomething=true/false. What to do if no value? Defaulted to true or false? Can also work with non-boolean values.
  - the util will compute the given launch args String[] according to the application launch arguments list and set a value for every launch arguments of the application list (if set=takes the value ; if missing or not set=the default value) 
  - then you can easily retrieve the value of each argument 

### RpgCompassBarHud
It is just a very simple prototype. It is the first I refactored using the Core module due to its simplicity, but still needed many features from the Core module I had then been able to test.

And about the maths, again just basic vector maths and logic.

### Shortest-Path-Algorithms
**Story part:**

Since many years I always wanted to do a complex GPS into Minecraft, and after many attempts of basic Dijkstra algorithm in the console, I decided to go further and create a visual debug for the graph.
This graph visualisation would simulate a player opening their map and clicking wherever they want to go. 

With the prototype going on, I started to implement the A* algorithm, then witnessing everything was slow I researched for Java multithreading and A* optimisations including experiment on different data structures. 
I ended up with a very efficient multithreaded GPS library that can handle hundreds of parallel requests to find the shortest path between two points.
There are two types of tests:
- the console one, just running some tests on random generated graphs.
- the window one, where you can with the launch arguments set the generated graph parameters or toggle visual debug options. And of course you can select in the window itself the start and goal points for the GPS.

**Technical part:**

WIP (notes: A* optimisations, data structures experiments with time profiling, multithreading using two executors: one to compute the path the other to supply the result callback)

### Cube-Renderer
**Story part:**

The idea came when I first wondered back in March 2025, while watching a Unity tutorial video: "But how the engine makes lights? And shadows? And just... how everything is shown on screen?".
This is when I got into engine programming and particularly graphics programming. And one of my first questions was: what are the maths done by the maths library with the matrices, and the maths done the graphics API e.g. during rasterisation.
Thus, I opened my IDE and started this prototype, trying to do everything myself (well, all the maths to compute the screen coordinates because I still use Java Swing for rendering).

**Technical part:**

Still WIP (broken since I rewrote the entire project as a module architecture with the Core-prototypes system).

## License
Distributed under the Apache License 2.0. See `LICENSE` for more information.