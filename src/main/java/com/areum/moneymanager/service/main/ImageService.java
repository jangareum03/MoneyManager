package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.dto.ReqServiceDto;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class ImageService {

    @Value("${image.path}")
    private String downPath;

    //폴더 생성
    private File makeFolder( String mid, String date ) {
        String folder = downPath + mid + "/accountBook/" + date.substring(0, 6) + "/";

        return new File(folder);
    }

    //사진 구분
    public void uploadImageFile(ReqServiceDto.Write write, String mid ) throws Exception {
        String mode = "out";
        if( write.getCategory().substring(0, 2).equals("01") ) {
            mode = "in";
        }

        for( int i=0; i<write.getImages().size(); i++ ) {
            if( !write.getImages().get(i).isEmpty() ){
                saveImage( write.getImages().get(i), write.getAccountDate(),  mid, i, mode);
            }
        }

    }

    //사진저장
    public void saveImage( MultipartFile file, String date, String mid, int index, String mode ) throws Exception {
        File fileDir = makeFolder(mid, date);

        if( !fileDir.exists() ) {
            fileDir.mkdirs();
        }


        String fileName = FilenameUtils.getBaseName(file.getOriginalFilename());
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String saveFileName = String.format("[%s]%s_%s",mode+date.substring(6), fileName, index+"."+ext );

        File saveFile = new File(fileDir, saveFileName);
        file.transferTo(saveFile);
    }

}
