# Fill The Fridge — Roguelike Sorting & Shelf-Stacking Puzzle

A standalone Java 17+ and libGDX (LWJGL3 desktop backend) roguelike puzzle game inspired by *Fill The Fridge*. Pack groceries from shopping baskets into a procedurally rendered tile-based refrigerator before they spoil on the counter, manage weight and support physics, avoid downward liquid spills through absorbent foods, dispose of trap items in a slot-limited trash bin, and collect roguelike powerups between stages.

---

## 1. How to Run

### Prerequisites
- **Java 17+** (compatible with JDK 17 through JDK 25, including JetBrains Runtime bundled with IntelliJ IDEA)
- **Gradle 8.x / 9.x** (included via `./gradlew` and `gradlew.bat`)

### Run from IntelliJ IDEA
1. Open IntelliJ IDEA and select **File -> Open...**, then choose this project folder (`Final OOP`).
2. Allow IntelliJ IDEA to import the Gradle project (`build.gradle`).
3. Open the **Gradle** tool window on the right (`fill-the-fridge -> Tasks -> application -> run`) and double-click **`run`**, OR open [`src/main/java/com/fillthefridge/DesktopLauncher.java`](src/main/java/com/fillthefridge/DesktopLauncher.java) and click the green **Run** arrow next to `main`.

### Run from the Command Line (Gradle)
On Windows (PowerShell or Command Prompt):
```powershell
.\gradlew.bat run
```
On macOS / Linux:
```bash
./gradlew run
```

---

## 2. Controls

| Input | Action |
| :--- | :--- |
| **Left Click (Tray Item)** | Select or deselect an item in the outside shopping tray |
| **Hover (Fridge Grid)** | Display live 3-state placement preview (`LEGAL` green, `UNSTABLE` red, `SPILL_CAUSING` orange) and ghost sprite |
| **Left Click (Fridge Tile)** | Place the currently selected tray item at the hovered grid tile, or pick up an unconfirmed / trap / contaminated item from the fridge |
| **Right Click** | Undo the last unconfirmed fridge placement (returns the item to the tray and resumes its expiry timer from its remaining time) |
| **Space** | Confirm and permanently lock all current legal placements in the fridge |
| **Left Click (Trash Bin)** | Trash the currently selected trap item or contaminated item (consumes 1 trash slot for traps) |
| **F Key (or Freeze Button)** | Activate a **Deep Freeze** charge to pause the selected (or most urgent) tray item's timer for 3 seconds |
| **1 / 2 / 3 (or Left Click)** | Select a PowerUp card on the post-stage PowerUp Selection screen |
| **Mouse Wheel / Up & Down** | Scroll the Credits screen |
| **Escape** | Return to the Main Menu |

---

## 3. Text-Form Class Diagram

