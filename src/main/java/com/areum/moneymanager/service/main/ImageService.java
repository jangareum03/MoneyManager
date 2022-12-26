package com.areum.moneymanager.service.main;

import com.areum.moneymanager.config.WebConfig;
import com.areum.moneymanager.dao.ServiceDao;
import com.areum.moneymanager.dao.ServiceDaoImpl;
import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
public class ImageService {

    @Value("${image.downPath}")
    private String downPath;

    private final ServiceDao serviceDao;

    public ImageService( ServiceDaoImpl serviceDao ) {
        this.serviceDao = serviceDao;
    }

    //폴더 생성
    private File makeFolder( String mid, String date ) {
        String folder = downPath + "/accountBook/" + mid + "/" + date.substring(0, 4) + "/";

        return new File(folder);
    }

    //사진경로 찾기
    public String findImage( String mid, ResServiceDto.DetailAccount detailAccount ) {
        String folder = mid + "/" + detailAccount.getDate().substring(0, 4) + "/";

        return folder + detailAccount.getId() + "_" + detailAccount.getDate() + "01_" + detailAccount.getImage();
    }

    //사진  업로드
    public void uploadImageFile( ReqServiceDto.Write write, String mid ) throws Exception {
        for( int index=0; index<write.getImages().size(); index++ ) {
            if( !write.getImages().get(index).isEmpty() ){
                saveImage( write.getImages().get(index), mid, serviceDao.selectId(mid), write.getAccountDate(), "0" + (index+1) );
            }
        }
    }

    public void uploadImageFile( ReqServiceDto.UpdateAccount updateAccount, String mid ) throws Exception {
        for( int index=0; index<updateAccount.getImages().size(); index++ ) {
            if( !updateAccount.getImages().get(index).isEmpty() ){
                saveImage( updateAccount.getImages().get(index), mid, serviceDao.selectId(mid), updateAccount.getAccountDate(), "0" + (index+1) );
            }
        }
    }

    //사진저장
    public void saveImage( MultipartFile file, String mid, Long id, String date, String index ) throws Exception {
        File fileDir = makeFolder(mid, date);

        if( !fileDir.exists() ) {
            fileDir.mkdirs();
        }

        String saveFileName = String.format("%d_%s_%s", id, date + index, file.getOriginalFilename() );

        File saveFile = new File(fileDir, saveFileName);
        file.transferTo(saveFile);
    }

}
