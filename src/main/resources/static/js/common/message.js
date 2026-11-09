window.Message = {
    page(key) {
        return window.pageMessages?.[key] ?? key;
    }
};

window.MessageUI = {
    create(text, type="error") {
        const message = document.createElement('p');

        message.classList.add("message", `message--${type}`);
        message.textContent = text;

        return message;
    },

    show(parent, text, type="error") {
        this.hide(parent);

        const message = this.create(text, type);
        parent.appendChild(message);

        return message;
    },

    hide(parent) {
        parent.querySelectorAll(".message").forEach(
           message => message.remove()
        );
    }
};