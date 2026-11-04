//========================================
// 메시지
//========================================
const Message = {
    page(key) {
        return window.pageMessages?.[key] ?? key;
    }
};