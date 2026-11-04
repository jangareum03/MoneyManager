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

        test('유효하지 않은 아이디 입력 후 포커스 아웃하면 오류 메시지가 표시된다.', async ({ page }) => {
            const input = page.getByLabel('아이디');

            await input.fill('test');

            await input.blur();

            expect(page.getByText('아이디는 4~15자로 입력해주세요.'));
        });

        test('이미 가입된 아이디면 중복 오류 메시지가 표시된다.', async ({ page }) => {
            const input = page.getByLabel('아이디');

            await input.fill('test123');
            await input.blur();

            expect(page.getByText('이미 사용 중인 아이디입니다.'));
        });

        test('사용 가능한 아이디면 성공 오류 메시지가 표시된다.', async ({page}) => {
            const input = page.getByLabel('아이디');

            await input.fill('hong123');
            await input.blur();

            expect(page.getByText('사용 가능한 아이디입니다.'));
        });

        test('검증 완료 후 다른값을 입력하면 아이디 중복 확인을 재진행한다.', async ({ page }) => {
            const input = page.getByLabel('아이디');

            await input.fill('hong123');
            await input.blur();

            await input.fill('test123');
            await input.blur();

            expect(page.getByText('이미 사용 중인 아이디입니다.'));
            !expect(page.getByText('사용 가능한 아이디입니다.'));
        });

        test('검증 후 기존값과 동일하면 아무것도 하지 않고 메시지가 표시되지 않는다.', async ({ page }) => {
            let requestCount = 0;

            page.on('request', request => {
                if(request.url().includes('/api/accounts/username-availability')) {
                    requestCount++;
                }
            });

            const responsePromise = page.waitForResponse(response =>
                response.url().includes('/api/accounts/username-availability')
            );

            const input = page.getByLabel('아이디');

            await input.fill('hong123');
            await input.blur();

            await responsePromise;

            await input.blur();

            expect(requestCount).toBe(1);

            await input.focus();

            await expect(page.locator('.message')).not.toBeVisible();
        });

        test('이전 요청 취소 시 알릿이 표시되지 않는다.',  async ({ page }) => {
            let firstRequest = false;

            await page.route('**/api/accounts/username-availability**', async route => {
                const username = new URL(route.request().url()).searchParams.get('username');

                if(username === 'hong123') {
                    firstRequest = true;

                    //읃답 지연
                    await new Promise(resolve => setTimeout(resolve, 1000));

                    try{
                        await route.fulfill({
                            status: 200,
                            contentType: 'application/json',
                            body: JSON.stringify({
                                success: true,
                                message: '성공 메시지',
                                data: { available: true }
                            })
                        });
                    }catch {

                    }

                    return;
                }

                if(username === 'test123') {
                    await route.fulfill({
                        status: 200,
                        contentType: 'application/json',
                        body: JSON.stringify({
                            success: true,
                            message: '실패 메시지',
                            data: { available: false }
                        })
                    });
                }
            });

            const input = page.getByLabel('아이디');

            await input.fill('hong123');
            await input.blur();

            await expect.poll(() => firstRequest).toBe(true);

            await input.fill('test123');
            await input.blur();

            await expect(page.getByText('실패 메시지')).toBeVisible();
            await expect(page.getByRole('alert')).toBeHidden();
        });

        test('아이디 중복 확인 중 네트워크 오류가 발생하면 알럿을 표시한다.', async ({ page }) => {
            await page.route('**/api/accounts/username-availability**', async route => {
                return route.abort('failed');
            });

            const input = page.getByLabel('아이디');

            await input.fill('hong123');

            await input.blur();

            const dialog = await page.waitForEvent('dialog');

            expect(dialog.type()).toBe('alert');
            expect(dialog.message()).toBe('일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');

            await dialog.accept();
        });
    });

});