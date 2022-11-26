package com.tlcn.demo.util.contant;

public enum FolderName {
    AVATARS("avatars"), POST("postImages"), FILE("fileUploads");

    private final String name;

    public String getName() {
        return name;
    }

    FolderName(String name) {
        this.name = name;
    }

}
