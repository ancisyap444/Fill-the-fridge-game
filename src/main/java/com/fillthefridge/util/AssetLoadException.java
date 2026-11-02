package com.fillthefridge.util;

public class AssetLoadException extends RuntimeException {
    private final String assetPath;

    public AssetLoadException(String message, String assetPath) {
        super(message);
        this.assetPath = assetPath;
    }

    public AssetLoadException(String message, String assetPath, Throwable cause) {
        super(message, cause);
        this.assetPath = assetPath;
    }

    public String getAssetPath() {
        return assetPath;
    }
}
