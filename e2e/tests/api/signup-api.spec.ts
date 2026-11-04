import { test, expect } from '@playwright/test';

test.beforeEach(async ({ page }) => {
    await page.goto('/signup');
});

test('아이디 중복 API 요청 및 응답 검증', async ({ page }) => {
    //API 요청
    const responsePromise = page.waitForResponse(response =>
        response.url().includes('/api/accounts/username-availability')
        && response.request().method() === 'GET'
    );

    await page.getByLabel('아이디').fill('hong123');

    await page.getByLabel('아이디').blur();

    const response = await responsePromise;

    //요청값 검증
    const requestData = response.request();
    expect(requestData.url()).toContain('username=hong123');

    //응답 검증
    expect(response.status()).toBe(200);

    const responseData = await response.json();

    expect(responseData).toMatchObject({
        success: true,
        message: '사용 가능한 아이디입니다.',
        data: {
            available: true
        }
    });
});