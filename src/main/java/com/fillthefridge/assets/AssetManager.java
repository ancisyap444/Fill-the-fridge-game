package com.fillthefridge.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.fillthefridge.util.AssetLoadException;
import com.fillthefridge.util.Registry;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AssetManager {
    public static final String WHOLE_CHICKEN = "whole_chicken.png";
    public static final String STEAK = "steak.png";
    public static final String FISH = "fish.png";
    public static final String SAUSAGE = "sausage.png";
    public static final String MILK = "milk.png";
    public static final String YOGURT_CUP = "yogurt_cup.png";
    public static final String CHEESE = "cheese.png";
    public static final String BUTTER = "butter.png";
    public static final String EGG_CARTON = "egg_carton.png";
    public static final String LETTUCE = "lettuce.png";
    public static final String CARROT = "carrot.png";
    public static final String TOMATO = "tomato.png";
    public static final String BROCCOLI = "broccoli.png";
    public static final String CABBAGE = "cabbage.png";
    public static final String APPLE = "apple.png";
    public static final String BREAD = "bread.png";
    public static final String PIZZA_BOX = "pizza_box.png";
    public static final String JAM_JAR = "jam_jar.png";
    public static final String JUICE_BOTTLE = "juice_bottle.png";
    public static final String SODA_CAN = "soda_can.png";
    public static final String LEFTOVERS_CONTAINER = "leftovers_container.png";
    public static final String SOUP_CONTAINER = "soup_container.png";
    public static final String TAKEOUT_BOX = "takeout_box.png";

    public static final String EMPTY_MILK_CARTON = "empty_milk_carton.png";
    public static final String EMPTY_CAN = "empty_can.png";
    public static final String BANANA_PEEL = "banana_peel.png";
    public static final String PLASTIC_BAG = "plastic_bag.png";

    public static final String TRASH_BIN = "trash_bin.png";
    public static final String HEART = "heart.png";
    public static final String WARNING_ICON = "warning_icon.png";
    public static final String SNOWFLAKE = "snowflake.png";

    private static final String ITEMS_DIRECTORY = "assets/items";
    private static final String FALLBACK_ASSETS_DIRECTORY = "assets";
    private static final String CREDITS_FILE_PATH = "assets/credits.txt";

    private final Registry<String, Texture> textureRegistry;
    private final Map<String, Boolean> placeholderFlags;
    private final List<String> discoveredFiles;
    private final List<String> missingAssets;

    private BitmapFont smallFont;
    private BitmapFont bodyFont;
    private BitmapFont subtitleFont;
    private BitmapFont titleFont;

    public AssetManager() {
        this.textureRegistry = new Registry<>();
        this.placeholderFlags = new LinkedHashMap<>();
        this.discoveredFiles = new ArrayList<>();
        this.missingAssets = new ArrayList<>();
    }

    public void loadAll() {
        initializeFonts();
        scanAvailableFiles();
        for (String catalogFile : getCatalogFilenames()) {
            loadCatalogTexture(catalogFile);
        }
    }

    private void initializeFonts() {
        smallFont = createScaledFont(0.92f);
        bodyFont = createScaledFont(1.15f);
        subtitleFont = createScaledFont(1.55f);
        titleFont = createScaledFont(2.45f);
    }

    private BitmapFont createScaledFont(float scale) {
        BitmapFont font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(scale);
        font.setUseIntegerPositions(false);
        return font;
    }

    public List<String> getCatalogFilenames() {
        return List.of(
            WHOLE_CHICKEN, STEAK, FISH, SAUSAGE,
            MILK, YOGURT_CUP, CHEESE, BUTTER, EGG_CARTON,
            LETTUCE, CARROT, TOMATO, BROCCOLI, CABBAGE, APPLE,
            BREAD, PIZZA_BOX, JAM_JAR, JUICE_BOTTLE, SODA_CAN,
            LEFTOVERS_CONTAINER, SOUP_CONTAINER, TAKEOUT_BOX,
            EMPTY_MILK_CARTON, EMPTY_CAN, BANANA_PEEL, PLASTIC_BAG,
            TRASH_BIN, HEART, WARNING_ICON, SNOWFLAKE
        );
    }

    private void scanAvailableFiles() {
        discoveredFiles.clear();
        File dir = new File(ITEMS_DIRECTORY);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile() && isImageFile(f.getName())) {
                        discoveredFiles.add(f.getName());
                    }
                }
            }
        }
        Collections.sort(discoveredFiles);
    }

    private boolean isImageFile(String filename) {
        String lower = filename.toLowerCase(Locale.ROOT);
        return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg");
    }

    private void loadCatalogTexture(String expectedFilename) {
        FileHandle resolvedHandle = resolveImageFileHandle(expectedFilename);
        if (resolvedHandle != null && resolvedHandle.exists()) {
            try {
                Pixmap rawPixmap = new Pixmap(resolvedHandle);
                Pixmap trimmedPixmap = trimTransparentBorders(rawPixmap);
                Texture texture = new Texture(trimmedPixmap);
                trimmedPixmap.dispose();
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                textureRegistry.register(expectedFilename, texture);
                placeholderFlags.put(expectedFilename, false);
                return;
            } catch (Exception ignored) {
            }
        }
        missingAssets.add(expectedFilename);
        Texture placeholder = generatePlaceholderTexture(expectedFilename);
        textureRegistry.register(expectedFilename, placeholder);
        placeholderFlags.put(expectedFilename, true);
    }

    private Pixmap trimTransparentBorders(Pixmap source) {
        int width = source.getWidth();
        int height = source.getHeight();
        int minX = width;
        int minY = height;
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = source.getPixel(x, y);
                int alpha = pixel & 0xFF;
                if (alpha > 16) {
                    if (x < minX) {
                        minX = x;
                    }
                    if (x > maxX) {
                        maxX = x;
                    }
                    if (y < minY) {
                        minY = y;
                    }
                    if (y > maxY) {
                        maxY = y;
                    }
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return source;
        }

        int trimmedWidth = maxX - minX + 1;
        int trimmedHeight = maxY - minY + 1;
        if (trimmedWidth == width && trimmedHeight == height) {
            return source;
        }

        Pixmap trimmed = new Pixmap(trimmedWidth, trimmedHeight, Pixmap.Format.RGBA8888);
        trimmed.setBlending(Pixmap.Blending.None);
        trimmed.drawPixmap(source, 0, 0, minX, minY, trimmedWidth, trimmedHeight);
        source.dispose();
        return trimmed;
    }

    private FileHandle resolveImageFileHandle(String expectedFilename) {
        String[] searchRoots = new String[]{ITEMS_DIRECTORY, FALLBACK_ASSETS_DIRECTORY, "."};
        for (String root : searchRoots) {
            String candidatePath = root.equals(".") ? expectedFilename : root + "/" + expectedFilename;
            FileHandle handle = getFileHandleSafely(candidatePath);
            if (handle != null && handle.exists() && !handle.isDirectory()) {
                return handle;
            }
        }

        String bestMatch = findBestSimilarFilename(expectedFilename);
        if (bestMatch != null) {
            for (String root : searchRoots) {
                String candidatePath = root.equals(".") ? bestMatch : root + "/" + bestMatch;
                FileHandle handle = getFileHandleSafely(candidatePath);
                if (handle != null && handle.exists() && !handle.isDirectory()) {
                    return handle;
                }
            }
        }
        return null;
    }

    private FileHandle getFileHandleSafely(String path) {
        if (Gdx.files != null) {
            FileHandle internal = Gdx.files.internal(path);
            if (internal.exists()) {
                return internal;
            }
            FileHandle local = Gdx.files.local(path);
            if (local.exists()) {
                return local;
            }
        }
        File rawFile = new File(path);
        if (rawFile.exists() && rawFile.isFile()) {
            return new FileHandle(rawFile);
        }
        return null;
    }

    private String findBestSimilarFilename(String expectedFilename) {
        String expectedStem = normalizeStem(expectedFilename);
        String bestCandidate = null;
        int bestScore = 0;

        for (String candidate : discoveredFiles) {
            if (candidate.equalsIgnoreCase("fridge_bg.png")) {
                continue;
            }
            String candidateStem = normalizeStem(candidate);
            int score = computeSimilarityScore(expectedStem, candidateStem);
            if (score > bestScore) {
                bestScore = score;
                bestCandidate = candidate;
            }
        }
        return bestScore >= 40 ? bestCandidate : null;
    }

    private String normalizeStem(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        String base = dotIndex > 0 ? filename.substring(0, dotIndex) : filename;
        return base.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private int computeSimilarityScore(String expected, String candidate) {
        if (expected.equals(candidate)) {
            return 100;
        }
        if (expected.contains(candidate) || candidate.contains(expected)) {
            return 75;
        }
        if (expected.contains("chicken") && candidate.contains("chicken")) {
            return 65;
        }
        if (expected.contains("steak") && candidate.contains("meat")) {
            return 60;
        }
        if (expected.contains("fish") && candidate.contains("fish")) {
            return 65;
        }
        if (expected.contains("yogurt") && candidate.contains("yogurt")) {
            return 65;
        }
        if (expected.contains("leftover") && candidate.contains("leftover")) {
            return 65;
        }
        return 0;
    }

    private Texture generatePlaceholderTexture(String filename) {
        int size = 128;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        Color baseColor = pickColorForFilename(filename);
        pixmap.setColor(baseColor);
        pixmap.fillRectangle(6, 6, size - 12, size - 12);

        Color borderColor = new Color(
            Math.max(0f, baseColor.r - 0.22f),
            Math.max(0f, baseColor.g - 0.22f),
            Math.max(0f, baseColor.b - 0.22f),
            1f
        );
        pixmap.setColor(borderColor);
        for (int i = 0; i < 4; i++) {
            pixmap.drawRectangle(6 + i, 6 + i, size - 12 - i * 2, size - 12 - i * 2);
        }

        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    private Color pickColorForFilename(String filename) {
        int hash = Math.abs(filename.hashCode());
        float r = 0.40f + ((hash & 0xFF) / 255f) * 0.45f;
        float g = 0.40f + (((hash >> 8) & 0xFF) / 255f) * 0.45f;
        float b = 0.40f + (((hash >> 16) & 0xFF) / 255f) * 0.45f;
        return new Color(r, g, b, 1f);
    }

    public Texture getTexture(String name) {
        return textureRegistry.get(name).orElseGet(() -> {
            loadCatalogTexture(name);
            return textureRegistry.get(name).orElseThrow(
                () -> new AssetLoadException("Unable to load or generate texture", name)
            );
        });
    }

    public boolean isPlaceholder(String name) {
        return placeholderFlags.getOrDefault(name, false);
    }

    public List<String> getDiscoveredFiles() {
        return Collections.unmodifiableList(discoveredFiles);
    }

    public List<String> getMissingAssets() {
        return Collections.unmodifiableList(missingAssets);
    }

    public List<String> loadCreditsLines() throws AssetLoadException {
        FileHandle handle = getFileHandleSafely(CREDITS_FILE_PATH);
        if (handle == null || !handle.exists()) {
            handle = getFileHandleSafely("credits.txt");
        }
        if (handle == null || !handle.exists()) {
            throw new AssetLoadException("Credits file not found", CREDITS_FILE_PATH);
        }
        try {
            String rawContent = handle.readString("UTF-8");
            String[] lines = rawContent.split("\\r?\\n");
            List<String> result = new ArrayList<>();
            Collections.addAll(result, lines);
            return result;
        } catch (Exception e) {
            throw new AssetLoadException("Failed to read credits file", CREDITS_FILE_PATH, e);
        }
    }

    public BitmapFont getSmallFont() {
        return smallFont;
    }

    public BitmapFont getBodyFont() {
        return bodyFont;
    }

    public BitmapFont getSubtitleFont() {
        return subtitleFont;
    }

    public BitmapFont getTitleFont() {
        return titleFont;
    }

    public void dispose() {
        for (Texture texture : textureRegistry.getAll().values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        textureRegistry.clear();
        if (smallFont != null) {
            smallFont.dispose();
        }
        if (bodyFont != null) {
            bodyFont.dispose();
        }
        if (subtitleFont != null) {
            subtitleFont.dispose();
        }
        if (titleFont != null) {
            titleFont.dispose();
        }
    }
}