```text
+--------------------------------------------------------------------------------------------------+
|                                      com.fillthefridge.util                                      |
+--------------------------------------------------------------------------------------------------+
| <<interface>> Updatable          : + update(float deltaTime)                                     |
| <<interface>> Renderable         : + render(SpriteBatch, ShapeRenderer, BitmapFont)              |
| <<interface>> Placeable          : + getGridPosition(), + setGridPosition(GridPosition),         |
|                                    + getSize(), + isConfirmed(), + setConfirmed(boolean)         |
| <<interface>> Spoilable          : + getRemainingTime(), + getMaxShelfLife(), + updateTimer(),   |
|                                    + isWarningThresholdReached(), + isCriticalThresholdReached(),|
|                                    + isSpoiledAfterGrace(), + onTimerWarning()                   |
| <<interface>> Spillable          : + isSpillable(), + spillDownward(Fridge) : List<Item>         |
| <<interface>> StageGenerator     : + generateStage(StageConfig, ActivePowerUps) : GeneratedStage |
|                                    + verifySolvability(GeneratedStage, StageConfig) : boolean    |
| <<interface>> GameEventListener  : + onLivesChanged(...), + onStageCleared(...),                 |
|                                    + onItemSpoiled(...), + onSpillOccurred(...),                 |
|                                    + onWarningMessage(...)                                       |
| GridPosition (final, immutable)  : - col: int, - row: int                                        |
| GridSize (final, immutable)      : - width: int, - height: int                                   |
| UndoStack<T>                     : - stack: Deque<T>                                             |
| Registry<K, V>                   : - entries: Map<K, V>                                          |
| InvalidPlacementException        : extends Exception                                             |
| AssetLoadException               : extends RuntimeException                                      |
+--------------------------------------------------------------------------------------------------+
                                                 ^
                                                 | implements / uses
+--------------------------------------------------------------------------------------------------+
|                                     com.fillthefridge.model                                      |
+--------------------------------------------------------------------------------------------------+
| <<abstract>> GameObject          : implements Updatable, Renderable                              |
|   |-- Fridge                     : *-- List<Shelf>, *-- Tile[][] tiles, o-- List<Item>           |
|   |-- Tray                       : o-- List<Item> activeItems, o-- Deque<Item> pendingQueue      |
|   |-- TrashBin                   : o-- List<Item> trashedItems, - maxSlots: int                  |
|   +-- <<abstract>> Item          : (in com.fillthefridge.items)                                  |
|                                                                                                  |
| GameState                        : *-- Fridge, *-- Tray, *-- TrashBin, *-- PlayerLives,          |
|                                    *-- ActivePowerUps, *-- UndoStack<PlacementAction>,           |
|                                    o-- List<GameEventListener>                                   |
| Shelf                            : - rowIndex: int, - columnSpan: int, - baseShelf: boolean      |
| Tile                             : - position: GridPosition, - state: TileState,                 |
|                                    - occupyingItem: Item                                         |
| PlayerLives                      : - currentLives: int, - maxLives: int,                         |
|                                    o-- List<GameEventListener>                                   |
| ActivePowerUps                   : - coldSnapBonusSeconds: float, - deepFreezeCharges: int,      |
|                                    - ghostFitEnabled: boolean, - sniffTestEnabled: boolean,      |
|                                    - extraTrashSlots: int, - dripTrayCharges: int                |
| StageConfig (final, immutable)   : stage scaling parameters & seed                               |
| PlacementAction (final, immut.)  : - item: Item, - position: GridPosition                        |
| <<enum>> ItemCategory            : VEGETABLE, MEAT, DAIRY, MILK, BAKERY, PANTRY, LEFTOVER, TRAP  |
| <<enum>> TileState               : EMPTY, OCCUPIED_UNCONFIRMED, OCCUPIED_CONFIRMED,              |
|                                    CONTAMINATED, TRAP_OCCUPIED                                   |
| <<enum>> PlacementResult         : LEGAL, UNSTABLE, SPILL_CAUSING                                |
| <<enum>> GamePhase               : MAIN_MENU, PLAYING, STAGE_CLEAR, POWERUP_SELECTION,           |
|                                    GAME_OVER, CREDITS                                            |
+--------------------------------------------------------------------------------------------------+
                                                 ^
                                                 | extends / implements
+--------------------------------------------------------------------------------------------------+
|                                     com.fillthefridge.items                                      |
+--------------------------------------------------------------------------------------------------+
| <<abstract>> Item extends GameObject implements Placeable, Spoilable                             |
|   + {abstract} onTimerWarning() : String                                                         |
|   + {abstract} canSupport(Item aboveItem) : boolean                                              |
|   + {abstract} onStackedUpon(Item aboveItem, Fridge fridge) : PlacementResult                    |
|   + {abstract} createFreshCopy() : Item                                                          |
|   |                                                                                              |
|   |-- Vegetable                  : lettuce (absorbent), cabbage (absorbent), carrot, tomato,     |
|   |                                broccoli, apple                                               |
|   |-- Meat                       : whole_chicken, steak, fish, sausage                           |
|   |-- Dairy                      : yogurt_cup, cheese, butter, egg_carton                        |
|   |-- Milk                       : implements Spillable (milk)                                   |
|   |-- BakeryItem                 : implements Spillable (bread [absorbent], pizza_box [holds L], |
|   |                                jam_jar, juice_bottle [spillable], soda_can)                  |
|   |-- Leftover                   : implements Spillable (leftovers_container [spillable],        |
|   |                                soup_container [spillable], takeout_box)                      |
|   +-- TrapItem                   : empty_milk_carton, empty_can, banana_peel, plastic_bag        |
|                                                                                                  |
| ItemCatalog                      : factory & template registry for all 27 catalog items          |
+--------------------------------------------------------------------------------------------------+
                                                 ^
                                                 |
+--------------------------------------------------------------------------------------------------+
|                                    com.fillthefridge.powerups                                    |
+--------------------------------------------------------------------------------------------------+
| <<abstract>> PowerUp             : + {abstract} apply(GameState gameState)                       |
|   |-- ColdSnapPowerUp            : +2s to all tray timers                                        |
|   |-- DeepFreezePowerUp          : Pause one tray item for 3s                                    |
|   |-- GhostFitPowerUp            : Exact placement preview and support highlights                |
|   |-- SniffTestPowerUp           : Reveal trap items immediately                                 |
|   |-- TrashCompactorPowerUp      : +2 trash bin slots                                            |
|   |-- DripTrayPowerUp            : Negate the next spill                                         |
|   +-- SecondWindPowerUp          : Heal 1 life (rare)                                            |
| PowerUpPool                      : Seeded generator offering 1 of 3 random powerups per stage    |
+--------------------------------------------------------------------------------------------------+
                                                 ^
                                                 |
+--------------------------------------------------------------------------------------------------+
|                        com.fillthefridge.logic / assets / ui / root                              |
+--------------------------------------------------------------------------------------------------+
| <<interface>> DifficultyScalingStrategy : + createStageConfig(int stage, long seed)              |
|   +-- ProgressiveDifficultyStrategy     : Priority-ordered stage difficulty scaling              |
| SolvableStageGenerator                  : implements StageGenerator (bottom-up gravity tiling)   |
| GeneratedStage                          : holds verified solutionPlacements + stageItems         |
| GameController                          : coordinates placement, spills, undo, lock, powerups    |
| AssetManager (assets)                   : centralized Registry<String, Texture> + credits reader |
| ProceduralFridgeRenderer (ui)           : procedural cabinet, shelves, door, light gradient      |
| HudRenderer (ui)                        : top HUD, lives, baskets, timer bars, stage-clear toast |
| BaseScreen (ui)                         : abstract base screen with smooth fade transitions      |
|   |-- MainMenuScreen                    |-- PowerUpSelectionScreen      |-- CreditsScreen        |
|   |-- GameplayScreen (GameEventListener)|-- GameOverScreen                                       |
| FillTheFridgeGame & DesktopLauncher     : LWJGL3 application entry point                         |
+--------------------------------------------------------------------------------------------------+
```

