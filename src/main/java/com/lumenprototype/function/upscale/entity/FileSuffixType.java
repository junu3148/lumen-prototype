package com.lumenprototype.function.upscale.entity;

public enum FileSuffixType {
    BEFORE("_Before"),
    AFTER("_After"),
    IMAGE("_img"),
    DEFAULT("");

    private final String suffix;

    FileSuffixType(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }

    // 파일 타입 문자열로부터 적절한 enum을 찾아내는 메서드
    public static FileSuffixType fromString(String type) {
        for (FileSuffixType fileType : FileSuffixType.values()) {
            if (fileType.name().equalsIgnoreCase(type)) {
                return fileType;
            }
        }
        return DEFAULT;
    }
}