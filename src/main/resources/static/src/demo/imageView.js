'use strict';

import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import Draggable from 'react-draggable';
import $ from 'jquery';
import './demo.scss';

class App extends Component {
    constructor(...args) {
        super(...args);
        this.state = {
            delta : { x : 0, y : 0 }
        };
        this.url = 'http://image.zdnet.co.kr/2017/10/02/lejj_Lum5eBTRUz1z7t0.jpg';
    }

    onDrag(e, ui) {
        const {x, y} = this.state.delta;
        this.setState({
            delta: {
                x: x + ui.deltaX,
                y: y + ui.deltaY,
            }
        });
    }

    onStop(e, ui) {
        let $result = $('#result'),
            { x, y } = this.state.delta;
        $result.css('background-image', `url(${this.url})`);
        $result.css('background-position', `${x}px ${y}px`);
    }

    render() {
        let { delta }  = this.state,
            style = {
                width:'300px',
                height:'300px',
                backgroundImage:`url(${this.url})`
            },
            vStyle = {
                position:'fixed',
                top:'10px',
                left:'10px'
            };
        return (
            <Draggable handle=".handle" bounds={{top:-100, left:-100, right:0, bottom:0}} onDrag={this.onDrag.bind(this)} onStop={this.onStop.bind(this)}>
                <div className="handle" style={style}>
                    <div style={vStyle}>
                        x: {delta.x.toFixed(0)}, y: {delta.y.toFixed(0)}
                    </div>
                </div>
            </Draggable>

        );
    }
}

ReactDOM.render(<App/>, document.getElementById('view'));