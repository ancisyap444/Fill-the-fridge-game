package com.fillthefridge.items;

import com.fillthefridge.assets.AssetManager;
import com.fillthefridge.model.ItemCategory;
import com.fillthefridge.util.GridSize;
import java.util.ArrayList;
import java.util.List;

public final class ItemCatalog {
    private static final List<Item> GROCERY_TEMPLATES = createGroceryTemplates();
    private static final List<TrapItem> TRAP_TEMPLATES = createTrapTemplates();

    private ItemCatalog() {
    }

    private static List<Item> createGroceryTemplates() {
        List<Item> list = new ArrayList<>();

        list.add(new Meat(
            "whole_chicken", "Whole Chicken", AssetManager.WHOLE_CHICKEN,
            new GridSize(2, 2), 5.8f, 3, 3,
            "The whole chicken is starting to smell!"
        ));
        list.add(new Meat(
            "steak", "Prime Steak", AssetManager.STEAK,
            new GridSize(2, 1), 5.2f, 2, 3,
            "The raw steak is getting warm!"
        ));
        list.add(new Meat(
            "fish", "Fresh Fish", AssetManager.FISH,
            new GridSize(2, 1), 4.8f, 2, 2,
            "The fish is starting to reek!"
        ));
        list.add(new Meat(
            "sausage", "Sausage Link", AssetManager.SAUSAGE,
            new GridSize(1, 1), 5.6f, 1, 2,
            "The sausage is sweating on the counter!"
        ));

        list.add(new Milk(
            "milk", "Milk Carton", AssetManager.MILK,
            new GridSize(2, 1), 8.0f, 2,
            "The milk is turning sour outside!"
        ));

        list.add(new Dairy(
            "yogurt_cup", "Yogurt Cup", AssetManager.YOGURT_CUP,
            new GridSize(1, 1), 10.0f, 1, 1,
            "The yogurt cup is getting lukewarm!"
        ));
        list.add(new Dairy(
            "cheese", "Cheddar Block", AssetManager.CHEESE,
            new GridSize(1, 1), 12.0f, 2, 2,
            "The cheese is starting to sweat!"
        ));
        list.add(new Dairy(
            "butter", "Butter Stick", AssetManager.BUTTER,
            new GridSize(1, 1), 11.8f, 1, 2,
            "The butter is softening fast!"
        ));
        list.add(new Dairy(
            "egg_carton", "Egg Carton", AssetManager.EGG_CARTON,
            new GridSize(1, 2), 14.0f, 1, 1,
            "The eggs need refrigeration soon!"
        ));

        list.add(new Vegetable(
            "lettuce", "Crisp Lettuce", AssetManager.LETTUCE,
            new GridSize(1, 1), 6.5f, 1, 1, true,
            "The lettuce leaves are wilting!"
        ));
        list.add(new Vegetable(
            "carrot", "Crunchy Carrot", AssetManager.CARROT,
            new GridSize(1, 1), 10.5f, 1, 2, false,
            "The carrot is losing its crunch!"
        ));
        list.add(new Vegetable(
            "tomato", "Ripe Tomato", AssetManager.TOMATO,
            new GridSize(1, 1), 8.5f, 1, 1, false,
            "The tomato is getting mushy!"
        ));
        list.add(new Vegetable(
            "broccoli", "Green Broccoli", AssetManager.BROCCOLI,
            new GridSize(1, 1), 6.6f, 1, 2, false,
            "The broccoli florets are yellowing!"
        ));
        list.add(new Vegetable(
            "cabbage", "Green Cabbage", AssetManager.CABBAGE,
            new GridSize(2, 1), 6.8f, 2, 3, true,
            "The cabbage is wilting in the heat!"
        ));
        list.add(new Vegetable(
            "apple", "Red Apple", AssetManager.APPLE,
            new GridSize(1, 1), 12.5f, 1, 2, false,
            "The apple is getting bruised and warm!"
        ));

        list.add(new BakeryItem(
            "bread", "Artisan Bread", AssetManager.BREAD, ItemCategory.BAKERY,
            new GridSize(2, 1), 9.5f, 1, 1, true, false,
            "The bread loaf is going stale!"
        ));
        list.add(new BakeryItem(
            "pizza_box", "Pizza Box", AssetManager.PIZZA_BOX, ItemCategory.BAKERY,
            new GridSize(2, 2), 10.0f, 2, 1, false, false,
            "The leftover pizza box needs chilling!"
        ));
        list.add(new BakeryItem(
            "jam_jar", "Berry Jam Jar", AssetManager.JAM_JAR, ItemCategory.PANTRY,
            new GridSize(1, 1), 13.5f, 2, 3, false, false,
            "The opened jam jar should be chilled!"
        ));
        list.add(new BakeryItem(
            "juice_bottle", "Juice Bottle", AssetManager.JUICE_BOTTLE, ItemCategory.PANTRY,
            new GridSize(1, 2), 9.0f, 2, 0, false, true,
            "The fresh juice is warming up!"
        ));
        list.add(new BakeryItem(
            "soda_can", "Soda Can", AssetManager.SODA_CAN, ItemCategory.PANTRY,
            new GridSize(1, 1), 14.5f, 2, 3, false, false,
            "The soda can is losing its chill!"
        ));

        list.add(new Leftover(
            "leftovers_container", "Open Leftovers", AssetManager.LEFTOVERS_CONTAINER,
            new GridSize(1, 1), 6.0f, 1, 0, true,
            "The open leftovers are starting to spoil!"
        ));
        list.add(new Leftover(
            "soup_container", "Hot Soup Bowl", AssetManager.SOUP_CONTAINER,
            new GridSize(1, 1), 5.6f, 1, 0, true,
            "The soup container is about to go bad!"
        ));
        list.add(new Leftover(
            "takeout_box", "Takeout Box", AssetManager.TAKEOUT_BOX,
            new GridSize(1, 1), 6.8f, 2, 2, false,
            "The takeout box smells questionable!"
        ));

        return list;
    }

