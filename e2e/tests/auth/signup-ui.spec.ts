import {expect, test} from "@playwright/test";

test.describe('회원가입 UI/UX', () => {

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

    test.describe('기본 화면', () => {

        test('화면에 제목이 표시된다.', async ({page}) => {
            await expect(
                page.getByRole('heading', {name: '회원가입', level: 1}),
            ).toBeVisible();

            await expect(
                page.getByText('돈 매니저', {exact: true})
            ).toBeVisible();
        });

        test('화면에 입력란이 표시된다.', async ({ page }) => {
            for(const field of fields) {
                const input = page.getByLabel(
                    new RegExp(`^${field.label}\\*?$`)
                );

                await expect(input).toBeVisible();

                await expect(input).toHaveAttribute('placeholder', field.placeholder);

                await expect(input).toHaveAttribute('type', field.type);
            }
        });

        test('필수 입력란에 필수 입력 표시가 존재한다.', async ({ page }) => {
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

        test('성별 기본값이 선택된 상태로 표시된다.', async ({ page }) => {
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

        test('전송, 취소, 가입 버튼이 표시된다.', async ({ page }) => {
            const buttons = ['전송', '취소', '가입'];

            for(const button of buttons) {
                await expect(page.getByRole('button', {name: `${button}`})).toBeVisible();
            }
        });

    });

    test.describe('공통 이벤트', () => {
        test('입력란에 포커스인 상태면 테두리가 표시된다.', async ({ page }) => {
            for(const field of fields) {
                const input = page.getByLabel(
                    new RegExp(`^${field.label}\\*?$`)
                );

                await input.focus();

                await expect(input).toHaveCSS(
                    'border-color',
                    'rgb(20, 145, 138)'
                );
            }
        });
    });

});