---

## 4. OOP Pillars & Design Patterns Mapping Table

| OOP Pillar / Requirement | Exact Classes & Interfaces Demonstrating It | How It Is Demonstrated |
| :--- | :--- | :--- |
| **Abstraction** | [`GameObject`](src/main/java/com/fillthefridge/model/GameObject.java), [`Item`](src/main/java/com/fillthefridge/items/Item.java), [`PowerUp`](src/main/java/com/fillthefridge/powerups/PowerUp.java), [`BaseScreen`](src/main/java/com/fillthefridge/ui/BaseScreen.java) | Abstract base classes define shared state and contracts (`update`, `render`, `onTimerWarning`, `canSupport`, `onStackedUpon`, `apply`) while deferring concrete behavior to subclasses. |
| **Inheritance** | [`Vegetable`](src/main/java/com/fillthefridge/items/Vegetable.java), [`Meat`](src/main/java/com/fillthefridge/items/Meat.java), [`Dairy`](src/main/java/com/fillthefridge/items/Dairy.java), [`Milk`](src/main/java/com/fillthefridge/items/Milk.java), [`BakeryItem`](src/main/java/com/fillthefridge/items/BakeryItem.java), [`Leftover`](src/main/java/com/fillthefridge/items/Leftover.java), [`TrapItem`](src/main/java/com/fillthefridge/items/TrapItem.java) extend [`Item`](src/main/java/com/fillthefridge/items/Item.java); [`ColdSnapPowerUp`](src/main/java/com/fillthefridge/powerups/ColdSnapPowerUp.java), [`DeepFreezePowerUp`](src/main/java/com/fillthefridge/powerups/DeepFreezePowerUp.java), [`GhostFitPowerUp`](src/main/java/com/fillthefridge/powerups/GhostFitPowerUp.java), [`SniffTestPowerUp`](src/main/java/com/fillthefridge/powerups/SniffTestPowerUp.java), [`TrashCompactorPowerUp`](src/main/java/com/fillthefridge/powerups/TrashCompactorPowerUp.java), [`DripTrayPowerUp`](src/main/java/com/fillthefridge/powerups/DripTrayPowerUp.java), [`SecondWindPowerUp`](src/main/java/com/fillthefridge/powerups/SecondWindPowerUp.java) extend [`PowerUp`](src/main/java/com/fillthefridge/powerups/PowerUp.java) | Specialized grocery categories and powerup types inherit common spatial, timer, and rendering state while customizing domain rules. |
| **Polymorphism** | [`Item.onTimerWarning()`](src/main/java/com/fillthefridge/items/Item.java), [`Item.canSupport(Item)`](src/main/java/com/fillthefridge/items/Item.java), [`Item.onStackedUpon(Item, Fridge)`](src/main/java/com/fillthefridge/items/Item.java), [`PowerUp.apply(GameState)`](src/main/java/com/fillthefridge/powerups/PowerUp.java) | `Fridge.evaluatePlacement` invokes `belowItem.onStackedUpon(item, this)` and `belowItem.canSupport(item)` through the `Item` base type; `GameController` invokes `item.onTimerWarning()` and `chosenPowerUp.apply(gameState)` polymorphically. |
| **Encapsulation** | [`GridPosition`](src/main/java/com/fillthefridge/util/GridPosition.java), [`GridSize`](src/main/java/com/fillthefridge/util/GridSize.java), [`StageConfig`](src/main/java/com/fillthefridge/model/StageConfig.java), [`PlacementAction`](src/main/java/com/fillthefridge/model/PlacementAction.java), [`GeneratedStage`](src/main/java/com/fillthefridge/logic/GeneratedStage.java), [`PlayerLives`](src/main/java/com/fillthefridge/model/PlayerLives.java) | All fields across all classes are `private`; value objects (`GridPosition`, `GridSize`, `StageConfig`, `PlacementAction`) are `final` and immutable; collections are exposed via `Collections.unmodifiableList(...)` / `List.copyOf(...)`. |
| **Interfaces** | [`Updatable`](src/main/java/com/fillthefridge/util/Updatable.java), [`Renderable`](src/main/java/com/fillthefridge/util/Renderable.java), [`Placeable`](src/main/java/com/fillthefridge/util/Placeable.java), [`Spoilable`](src/main/java/com/fillthefridge/util/Spoilable.java), [`Spillable`](src/main/java/com/fillthefridge/util/Spillable.java), [`StageGenerator`](src/main/java/com/fillthefridge/util/StageGenerator.java), [`DifficultyScalingStrategy`](src/main/java/com/fillthefridge/logic/DifficultyScalingStrategy.java), [`GameEventListener`](src/main/java/com/fillthefridge/util/GameEventListener.java) | Capability interfaces decouple update/render loops, grid placement, spoilage timers, downward spill propagation, stage generation, difficulty scaling, and UI event notifications. |
| **Composition** | [`Fridge`](src/main/java/com/fillthefridge/model/Fridge.java) (`List<Shelf>` + `Tile[][]`), [`GameState`](src/main/java/com/fillthefridge/model/GameState.java) (`Fridge`, `Tray`, `TrashBin`, `PlayerLives`, `ActivePowerUps`, `UndoStack<PlacementAction>`) | Complex domain aggregates are composed of focused collaborating objects rather than monolithic classes. |
| **Enums** | [`ItemCategory`](src/main/java/com/fillthefridge/model/ItemCategory.java), [`TileState`](src/main/java/com/fillthefridge/model/TileState.java), [`PlacementResult`](src/main/java/com/fillthefridge/model/PlacementResult.java), [`GamePhase`](src/main/java/com/fillthefridge/model/GamePhase.java) | Type-safe enumerations represent item categories, tile occupancy states, 3-state placement previews, and game flow phases. |
| **Custom Exceptions** | [`InvalidPlacementException`](src/main/java/com/fillthefridge/util/InvalidPlacementException.java), [`AssetLoadException`](src/main/java/com/fillthefridge/util/AssetLoadException.java) | Checked `InvalidPlacementException` handles illegal fridge placements and invalid trash bin operations; `AssetLoadException` handles missing/corrupt resource files. |
| **Collections & Generics** | [`UndoStack<T>`](src/main/java/com/fillthefridge/util/UndoStack.java), [`Registry<K, V>`](src/main/java/com/fillthefridge/util/Registry.java), `Deque<Item>`, `Set<Item>`, `List<PowerUp>` | Custom generic classes `UndoStack<T>` and `Registry<K, V>` and standard generic collections manage textures, undo history, and tray queues. |
| **Strategy Pattern** | [`DifficultyScalingStrategy`](src/main/java/com/fillthefridge/logic/DifficultyScalingStrategy.java), [`ProgressiveDifficultyStrategy`](src/main/java/com/fillthefridge/logic/ProgressiveDifficultyStrategy.java) | `GameController` delegates per-stage parameter calculation to an interchangeable `DifficultyScalingStrategy`. |
| **Observer Pattern** | [`GameEventListener`](src/main/java/com/fillthefridge/util/GameEventListener.java), [`PlayerLives`](src/main/java/com/fillthefridge/model/PlayerLives.java), [`GameState`](src/main/java/com/fillthefridge/model/GameState.java), [`GameplayScreen`](src/main/java/com/fillthefridge/ui/GameplayScreen.java) | `PlayerLives` and `GameState` broadcast life changes, spoilage, spills, warnings, and stage clears to registered `GameEventListener` observers (`GameplayScreen`). |

