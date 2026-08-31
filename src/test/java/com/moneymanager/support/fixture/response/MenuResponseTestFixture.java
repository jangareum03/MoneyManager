package com.moneymanager.support.fixture.response;

import com.moneymanager.ledger.domain.dto.response.history.MenuResponse;
import com.moneymanager.ledger.domain.dto.response.item.CategoryItem;
import com.moneymanager.ledger.domain.dto.response.item.MenuItem;
import com.moneymanager.ledger.domain.dto.response.item.SubMenuItem;
import com.moneymanager.ledger.domain.enums.HistoryMenu;
import com.moneymanager.support.fixture.entity.category.CategoryFixture;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuResponseTestFixture {

    private HistoryMenu menu;

    private MenuResponseTestFixture() {}

    public static MenuResponseTestFixture builder() {
        return new MenuResponseTestFixture();
    }

    public SubMenuItem subMenuItem() {
        Map<String, List<CategoryItem>> sub = new HashMap<>();

        sub.put("수입", List.of(CategoryItem.from(CategoryFixture.income())));
        sub.put("지출", List.of(CategoryItem.from(CategoryFixture.outlay())));

        return SubMenuItem.of(sub);
    }

    public MenuItem menuItem(HistoryMenu menu) {
        return MenuItem.from(
                menu,
                subMenuItem(),
                HistoryMenu.ALL
        );
    }

    public MenuResponse build() {
        return MenuResponse.from(
                List.of(
                        menuItem(HistoryMenu.ALL),
                        menuItem(HistoryMenu.CATEGORY)
                )
        );
    }

}