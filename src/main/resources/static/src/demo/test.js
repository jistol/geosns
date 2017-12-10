import $ from 'jquery'
import React from 'react';
import ReactDOM from 'react-dom';
import EditableInput from "../module/EditableInput";

// $('body').on('keydown', (e) => {
//     let key = '';
//     if (EventKey.isEnter(e)) {
//         key = 'enter';
//     } else if (EventKey.isSpace(e)) {
//         key = 'space'
//     } else {
//         key = 'etc';
//     }
//
//     $('#result').text(key);
// });

const onChange = value => {
    $('#result').text(value);
};

let data = undefined, text = 'init';

ReactDOM.render(
    <EditableInput ref={ref => data = ref} value={text}/>,
    $('#render')[0]
);


setTimeout(() => {
    debugger
    text = 'init2';
}, 3000);