---

## 5. Asset Inspection & Missing Assets List

### Inspected Files in `assets/items/`
All 38 image files discovered in `assets/items/` were inspected and indexed by [`AssetManager`](src/main/java/com/fillthefridge/assets/AssetManager.java):
- **Catalog Grocery Items (23/23 present)**: `whole_chicken.png`, `steak.png`, `fish.png`, `sausage.png`, `milk.png`, `yogurt_cup.png`, `cheese.png`, `butter.png`, `egg_carton.png`, `lettuce.png`, `carrot.png`, `tomato.png`, `broccoli.png`, `cabbage.png`, `apple.png`, `bread.png`, `pizza_box.png`, `jam_jar.png`, `juice_bottle.png`, `soda_can.png`, `leftovers_container.png`, `soup_container.png`, `takeout_box.png`
- **Catalog Trap Items (4/4 present)**: `empty_milk_carton.png`, `empty_can.png`, `banana_peel.png`, `plastic_bag.png`
- **Catalog Interface Icons (4/4 present)**: `trash_bin.png`, `heart.png`, `warning_icon.png`, `snowflake.png`
- **Ignored File**: `fridge_bg.png` (explicitly ignored per specification; the fridge is drawn 100% procedurally by [`ProceduralFridgeRenderer`](src/main/java/com/fillthefridge/ui/ProceduralFridgeRenderer.java))
- **Additional Variant Files Present**: `cheese.jpg`, `leftovers.jpg`, `rawchicken.jpg`, `rawmeat.jpg`, `Raw_Fish_icon.jpg`, `yogurt.jpg`

