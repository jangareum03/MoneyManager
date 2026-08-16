import { test as setup, expect } from '@playwright/test';
import dotenv from 'dotenv';

dotenv.config();

setup('회원 인증', async ({ page }) => {
    await page.goto('/');

    await page.getByLabel('아이디').fill(process.env.MEMBER_USERNAME!);
    await page.getByLabel('비밀번호').fill(process.env.MEMBER_PASSWORD!);

    await page.getByRole('button', {name: '로그인'}).click();

    await expect(page).toHaveURL('/ledgers/new/step1');

    await page.context().storageState({
        path: 'playwright/.auth/user.json',
    });
});