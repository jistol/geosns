import React, { Component } from 'react';

export default class FileInput extends Component {
    constructor(props) {
        super(props);
    }

    renderIcon({src, imgWidth, imgHeight, cursor}) {
        return (<img src={src} style={{width: imgWidth, height: imgHeight, cursor: cursor}}/>);
    }

    render() {
        let { name, capture, accept, className, onChange, onClick } = this.props;
        return (
            <label className={className}>
                {
                    (this.props.icon || this.renderIcon)(this.props)
                }
                <input name={name} type="file" style={{display:'none'}} accept={accept} capture={capture} onChange={onChange} onClick={onClick} multiple={this.props.multiple}/>
            </label>
        );
    }
}

FileInput.defaultProps = {
    name : 'fileInput',
    imgWidth: '32px',
    imgHeight: '32px',
    cursor: 'pointer',
    accept: 'image/*',
    className: 'file-input'
};