package com.moneymanager.ledger.service.read;

import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.ledger.domain.dto.response.item.CategoryItem;
import com.moneymanager.ledger.domain.entity.Category;
import com.moneymanager.ledger.domain.enums.CategoryType;
import com.moneymanager.ledger.service.cache.CategoryCacheService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.moneymanager.global.exception.code.ErrorCode.DATA_NOT_FOUND;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.ledger<br>
 * 파일이름       : CategoryReadService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 7<br>
 * 설명              : 카테고리 정보를 조회하는 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 * 		<thead>
 * 		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 * 		 	  	<td>날짜</td>
 * 		 	  	<td>작성자</td>
 * 		 	  	<td>변경내용</td>
 * 		 	</tr>
 * 		</thead>
 * 		<tbody>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>26. 1. 7.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
public class CategoryReadService {

    private final Map<String, Category> categoryMap;

    public CategoryReadService(CategoryCacheService categoryCacheService) {
        this.categoryMap = categoryCacheService.getCategoryMap();
    }

    public List<CategoryItem> getRootCategories() {
        return categoryMap.values().stream()
                .filter(c -> c.getParentCode() == null)
                .map(CategoryItem::from)
                .sorted(Comparator.comparing(CategoryItem::getCode))
                .collect(Collectors.toList());
    }

    public List<CategoryItem> getMiddleCategories(CategoryType type) {
        return categoryMap.values().stream()
                .filter(c -> c.getParentCode() != null)
                .filter(c -> c.getParentCode().endsWith("0000"))
                .filter(
                        c -> type == CategoryType.INCOME
                                ? c.getCode().startsWith("01")
                                : c.getCode().startsWith("02")
                )
                .map(CategoryItem::from)
                .sorted(Comparator.comparing(CategoryItem::getCode))
                .toList();
    }

    public List<CategoryItem> getLowCategories(CategoryType type) {
        return categoryMap.values().stream()
                .filter(c ->
                        c.getParentCode() != null
                                && c.getParentCode().endsWith("00")
                                && !c.getParentCode().endsWith("0000")
                )
                .filter(
                        c -> type == CategoryType.INCOME
                                ? c.getCode().startsWith("01")
                                : c.getCode().startsWith("02")
                )
                .map(CategoryItem::from)
                .sorted(Comparator.comparing(CategoryItem::getCode))
                .collect(Collectors.toList());
    }

    public List<CategoryItem> getChildrenByParentCode(String code) {
        Category current = getCategory(code);

        return categoryMap.values().stream()
                .filter(c ->
                        c.getParentCode() != null
                                && c.getParentCode().equals(current.getCode())
                )
                .map(CategoryItem::from)
                .sorted(Comparator.comparing(CategoryItem::getCode))
                .collect(Collectors.toList());
    }

    public List<CategoryItem> findCategoryHierarchy(String code) {
        List<Category> result = new ArrayList<>();

        Category current = getCategory(code);

        while (current != null) {
            result.add(current);

            String parentCode = current.getParentCode();
            current = (parentCode != null) ? getCategory(parentCode) : null;
        }

        Collections.reverse(result);

        return result.stream()
                .map(CategoryItem::from)
                .toList();
    }


    public Category getCategory(String code) {
        Category category = categoryMap.get(code);

        if (category == null) {
            throw new ApplicationException(
                    DATA_NOT_FOUND,
                    LogContent.of(
                            "카테고리 조회",
                            Category.class,
                            "code", code
                    )
            );
        }

        return category;
    }

    public boolean exists(String code) {
        return categoryMap.get(code) != null;
    }

}
