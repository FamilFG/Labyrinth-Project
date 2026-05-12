# Labyrinth

Labyrinth is a Java maze adventure game. The player explores a labyrinth,
opens chests and doors, fights enemies, collects items, and reaches the exit to
move to the next level.

This repository contains the full source code and a Geany project file, so the
project can be opened, compiled, and run directly from Geany.

## Features

- Graphical Java Swing version
- Console version
- Three map-based levels
- Player movement and turn-based enemy movement
- Chests, doors, keys, tools, combat, and inventory
- Text-file maps that can be edited or expanded

## Requirements

- Java JDK 8 or newer
- Geany

Check that Java is installed:

```powershell
javac -version
java -version
```

If these commands are not recognized, install a JDK and add Java to your system
`PATH`.

## Installation

1. Download or clone this project.
2. Keep the folder structure unchanged.
3. Open the project folder in Geany using the included `Labyrinth.geany` file.

No external libraries are required. The project uses only standard Java classes.

## Project Structure

```text
Labyrinth-Project/
|-- README.md
|-- Labyrinth.geany
|-- labyrinth/
    |-- maps/
    |   |-- map1.txt
    |   |-- map2.txt
    |   `-- map3.txt
    `-- src/
        `-- game/
            |-- main/
            |   |-- GuiMain.java
            |   `-- Main.java
            |-- ui/
            |-- utils/
            |-- world/
            |-- map/
            |-- living/
            |-- objects/
            `-- interfaces/
```

Important files:

- `labyrinth/src/game/main/GuiMain.java` starts the graphical version.
- `labyrinth/src/game/main/Main.java` starts the console version.
- `labyrinth/maps/` contains the level map files.
- `Labyrinth.geany` contains the Geany project and build settings.

## Running The Project In Geany

1. Open Geany.
2. Go to `Project > Open`.
3. Select `Labyrinth.geany`.
4. Open any Java file, such as:

   ```text
   labyrinth/src/game/main/GuiMain.java
   ```

5. Use the build menu:

   - `Compile Project` compiles all Java files into the `out` folder.
   - `Run GUI` starts the graphical version of the game.

If the project was moved to another folder, Geany may still use old absolute
paths. In that case, update the project build commands so the working directory
points to your current `Labyrinth-Project` folder.

## Manual Build And Run

These commands can be used from PowerShell in the project root folder.

Compile:

```powershell
New-Item -ItemType Directory -Force out
javac -d out (Get-ChildItem -Path labyrinth\src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
```

Run the graphical version:

```powershell
java -cp out game.main.GuiMain
```

Run the console version:

```powershell
java -cp out game.main.Main
```

## Controls

Graphical version:

- `W`, `A`, `S`, `D` or arrow keys: move
- `E`: open a nearby chest or door
- `I`: show inventory

Console version:

- `w`, `a`, `s`, `d`: move
- `open`: open a nearby chest or door
- `inv`: show inventory
- `q`: quit

## Map Symbols

- `P`: player
- `#`: wall
- `.`: floor
- `C`: chest
- `D`: closed door
- `O`: open door
- `E`: enemy
- `X`: exit

## Editing Or Adding Maps

Maps are stored as text files in `labyrinth/maps/`.

The current levels are loaded in this order:

1. `map1.txt`
2. `map2.txt`
3. `map3.txt`

To change a level, edit the matching map file. To add a new level, create a new
map file and update the map list in:

```text
labyrinth/src/game/utils/LevelManager.java
```

Each map should contain one player start position `P` and one exit `X`.

## Gameplay Notes

The game starts on `map1.txt`. When the player reaches the exit, the next map is
loaded automatically. After the final map is completed, the player wins.

Enemies move after the player takes a turn. Chests may contain useful items, and
some doors may require an item before they can be opened.

## Troubleshooting

If maps do not load, make sure the game is being run from the project root and
that this folder exists:

```text
labyrinth/maps/
```

If Geany cannot compile the project, check that:

- The JDK is installed.
- `javac` works from the terminal.
- The Geany build working directory points to this project folder.
- The source files are still inside `labyrinth/src/`.

If the GUI does not open, try compiling again and then running:

```powershell
java -cp out game.main.GuiMain
```

## Possible Future Improvements

- Add more maps and difficulty levels
- Add save and load support
- Add sound effects
- Add more item types
- Add a score or timer system
- Improve enemy behavior
