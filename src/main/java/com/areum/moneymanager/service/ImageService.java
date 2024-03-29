package com.areum.moneymanager.service;

import com.areum.moneymanager.dao.ServiceDao;
import com.areum.moneymanager.dao.ServiceDaoImpl;
import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResMemberDto;
import com.areum.moneymanager.dto.ResServiceDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


@Service
public class ImageService {

    @Value("${image.downPath}")
    private String downPath;

    private final ServiceDao serviceDao;

    public ImageService( ServiceDaoImpl serviceDao ) {
        this.serviceDao = serviceDao;
    }

    //폴더 생성
    private File makeFolder(String mid, String date, String type ) {
        String folder = "";

        if (type.equals("a")) {
            folder = downPath + "/accountBook/" + mid + "/" + date.substring(0, 4) + "/";
        } else {
            folder = downPath + "/profile/" + mid + "/" + date + "/";
        }

        return new File(folder);
    }

    private File makeFolder( String mid, String date ) {
        String folder = downPath + "/accountBook/" + mid + "/" + date.substring(0, 4) + "/";

        return new File(folder);
    }

    //사진경로 찾기
    public String findImage( String mid, ResServiceDto.DetailAccount detailAccount ) {
        String folder = mid + "/" + detailAccount.getDate().substring(0, 4) + "/";

        return folder + detailAccount.getId() + "_" + detailAccount.getDate() + "01_" + detailAccount.getImage();
    }

    //프로필 사진 경로 찾기
    public String findProfile( String mid, String profile, ResMemberDto.ProfileHistory profileHistory ) {
        String folder = mid + "/" + profileHistory.getYear() + "/";

        String month = profileHistory.getMonth().length() == 1 ? "0" + profileHistory.getMonth() : profileHistory.getMonth();
        String date = profileHistory.getDate().length() == 1 ? "0" + profileHistory.getDate() : profileHistory.getDate();

        return folder + month + date + profileHistory.getTime() + "_" + profile;
    }

    //사진  업로드
    public void uploadImageFile(ReqServiceDto.Write write, String mid ) throws Exception {
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

    public void uploadProfile( String mid, ResMemberDto.ProfileHistory profileHistory, MultipartFile multipartFile ) throws Exception {
        saveProfile( mid, profileHistory, multipartFile );
    }

    //사진저장
    public void saveImage( MultipartFile file, String mid, Long id, String date, String index ) throws Exception {
        File fileDir = makeFolder(mid, date, "a");

        if( !fileDir.exists() ) {
            fileDir.mkdirs();
        }

        String saveFileName = String.format("%d_%s_%s", id, date + index, file.getOriginalFilename() );

        File saveFile = new File(fileDir, saveFileName);
        file.transferTo(saveFile);
    }


    //프로필 저장
    private void saveProfile( String mid, ResMemberDto.ProfileHistory profileHistory, MultipartFile multipartFile ) throws IOException {
        File fileDir = makeFolder( mid, profileHistory.getYear(), "p" );

        if( !fileDir.mkdirs() ) {
            System.out.println("해당 경로는 이미 존재합니다.");
        }

        //이미지 이름 지정
        String month = profileHistory.getMonth().length() == 1 ? "0" + profileHistory.getMonth() : profileHistory.getMonth();
        String date = profileHistory.getDate().length() == 1 ? "0" + profileHistory.getDate() : profileHistory.getDate();
        String saveFileName = String.format("%s_%s", month + date + profileHistory.getTime(), profileHistory.getProfile() );

        File saveFile = new File(fileDir, saveFileName);
        if( profileHistory.getProfile().equals(multipartFile.getOriginalFilename()) ) {
            multipartFile.transferTo(saveFile);
        }
    }
}
