import { test, expect } from '@playwright/test';

test.describe('회원가입 랜더링 테스트', () => {

    const fields = [
        {
            label: '아이디',
            placeholder: '아이디를 입력해주세요.',
            type: 'text'
        },
        {
            label: '비밀번호',
            placeholder: '비밀번호를 입력해주세요.',
            type: 'password'
        },
        {
            label: '비밀번호 확인',
            placeholder: '비밀번호를 재입력해주세요.',
            type: 'password'
        },
        {
            label: '이름',
            placeholder: '이름을 입력해주세요.',
            type: 'text'
        },
        {
            label: '생년월일',
            placeholder: '생년월일을 입력해주세요. (ex. 19950101)',
            type: 'text'
        },
        {
            label: '이메일',
            placeholder: '이메일을 입력해주세요.',
            type: 'email'
        }
    ];

    test.beforeEach(async ({page}) => {
        await page.goto('/signup');
    })

    test('화면에 제목이 표시된다.', async ({page}) => {
        await expect(
            page.getByRole('heading', {name: '회원가입', level: 1}),
        ).toBeVisible();

        await expect(
            page.getByText('돈 매니저', {exact: true})
        ).toBeVisible();
    });

    test('화면에 입력란이 표시된다.', async ({page}) => {
        for (const field of fields) {
            const input = page.getByLabel(
                new RegExp(`^${field.label}\\*?$`)
            );

            await expect(input).toBeVisible();

            await expect(input).toHaveAttribute('placeholder', field.placeholder);

            await expect(input).toHaveAttribute('type', field.type);
        }
    });

    test('필수 입력란에 필수 입력 표시가 존재한다.', async ({page}) => {
        const requiredLabels = page.locator('.signup-page__label--required');

        await expect(requiredLabels).toHaveCount(4);

        await expect(requiredLabels).toHaveText([
            /^아이디$/,
            /^비밀번호$/,
            /^이름$/,
            /^이메일$/
        ]);

        const afterContents = await requiredLabels.evaluateAll(labels =>
            labels.map(label => getComputedStyle(label, '::after').content)
        );

        const afterColors = await requiredLabels.evaluateAll(labels =>
            labels.map(label => getComputedStyle(label, '::after').color)
        );

        expect(afterContents).toEqual(Array(4).fill('"*"'));
        expect(afterColors).toEqual(Array(4).fill('rgb(210, 24, 13)'));
    });

    test('성별 기본값이 선택된 상태로 표시된다.', async ({page}) => {
        const genderNone = page.getByRole('radio', {name: '선택없음'});
        const genderMale = page.getByRole('radio', {name: '남자'});
        const genderFemale = page.getByRole('radio', {name: '여자'});

        //라디오 표시 여부
        await expect(genderNone).toBeVisible();
        await expect(genderMale).toBeVisible();
        await expect(genderFemale).toBeVisible();

        //기본 선택 상태
        await expect(genderNone).toBeChecked();
        await expect(genderMale).not.toBeChecked();
        await expect(genderFemale).not.toBeChecked();
    });

    test('전송, 취소, 가입 버튼이 표시된다.', async ({page}) => {
        const buttons = ['전송', '취소', '가입'];

        for (const button of buttons) {
            await expect(page.getByRole('button', {name: `${button}`})).toBeVisible();
        }
    });
});


test.describe('아이디 중복 검사', () => {
    test.beforeEach(async ({ page }) => {
        await page.goto('/signup');
    });

    test('아이디를 입력하지 않고 포커스를 아웃하면 필수 메시지가 표시된다.', async ({ page }) => {
        const input = page.getByLabel('아이디');
        const message = page.getByText('아이디를 확인해주세요.');

        await input.focus();
        await input.blur();

        await expect(message).toBeVisible();
        await expect(message).toHaveClass(/message--error/);
    });

    test('중복된 아이디를 입력하면 중복 메시지가 표시된다.', async ({ page }) => {
        const input = page.getByLabel('아이디');
        const message = page.getByText('이미 사용 중인 아이디입니다.');

        await input.fill('test123');
        await input.blur();

        await expect(message).toBeVisible();
        await expect(message).toHaveClass(/message--error/);
    });

    test('사용 가능한 아이디를 입력하면 성공 메시지가 표시된다.', async ({page}) => {
        const input = page.getByLabel('아이디');
        const message = page.getByText('사용 가능한 아이디입니다.');

        await input.fill('hong123');
        await input.blur();

        await expect(message).toBeVisible();
        await expect(message).toHaveClass(/message--success/);
    });

    test('아이디 입력값을 변경하면 기존 메시지가 사라진다.', async ({ page }) => {
        const input = page.getByLabel('아이디');

        const failMessage = page.getByText('이미 사용 중인 아이디입니다.');
        const successMessage = page.getByText('사용 가능한 아이디입니다.');

        //첫번째 입력값: 사용 가능한 아이디
        await input.fill('hong123');
        await input.blur();

        await expect(successMessage).toBeVisible();

        //두번째 입력값: 사용 불가능한 아이디
        await input.fill('test123');

        await expect(successMessage).toBeHidden();

        await input.blur();

        await expect(failMessage).toBeVisible();
    });

    test('기존 입력값과 동일하면 아무것도 하지 않는다.', async ({ page }) => {
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

        await expect(page.locator('.message')).toBeVisible();
    });

    test('오래된 요청보다 최신 요청의 결과를 우선 표시한다.', async ({ page }) => {
        const input = page.getByLabel('아이디');

        const failMessage = page.getByText('이미 사용 중인 아이디입니다.');
        const successMessage = page.getByText('사용 가능한 아이디입니다.');

        await page.route('**/api/accounts/username-availability**', async route => {
            const url = new URL(route.request().url());
            const username = url.searchParams.get('username');

            if(username === 'hong123') {
                await new Promise(resolve => setTimeout(resolve, 2000));
            }

            await route.fulfill({
                status: 200,
                contentType: 'application/json',
                body: JSON.stringify({
                    success: true,
                    message: username === 'hong123'
                    ? '사용 가능한 아이디입니다.'
                    : '이미 사용 중인 아이디입니다.',
                    data: {
                        available: username === 'hong123'
                    }
                })
            });
        });

        //첫번째 요청
        await input.fill('hong123');
        await input.blur();

        //두번째 요청
        await input.fill('test123');
        await input.blur();

        await expect(failMessage).toBeVisible();

        await page.waitForTimeout(2500);

        await expect(failMessage).toBeVisible();
        await expect(successMessage).toBeHidden();
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