    private static List<TrapItem> createTrapTemplates() {
        List<TrapItem> traps = new ArrayList<>();
        traps.add(new TrapItem(
            "empty_milk_carton", "Empty Milk Carton", AssetManager.EMPTY_MILK_CARTON,
            new GridSize(2, 1), 10.0f,
            "Why is an empty milk carton still on the tray?!"
        ));
        traps.add(new TrapItem(
            "empty_can", "Crushed Empty Can", AssetManager.EMPTY_CAN,
            new GridSize(1, 1), 11.0f,
            "That crushed tin can belongs in the trash!"
        ));
        traps.add(new TrapItem(
            "banana_peel", "Rotten Banana Peel", AssetManager.BANANA_PEEL,
            new GridSize(1, 1), 9.0f,
            "The banana peel is attracting fruit flies!"
        ));
        traps.add(new TrapItem(
            "plastic_bag", "Crinkled Plastic Bag", AssetManager.PLASTIC_BAG,
            new GridSize(2, 1), 11.5f,
            "Trash the plastic bag before it clutters the kitchen!"
        ));
        return traps;
    }

    public static List<Item> getAllGroceryTemplates() {
        List<Item> copies = new ArrayList<>();
        for (Item item : GROCERY_TEMPLATES) {
            copies.add(item.createFreshCopy());
        }
        return copies;
    }

    public static List<TrapItem> getAllTrapTemplates() {
        List<TrapItem> copies = new ArrayList<>();
        for (TrapItem trap : TRAP_TEMPLATES) {
            copies.add((TrapItem) trap.createFreshCopy());
        }
        return copies;
    }

    public static boolean isShortLifeItem(Item item) {
        return item.getCategory() == ItemCategory.MEAT
            || item.getCategory() == ItemCategory.LEFTOVER
            || item.getId().equals("lettuce")
            || item.getId().equals("broccoli")
            || item.getId().equals("cabbage");
    }
}
