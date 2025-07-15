package com.areum.moneymanager.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface ImageService<PK> {

	void saveImage( PK pk, MultipartFile image ) throws IOException;

	String findImage( PK pk );

	void deleteImage( Path path );

}
