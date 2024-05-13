package com.lumenprototype.file;

import com.lumenprototype.function.upscale.en.FileSuffixType;

import java.io.File;

public interface FileStorageService {

    /**
     * 주어진 파일을 저장소에 저장합니다.
     *
     * @param file 저장할 파일 객체입니다.
     * @param uuid 파일을 고유하게 식별할 UUID 문자열입니다.
     * @param fileSuffixType 파일 확장자 유형을 나타내는 enum 입니다. 이는 파일을 저장할 때 사용할 확장자를 결정하는데 사용됩니다.
     */
    void storeFile(File file, String uuid, FileSuffixType fileSuffixType);

    /**
     * 지정된 파일 이름과 파일 확장자 유형에 따라 저장된 파일의 URL을 반환합니다.
     *
     * @param fileName 검색할 파일의 이름입니다.
     * @param fileSuffixType 파일 확장자 유형을 나타내는 enum입니다. 이는 URL을 생성할 때 사용할 확장자를 결정하는데 사용됩니다.
     * @return 저장된 파일의 URL을 문자열로 반환합니다. 파일이 존재하지 않을 경우 null을 반환할 수 있습니다.
     */
    String getFileUrl(String fileName, FileSuffixType fileSuffixType);
}
