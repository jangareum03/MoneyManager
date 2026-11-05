import { test, expect } from '@playwright/test';

test.describe('회원가입', () => {

    test.describe('페이지 진입', () => {

        test('회원가입 페이지에 접근할 수 있다.',  async ({ page }) => {
            await page.goto('/signup');
        });

    });

    test.describe('아이디 중복 검사', () => {

        test.beforeEach(async ({ page }) => {
            await page.goto('/signup');
        });

        test('아이디 미입력 후 포커스 아웃하면 오류 메시지가 표시된다.', async ({ page }) => {
            const input = page.getByLabel('아이디');

            await input.blur();

            expect(page.getByText('아이디를 확인해주세요.'));
        });

    });

});