import React, { Component } from 'react';
import $ from 'jquery';
import AlignDiv from './AlignDiv';
import ImageGallery from 'react-image-gallery';
import "react-image-gallery/styles/css/image-gallery.css";

export default class ImageViewer extends Component {
    constructor(props) {
        super(props);

        let { container, fileView } = this.props;
        this.state = {
            images : ( this.props.images || [] ),
            container : $.extend(true, {}, { display : 'table' }, container),
            fileView : fileView
        };
        this._gallery = null;
    }

    renderItem(item) {
        let { fileView } = this.state;
        return (
            <AlignDiv cellStyle={fileView.item} center>
                <img name="test" src={item.original} style={fileView.img}/>
            </AlignDiv>
        );
    }

    renderGallery() {
        return (
            <ImageGallery
                ref={ ref => this._gallery = ref }
                items={this.state.images}
                defaultImage={this.props.defaultImage.original}
                lazyLoad={this.props.lazyLoad}
                showThumbnails={this.props.showThumbnails}
                showFullscreenButton={this.props.showFullscreenButton}
                showPlayButton={this.props.showPlayButton}
            />
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
        this.setState({ container : $.extend(true, {}, this.state.container, {display:'table'}) });
    }

    setImage(images) {
        this.state.images = images;
        this.setState({ images : images });
    }

    add(...urls) {
        let { images } = this.state;
        urls.forEach(src => images.push({ original: src, thumbnail: src, renderItem: this.renderItem.bind(this) }));
        this.setImage(images);
    }

    remove(idx) {
        let { images } = this.state;
        images.splice(idx, 1);
        this.setImage(images);
    }

    clear() {
        let { images } = this.state;
        images = [];
        this.setImage(images);
    }

    slideToLast() {
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

ImageViewer.defaultProps = {
    container: {
        height: '300px',
        margin: '0 auto',
        width: '100%'
    },
    fileView: {
        item : {
            height: '300px',
            width: '300px'
        },
        img : {
            maxHeight:'100%',
            maxWidth: '100%',
            width: 'auto',
            height: 'auto'
        }
    },
    defaultImage: {
        original: '/img/gallery/default.png'
    },
    showThumbnails : false,
    showFullscreenButton : false,
    showPlayButton : false,
    lazyLoad: true
};