### Missing Assets
- **None** (`0` missing assets). Every expected catalog filename in `assets/items/` is present.
- If any image file is ever removed from `assets/items/`, [`AssetManager`](src/main/java/com/fillthefridge/assets/AssetManager.java) first attempts name-similarity matching against remaining files and otherwise generates a clean colored rectangle placeholder with the item's display name rendered on top.

---

## 6. Credits (`assets/credits.txt`)

The Credits screen reads [`assets/credits.txt`](assets/credits.txt) dynamically at runtime:

```text
============================================================
               FILL THE FRIDGE - CREDITS
============================================================

Game Design & OOP Implementation:
- Built with Java 17+ and libGDX (LWJGL3 Desktop Backend)
- Procedural Fridge Renderer & Solvable Stage Generator

Icon Attributions (Flaticon - https://www.flaticon.com):
- whole_chicken.png : Icon made by [Author Name] from www.flaticon.com
- steak.png : Icon made by [Author Name] from www.flaticon.com
- fish.png : Icon made by [Author Name] from www.flaticon.com
- sausage.png : Icon made by [Author Name] from www.flaticon.com
- milk.png : Icon made by [Author Name] from www.flaticon.com
- yogurt_cup.png : Icon made by [Author Name] from www.flaticon.com
- cheese.png : Icon made by [Author Name] from www.flaticon.com
- butter.png : Icon made by [Author Name] from www.flaticon.com
- egg_carton.png : Icon made by [Author Name] from www.flaticon.com
- lettuce.png : Icon made by [Author Name] from www.flaticon.com
- carrot.png : Icon made by [Author Name] from www.flaticon.com
- tomato.png : Icon made by [Author Name] from www.flaticon.com
- broccoli.png : Icon made by [Author Name] from www.flaticon.com
- cabbage.png : Icon made by [Author Name] from www.flaticon.com
- apple.png : Icon made by [Author Name] from www.flaticon.com
- bread.png : Icon made by [Author Name] from www.flaticon.com
- pizza_box.png : Icon made by [Author Name] from www.flaticon.com
- jam_jar.png : Icon made by [Author Name] from www.flaticon.com
- juice_bottle.png : Icon made by [Author Name] from www.flaticon.com
- soda_can.png : Icon made by [Author Name] from www.flaticon.com
- leftovers_container.png : Icon made by [Author Name] from www.flaticon.com
- soup_container.png : Icon made by [Author Name] from www.flaticon.com
- takeout_box.png : Icon made by [Author Name] from www.flaticon.com
- empty_milk_carton.png : Icon made by [Author Name] from www.flaticon.com
- empty_can.png : Icon made by [Author Name] from www.flaticon.com
- banana_peel.png : Icon made by [Author Name] from www.flaticon.com
- plastic_bag.png : Icon made by [Author Name] from www.flaticon.com
- trash_bin.png : Icon made by [Author Name] from www.flaticon.com
- heart.png : Icon made by [Author Name] from www.flaticon.com
- warning_icon.png : Icon made by [Author Name] from www.flaticon.com
- snowflake.png : Icon made by [Author Name] from www.flaticon.com
```

