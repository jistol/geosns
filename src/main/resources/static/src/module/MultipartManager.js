import React, { Component } from 'react';
import $ from 'jquery';
import AlignDiv from './AlignDiv';
import MdCancel from 'react-icons/lib/md/cancel';
import ImageGallery from 'react-image-gallery';
import "react-image-gallery/styles/css/image-gallery.css";

export default class MultipartManager extends Component {
    constructor(props) {
        super(props);
        this.state = {
            images: [ this.props.defaultImage ],
            closeStyle: Object.assign(this.props.style.close, {display:'none'})
        };

        this.init();
    }

    init() {
        delete this.fileMap;
        this.fileMap = [];
        this.isDefault = true;
    }

    render() {
        let { container } = this.props.style;
        return (
            <div className="multipart-manager-container" style={container}>
                <MdCancel style={this.state.closeStyle} className="react-icon sm" onClick={this.onClose().bind(this)}/>
                <ImageGallery
                    ref={ ref => this._gallery = ref }
                    items={this.state.images}
                    defaultImage={this.props.defaultImage.original}
                    lazyLoad={this.props.lazyLoad}
                    showThumbnails={false}
                    showFullscreenButton={false}
                    showPlayButton={false}
                />
            </div>
        );
    }

    renderItem(item) {
        let { fileView } = this.props.style;
        return (
            <AlignDiv cellStyle={fileView.item} center>
                <img src={item.original} style={fileView.img}/>
            </AlignDiv>
        );
    }


    addFile(files, onError) {
        let { maxFileSize } = this.props,
            { images, closeStyle } = this.state;

        for (let i=0,ilen=files.length ; i<ilen ; i++) {
            let file = files[i],
                src = URL.createObjectURL(file);

            // validation
            if (this.fileSize + file.size > maxFileSize) {
                onError(-1, 'exceed file size limit.');
                return;
            }

            if (this.isDefault) {
                this.isDefault = false;
                images = [];
                this.fileMap = [];
                this.fileSize = 0;
            }

            this.fileMap.push(file);
            this.fileSize += file.size;

            images.push({
                original: src,
                thumbnail: src,
                renderItem: this.renderItem.bind(this)
            });
        }

        closeStyle.display = '';
        this.setState({
            images : images,
            closeStyle : closeStyle
        });
        this._gallery.slideToIndex(images.length - 1);
    }

    onClose() {
        return () => {
            let idx = this._gallery.getCurrentIndex();
            this.props.onClose(this.fileMap[idx]);
            this.state.images.length > 1? this.removeImage(idx) : this.backToDefault();
        }
    }

    removeImage(idx) {
        let { images } = this.state;

        images.splice(idx, 1);
        this.fileMap.splice(idx, 1);
        this.setState({ images : images });
    }

    backToDefault() {
        let { closeStyle } = this.state;
        closeStyle.display = 'none';
        this.init();
        this.setState({
            images : [this.props.defaultImage],
            closeStyle: closeStyle
        });
    }

    appendToFormData(argName, formData) {
        let _self = this;
        this.fileMap.forEach(file => {
            console.log(`appendToFormData file[${file.name}]`);
            formData.append(argName, file);
        });
    }

    submit(url, formData, { complete = ()=>{}, success = ()=>{}, error = ()=>{} }, method = 'post', argName = 'files') {
        this.appendToFormData(argName, formData);

        console.log(`url : ${url}, method : ${method}`);

        $.ajax({
            url : url,
            type : method,
            data : formData,
            processData: false,
            contentType: false,
            complete : (xhr, status) => {
                console.log(`complete status : ${status}`);
                complete(xhr, status);
            },
            success : (result, status, xhr) => {
                console.log(`success status : ${status}, result : ${result}`);
                success(result, status, xhr);
            },
            error : (xhr,status,err) => {
                console.log(`error status : ${status}, error : ${err}`);
                error(xhr, status, err);
            }
        });
    }

    submitByForm(formId, argName = 'files') {
        let form = document.querySelector(`#${formId}`),
            method = form.getAttribute('method') || 'post',
            action = form.getAttribute('action'),
            formData = new FormData(form);
        this.submit(action, formData, method, argName);
    }
}

MultipartManager.defaultProps = {
    style: {
        container: {
            height: '390px',
            display: 'table',
            margin: '0 auto',
            width: '100%'
        },
        close : {
            position: 'absolute',top: '0',left: '3px', cursor: 'pointer',
            width: '30px', height: '30px', zIndex: '1000', display: 'none'
        },
        fileView: {
            item : {
                height: '390px',
                width: '300px'
            },
            img : {
                maxHeight:'100%',
                maxWidth: '100%',
                width: 'auto',
                height: 'auto'
            }
        }
    },
    defaultImage: {
        original: '/img/gallery/default.png'
    },
    lazyLoad: true,
    maxFileSize: 31457280,
    onClose: () => {}
};