package com.moneymanager.support.fixture.file;

import com.moneymanager.global.domain.FileMetadata;

import java.nio.file.Path;

public final class FileMetadataFixture {

    private final Path absolutePath;
    private final Path relativePath;
    private final String originalFileName;
    private final String storedFileName;
    private final String contentType;

    private FileMetadataFixture(Path absolutePath, Path relativePath, String originalFileName, String storedFileName, String contentType) {
        this.absolutePath = absolutePath;
        this.relativePath = relativePath;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.contentType = contentType;;
    }

    public static FileMetadataFixture jpg(String fileName) {
        return new FileMetadataFixture(
                Path.of("/root/images").resolve("member").resolve(fileName),
                Path.of("/member").resolve(fileName),
                fileName,
                "stored" + fileName,
                "image/jpeg"
        );
    }

    public static FileMetadataFixture png(String fileName) {
        return new FileMetadataFixture(
                Path.of("/root/images").resolve("member").resolve(fileName),
                Path.of("/member").resolve(fileName),
                fileName,
                "stored" + fileName,
                "image/png"
        );
    }


    public FileMetadata build() {
        return FileMetadata.of(
                absolutePath,
                relativePath,
                originalFileName,
                storedFileName,
                contentType
        );
    }

}