---

## 7. Assumptions & Design Decisions

1. **Asset Organization**: The provided images were found in the project root and copied into `assets/items/` (and `assets/`) so that standard `assets/items/` lookup works out of the box.
2. **Tray Capacity & Cart Queue**: Because short-life items like raw meat/fish have a 4-6s shelf life while a stage has 8-18 total items, the `Tray` displays up to `trayCapacity` (3-4) active items at a time, and remaining items wait in the Cart Queue until a slot opens in the tray. Timers tick only for items currently in the active tray.
3. **Solvability Preservation on Spoilage or Contamination**: Winning a stage requires every fridge tile to be occupied by a legal non-trap item. Therefore, when a required non-trap item spoils outside in the tray (costing 1 life after the 0.5s grace period) or is trashed after being contaminated by a spill (which also costs 1 life per item hit), a fresh replacement of that required grocery item is added to the tray so the stage remains 100% solvable if the player still has lives left.
4. **Protecting Fresh Groceries from Accidental Trashing**: Only trap items and contaminated (spilled-on) items can be placed in the Trash Bin. Attempting to trash fresh uncontaminated groceries raises an `InvalidPlacementException` with a helpful HUD prompt so players cannot accidentally soft-lock a stage or waste limited trash slots.
5. **Picking Up Traps & Contaminated Items from the Fridge**: Left-clicking a trap item, contaminated item, or unconfirmed item inside the fridge (when no tray item is selected) picks it up so the player can immediately click the Trash Bin to dispose of it or reposition it. Any unconfirmed items resting directly above a removed item automatically return to the tray so nothing floats in mid-air.
6. **Spillable Items at Row 0**: Stacking on top of a spillable item (`milk`, `juice_bottle`, `soup_container`, `leftovers_container`) spills downward onto the item directly below it and chains through absorbent items (`bread`, `lettuce`, `cabbage`). If the spillable item is on the bottom shelf (`row = 0`) with no item below it when stacked upon, it spills across its own compartment, contaminating itself and costing 1 life.
