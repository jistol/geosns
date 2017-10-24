import React, { Component } from 'react';

export default class AlignDiv extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        let { left, right, top, bottom, center, tableStyle, cellStyle } = this.props,
            ts = Object.assign({ display: 'table' }, tableStyle),
            cs = Object.assign({ display: 'table-cell' }, cellStyle);

        if (left || right || top || bottom) {
            cs.position = 'absolute';
        }

        if (left) { cs.left = '0'; }
        if (right) { cs.right = '0'; }
        if (top) { cs.top = '0'; }
        if (bottom) { cs.bottom = '0'; }

        if (center) {
            ts.margin = '0 auto';
            cs.verticalAlign = 'middle';
            cs.textAlign = 'center';
        }

        return (
            <div style={ts}>
                <div style={cs}>
                    {this.props.children}
                </div>
            </div>
        );
    }
}