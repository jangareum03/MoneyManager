package com.areum.moneymanager.dto;

import com.areum.moneymanager.entity.AccountBook;
import com.areum.moneymanager.entity.Answer;
import com.areum.moneymanager.entity.Question;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

import java.util.Date;
import java.util.Map;


public class ResServiceDto {

    //Q&A 답변
    @Builder
    @Getter
    public static class Answer{
        private String writer;
        private Date date;
        private String title;
        private String content;

        public static Answer toDTO( com.areum.moneymanager.entity.Answer answer, String title ) {
            return Answer.builder().writer( answer.getAdmin().getNickname() ).date( answer.getRegDate() ).title( "[답변] " + title ).content( answer.getContent() ).build();
        }
    }

    //카테고리
    @Builder
    @Getter
    public static class Category {
        private String name;
        private String code;

        public static List<Category> entityToDto(List<com.areum.moneymanager.entity.Category> entityList) {
            List<ResServiceDto.Category> resultList = new ArrayList<>();

            for( com.areum.moneymanager.entity.Category entity : entityList ) {
                resultList.add( Category.builder().name(entity.getName()).code(entity.getCode()).build() );
            }

            return resultList;
        }
    }

    //가계부 정보
    @Builder
    @Getter
    public static class DetailAccount {
        private Long id;
        private String date;
        private String fix;
        private String fixOption;
        private String category;
        private String title;
        private String content;
        private int price;
        private String priceType;
        private String image;
        private String mapName;
        private String mapRoad;


        public static DetailAccount toDto( AccountBook entity ) {
            return DetailAccount.builder().id(entity.getId()).date(entity.getAccount_date()).fix(entity.getFix()).category(entity.getCategory_id()).fixOption(entity.getFix_option())
                    .title(entity.getTitle()).content(entity.getContent()).price(entity.getPrice()).priceType(entity.getPrice_type()).image(entity.getImage1()).mapName(entity.getLocation_name()).mapRoad(entity.getLocation()).build();
        }
    }

    //자주묻는질문
    @Builder
    @Getter
    public static class Faq {
        private String question;
        private String answer;
    }

    //리스트에서 전체 내역
    @Builder
    @Getter
    public static class ListAccount {
        private Long id;
        private String date;
        private String fix;
        private String code;
        private String name;
        private String title;
        private int price;
        private int totalPrice;
    }

    //월 차트
    @Builder
    @Getter
    public static class MonthChart{
        private String category;
        private int price;
    }

    public List<MonthChart> toResMonthChart(List<AccountBook> accountBookList ){
        List<ResServiceDto.MonthChart> resultList = new ArrayList<>(accountBookList.size());
        for(AccountBook accountBook : accountBookList ) {
            resultList.add(
                    ResServiceDto.MonthChart.builder()
                            .category(accountBook.getCategory_id())
                            .price(accountBook.getPrice()).build()
            );
        }

        return resultList;
    }

    //공지사항 리스트
    @Builder
    @Getter
    public static class Notice {
        private int num;
        private String id;
        private String type;
        private String title;
        private String content;
        private Date regDate;
        private int count;

        public static Notice toDTO( com.areum.moneymanager.entity.Notice entity, String type ) {
            return Notice.builder().type( type ).title( entity.getTitle() ).regDate( entity.getRegDate() ).content( entity.getContent() ).build();
        }

        public static List<ResServiceDto.Notice> toDTO(List<com.areum.moneymanager.entity.Notice> entityList, Map<Character, String> typeMap, ResServiceDto.Page pageInfo, int pageIndex ) {
            List<ResServiceDto.Notice> resultList = new ArrayList<>(entityList.size());

            int i= 1;
            for( com.areum.moneymanager.entity.Notice notice : entityList ) {
                resultList.add( Notice.builder().num( (pageIndex * pageInfo.getPostCount()) + (i++) ).id(notice.getId()).type( typeMap.get(notice.getType()) ).title( notice.getTitle() ).regDate( notice.getRegDate() ).count( notice.getReadCnt() ).build() );
            }

            return resultList;
        }
    }

    //페이징
    @Getter
    public static class Page{
        /* 총 게시물 개수 */
        private int total;
        /* 시작 페이지 */
        private int startPage;
        /* 끝 페이지 */
        private int endPage;
        /* 이전, 다음 페이지 표시 */
        private boolean isPrev, isNext;
        /* 페이지 게시물 표시 개수 */
        private int postCount;
        /* 페이지 사이즈 */
        private int pageSize;


        public Page( int total, int postCount, int pageSize, int pageIndex ) {
            this.total = total;
            this.postCount = postCount;
            this.pageSize = pageSize;

            /* 마지막페이지 */
            this.endPage = (int) (Math.ceil( pageIndex / (double)pageSize ) * pageSize);
            /* 시작페이지 */
            this.startPage = endPage - (pageSize - 1);

            /* 설정한 마지막페이지가 진짜 마지막페이지보다 큰 경우 */
            int end = (int) Math.ceil( total / (double)postCount );
            if( end == 0 ) {
                this.endPage = 1;
            }else if( end < endPage ) {
                this.endPage = end;
            }


            /* 이전 페이지 표시 */
            this.isPrev = startPage != 1;
            /* 다음페이지 표시 */
            this.isNext = endPage < end;
        }
    }

    //Q&A 리스트
    @Builder
    @Getter
    public static class QnA{
        /* 고유번호 */
        private String id;
        /* 리스트에 표시할 번호 */
        private int num;
        private String title;
        private char open;
        private Date regDate;
        private String nickName;

        public static List<QnA> toDTO( List<Question> questionList, ResServiceDto.Page pageInfo, int pageIndex ) {
            List<QnA> resultList = new ArrayList<>(questionList.size());

            int i=1;
            for( Question question : questionList ) {
                resultList.add( QnA.builder().id( question.getId() ).num( (pageInfo.getPostCount() * pageIndex) + (i++) ).title( question.getTitle() ).open( question.getOpen() ).regDate( question.getRegDate() ).nickName( question.getMemberInfo().getNickName() ).build() );
            }

            return resultList;
        }
    }

    //Q&A 상세
    @Builder
    @Getter
    public static class QnADetail {
        private String title;
        private String content;
        private String writer;
        private Date date;
        private char answer;

        public static QnADetail toDTO( Question question ) {
            return QnADetail.builder().title( question.getTitle() ).content( question.getContent() ).writer( question.getMemberInfo().getNickName() ).date( question.getRegDate() ).answer( question.getAnswer() ).build();
        }
    }

    //주 차트
    @Builder
    @Getter
    public static class WeekChart{
        private int week;
        private int inPrice;
        private int outPrice;
    }

    //년 차트
    @Builder
    @Getter
    public static class YearChart{
        private String month;
        private int inPrice;
        private int outPrice;
    }

}
