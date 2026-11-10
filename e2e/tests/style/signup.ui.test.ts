import { test, expect, Page} from "@playwright/test";
import {asyncWrapProviders} from "node:async_hooks";

test.beforeEach(async ({page}) => {
    await page.goto('/signup');
});

test.describe('회원가입 입력란 테스트', () => {

    async function createInput(page: Page, ...classNames: string[]) {
        await page.evaluate((classes) => {
            const input = document.createElement("input");

            input.id = 'test-input';
            input.classList.add(...classes);

            document.body.appendChild(input);

        }, classNames);
    }

    test('기본 상태는 회색 테두리로 표시된다.', async ({ page }) => {
        await createInput(page, 'signup-page__input');

        const input = page.locator('#test-input');

        await expect(input).toHaveCSS(
            'border-color',
            'rgb(174, 169, 169)'
        );
    });

    test('포커스 시 청록색 테두리로 표시된다.', async ({ page }) => {
        await createInput(page, 'signup-page__input');

        const input = page.locator('#test-input');

        await input.focus();

        await expect(input).toHaveCSS(
            'border-color',
            'rgb(20, 145, 138)'
        );
    });

    test('오류 클래스 적용 시 빨간색 테두리로 표시된다.', async ({ page }) => {
        await createInput(page, 'signup-page__input', 'signup-page__input--error');

        const input = page.locator('#test-input');

        await expect(input).toHaveCSS(
            'border-color',
            'rgb(210, 24, 13)'
        );
    });

    test('성공 클래스 적용 시 초록색 테두리로 표시된다.', async ({ page }) => {
        await createInput(page, 'signup-page__input', 'signup-page__input--success');

        const input = page.locator('#test-input');

        await expect(input).toHaveCSS(
            'border-color',
            'rgb(20, 83, 41)'
        );
    });

});


test.describe('회원가입 안내 메시지 테스트', () => {

    async function createMessage(page: Page, className: string) {
        await page.evaluate((className) => {
            const message = document.createElement("p");

            message.id = 'test-message';
            message.classList.add('message', className);

            document.body.appendChild(message);
        }, className);
    }

    test('오류 메시지는 빨간색으로 표시된다.', async ({ page }) => {
        await createMessage(page, 'message--error');

        const message = page.locator('#test-message');

        await expect(message).toHaveCSS(
            'color',
            'rgb(210, 24, 13)'
        );
    });

    test('성공 메시지는 초록색으로 표시된다.', async ({ page }) => {
        await createMessage(page, 'message--success');

        const message = page.locator('#test-message');

        await expect(message).toHaveCSS(
            'color',
            'rgb(20, 83, 41)'
        );
    });

});