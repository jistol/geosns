import React, { Component } from 'react';

export default class FileInput extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        let { name, capture, accept, src, style } = this.props;
        return (
                <label>
                    <img src={src} style={style}/>
                    <input name={name} type="file" style={{display:'none'}} accept={accept} capture={capture} onChange={this.props.onChange} multiple={this.props.multiple}/>
                </label>
        );
    }
}

FileInput.defaultProps = {
    name : 'fileInput',
    style : {
        'width': '64px',
        'height': '64px',
        'cursor': 'pointer'
    },
    accept : 'image/*'
};