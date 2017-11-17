import React, { Component } from 'react';
import $ from 'jquery';
import EditImgView from './EditImgView';
import ImageGallery from 'react-image-gallery';
import "react-image-gallery/styles/css/image-gallery.css";

export default class ImgViewer extends Component {
    constructor(props) {
        super(props);

        let { container } = this.props;
        this.state = {
            images : ( this.props.images || [] ),
            container : $.extend(true, {}, { display : 'block' }, container),
        };
        this._gallery = null;
    }

    isEmpty() {
        return !this.state.images || this.state.images.length < 1;
    }

    getImage() {
        return this.state.images;
    }

    getCurrentImage() {
        return this.state.images[this.getCurrentIndex()];
    }

    renderItem(info) {
        console.log(`renderItem info.original : ${info.original}, deltaX: ${info.deltaX}, deltaY: ${info.deltaY}`);

        let { width, height } = this.props,
            onDrag = (x, y, e, ui) => {
                info.deltaX = x;
                info.deltaY = y;
            };
        return (
            <EditImgView width={width} height={height}
                         disabled={this.props.disabled} src={info.original}
                         deltaX={info.deltaX} deltaY={info.deltaY}
                         onDrag={onDrag.bind(this)} />
        );
    }

    renderGallery() {
        let style = {
            width:`${this.props.width}px`,
            height:`${this.props.height}px`,
            backgroundImage:`url('${this.props.defaultImage}')`,
            backgroundRepeat:'no-repeat',
            backgroundPosition: 'center'
        };
        return (
            <div style={style}>
                <ImageGallery
                    ref={ ref => this._gallery = ref }
                    items={this.state.images}
                    lazyLoad={this.props.lazyLoad}
                    showThumbnails={this.props.showThumbnails}
                    showFullscreenButton={this.props.showFullscreenButton}
                    showPlayButton={this.props.showPlayButton}
                    disableSwipe={true}
                    infinite={false}
                    showNav={true}
                />
            </div>
        );
    }

    render() {
        return (
            <div className="image-viewer-container" style={this.state.container}>
                { this.renderGallery() }
            </div>
        );
    }

    hide() {
        this.setState({ container : $.extend(true, {}, this.state.container, {display:'none'}) });
    }

    show() {
        this.setState({ container : $.extend(true, {}, this.state.container, {display:'block'}) });
    }

    setImage(images) {
        this.state.images = images;
        this.setState({ images : images });
    }

    clear() {
        this.setImage([]);
    }

    add(...infos) {
        console.log(`imgViewer.add infos.length : ${infos.length}`);
        let { images } = this.state;
        infos.forEach(info => {
            console.log(`info.url : ${info.url}`);
        });

        infos.forEach(info => images.push({
            original: info.url,
            thumbnail: info.url,
            renderItem: this.renderItem.bind(this),
            deltaX: info.deltaX || 0,
            deltaY: info.deltaY || 0,
            data : info
        }));

        console.log(`img length : ${images.length}`);

        this.setImage(images);
    }

    remove(idx) {
        let { images } = this.state,
            removeImage = images.splice(idx, 1);
        this.setImage(images);
        return removeImage;
    }

    slideToLast() {
        console.log(`slideToLast`);
        let { images } = this.state;
        this._gallery.slideToIndex(images.length - 1);
    }

    getCurrentIndex() {
        return this._gallery.getCurrentIndex();
    }

    getSize() {
        return this.state.images.length;
    }
}

ImgViewer.defaultProps = {
    width: 322,
    height: 300,
    disabled: false,
    container: {
        height: '300px',
        margin: '0 auto',
        width: '100%',
        overflow: 'hidden'
    },
    defaultImage: '/img/gallery/default.png',
    showThumbnails : false,
    showFullscreenButton : false,
    showPlayButton : false,
    lazyLoad: false
};

