import { test, expect } from '@playwright/test';

test.describe('로그인', () => {

    test.beforeEach('로그인 페이지', async ({ page }) => {
        await page.goto('/');
    });

    test('로그인 화면이 정상적으로 표시된다.', async ({ page }) => {

        await expect(
            page.locator('#username')
        ).toBeVisible();

    });

});