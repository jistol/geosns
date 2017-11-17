'use strict';

import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import EditImgView from '../module/EditImgView';
import './demo.scss';

class App extends Component {
    constructor(...args) {
        super(...args);
        this.src = 'http://image.itdonga.com/files/2015/10/02/a0mk1.jpg'
    }

    onDrag(x, y, e, ui) {
        console.log(`onDrag x : ${x}, y : ${y}`);
        //this.log.innerHTML = `x : ${x}, y : ${y}`;
    }

    onStop(x, y, e, ui) {
        console.log(`onStop x : ${x}, y : ${y}`);
        ReactDOM.unmountComponentAtNode(document.getElementById("result"));
        ReactDOM.render(<App disabled={true} deltaX={x} deltaY={y}/>, document.getElementById('result'));
    }

    render() {
        return (
            <EditImgView width={300} height={300} disabled={this.props.disabled} src={this.src} deltaX={this.props.deltaX} deltaY={this.props.deltaY} onDrag={this.onDrag.bind(this)} onStop={this.onStop.bind(this)}/>
        );
    }
}

App.defaultProps = {
    disabled : false,
    deltaX : 0,
    deltaY : 0
};

ReactDOM.render(<App deltaX={-100}/>, document.getElementById('view'));
