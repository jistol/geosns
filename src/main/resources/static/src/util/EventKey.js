
const KeyMapper = {
    KeyCode : {
        isEnter : v => v == 13,
        isDelete : v => v == 46,
        isBackspace : v => v == 8,
        isCapsLock : v => v == 20,
        isSpace : v => v == 32,
        isTab : v => v == 9,
        isArrowDown : v => v == 40,
        isArrowLeft : v => v == 37,
        isArrowRight : v => v == 39,
        isArrowUp : v => v == 38,
        isEscape : v => v == 27,
        isNumpad0 : v => v == 96,
        isNumpad1 : v => v == 97,
        isNumpad2 : v => v == 98,
        isNumpad3 : v => v == 99,
        isNumpad4 : v => v == 100,
        isNumpad5 : v => v == 101,
        isNumpad6 : v => v == 102,
        isNumpad7 : v => v == 103,
        isNumpad8 : v => v == 104,
        isNumpad9 : v => v == 105,
        isDigit0 : v => v == 48,
        isDigit1 : v => v == 49,
        isDigit2 : v => v == 50,
        isDigit3 : v => v == 51,
        isDigit4 : v => v == 52,
        isDigit5 : v => v == 53,
        isDigit6 : v => v == 54,
        isDigit7 : v => v == 55,
        isDigit8 : v => v == 56,
        isDigit9 : v => v == 57
    },
    Code : {
        isEnter : v => v == 'Enter',
        isDelete : v => v == 'Delete',
        isBackspace : v => v == 'Backspace',
        isCapsLock : v => v == 'CapsLock',
        isSpace : v => v == 'Space',
        isTab : v => v == 'Tab',
        isArrowDown : v => v == 'ArrowDown',
        isArrowLeft : v => v == 'ArrowLeft',
        isArrowRight : v => v == 'ArrowRight',
        isArrowUp : v => v == 'ArrowUp',
        isEscape : v => v == 'Escape',
        isNumpad0 : v => v == 'Numpad0',
        isNumpad1 : v => v == 'Numpad1',
        isNumpad2 : v => v == 'Numpad2',
        isNumpad3 : v => v == 'Numpad3',
        isNumpad4 : v => v == 'Numpad4',
        isNumpad5 : v => v == 'Numpad5',
        isNumpad6 : v => v == 'Numpad6',
        isNumpad7 : v => v == 'Numpad7',
        isNumpad8 : v => v == 'Numpad8',
        isNumpad9 : v => v == 'Numpad9',
        isDigit0 : v => v == 'Digit0',
        isDigit1 : v => v == 'Digit1',
        isDigit2 : v => v == 'Digit2',
        isDigit3 : v => v == 'Digit3',
        isDigit4 : v => v == 'Digit4',
        isDigit5 : v => v == 'Digit5',
        isDigit6 : v => v == 'Digit6',
        isDigit7 : v => v == 'Digit7',
        isDigit8 : v => v == 'Digit8',
        isDigit9 : v => v == 'Digit9'
    }
};

const EventKey = {};

for (let key in KeyMapper.KeyCode) {
    if (key !== undefined && key !== null) {
        EventKey[key] = e => {
            if (e.code !== undefined) {
                return KeyMapper.Code[key](e.code);
            } else {
                return KeyMapper.KeyCode[key](e.keyCode);
            }
        };
    }
}

export default EventKey;