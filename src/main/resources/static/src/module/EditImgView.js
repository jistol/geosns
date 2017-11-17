import React, { Component } from 'react';
import Draggable from 'react-draggable';

export default class EditImgView extends Component {
    constructor(...args) {
        super(...args);
        this.id = EditImgView.id++;
        this.deltaX = this.props.deltaX;
        this.deltaY = this.props.deltaY;
        this.state = {
            bounds : {
                top: 0,
                bottom: 0,
                left: 0,
                right: 0
            },
            position : {x : this.props.deltaX, y : this.props.deltaY},
            disabled: this.props.disabled
        };
        this.src = this.props.src;
    }

    render() {
        console.log(`EditImgView.render`);
        let style = {
                width : `${this.props.width}px`,
                height : `${this.props.height}px`,
                overflow : 'hidden',
                margin: '0 auto'
            };
        try {
            console.log(`do render`);
            return (
                <div style={style}>
                    <Draggable ref={ref => this.draggable = ref} handle={`.handle${this.id}`} defaultPosition={this.state.position} bounds={this.state.bounds} disabled={this.state.disabled} onDrag={this.onDrag.bind(this)} onStop={this.onStop.bind(this)}>
                        <img style={{width:'auto'}} className={`handle${this.id}`} src={this.src} onLoad={this.onLoad.bind(this)}/>
                    </Draggable>
                </div>
            );
        } catch(e) {
            console.log(e.message);
            throw e;
        }

    }

    onLoad(e) {
        let img = e.target,
            { width, height } = this.props;

        console.log(`img.w : ${img.width}, img.h : ${img.height}`);

        if (img.width < width && img.height < height) {
            this.deltaX = this.deltaY = 0;
            this.setState({
                bounds : {
                    top : (height - img.height) / 2,
                    left : (width - img.width) / 2,
                    right : 0, bottom : 0
                },
                position : { x:0, y:0 },
                disabled : true
            });
        } else {
            let scaleW = width / img.width,
                scaleH = height / img.height,
                scale = Math.max(scaleW, scaleH),
                imgW = img.width * scale,
                imgH = img.height * scale;

            img.width = imgW;
            img.height = imgH;

            this.setState({
                bounds : {
                    top : Math.min(height - img.height, 0),
                    left : Math.min(width - img.width, 0),
                    right : 0, bottom : 0
                }
            });
        }
    }

    onDrag(e, ui) {
        this.deltaX += ui.deltaX;
        this.deltaY += ui.deltaY;
        this.props.onDrag(this.deltaX, this.deltaY, e, ui);
    }

    onStop(e, ui) {
        this.props.onStop(this.deltaX, this.deltaY, e, ui);
    }
}

EditImgView.defaultProps = {
    width : 300,
    height : 300,
    disabled : false,
    deltaX : 0,
    deltaY : 0,
    src : '',
    onDrag : () => {},
    onStop : () => {}
};

EditImgView.id = 0;