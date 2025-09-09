package com.example.unicon.module.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleVO {
    private String moduleId;
    private String code;
    private String name;
    private String description;
    private String icon;
    private Long price;

    //모듈 마켓플레이스
    // 페이징 처리용
    private Integer pageSize;
    private Integer pageIndex;

    // 검색 조건용 필드들
    private String scModuleId;
    private String scCode;
    private String scName;
    private String scDescription;
    private String scPrice;
    private String scIcon;

    // 프론트엔드에서 사용할 추가 정보
    private String category;        // 카테고리 (education, meeting, management)
    private Boolean subscribed;     // 구독 여부
    private String formattedPrice;  // 포맷된 가격 문자열
    private Double rating;          // 평점
    private String ratingStars;     // 별점 문자열

    // 페이징 계산 헬퍼 메서드
    public Integer getOffset() {
        if (pageIndex != null && pageSize != null && pageIndex > 0) {
            return (pageIndex - 1) * pageSize;
        }
        return 0;
    }

}