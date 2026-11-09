window.ClassUtil = {
    add(element, className) {
        element?.classList.add(className);
    },

    remove(element, className) {
        element?.classList.remove(className);
    },

    toggle(element, className, force) {
        element?.classList.toggle(className, force);
    }
};