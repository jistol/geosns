import React, { Component } from 'react';
import AlignDiv from './AlignDiv';
import ImageGallery from 'react-image-gallery';
import "react-image-gallery/styles/css/image-gallery.css";

export default class MultipartManager extends Component {
    constructor(props) {
        super(props);

        this.state = {
            images: [ this.props.defaultImage ]
        };

        this.fileMap = {};
        this.isDefault = true;
        this.fileSize = 0;
    }

    render() {
        let { container } = this.props.style;
        return (
            <div className="multipart-manager-container" style={container}>
                <ImageGallery
                    ref={ ref => this._gallery = ref }
                    items={this.state.images}
                    defaultImage={this.props.defaultImage.original}
                    lazyLoad={this.props.lazyLoad}
                    showThumbnails={false}
                />
            </div>
        );
    }

    renderItem(item) {
        let { fileView } = this.props.style;
        return (
            <AlignDiv cellStyle={fileView.item} center>
                <div style={fileView.close} onClick={this.onClose(item.original).bind(this)}>
                    <img style={fileView.closeImg} src="/img/close.png"/>
                </div>
                <img src={item.original} style={fileView.img}/>
            </AlignDiv>
        );
    }


    addFile(files, onError) {
        let { maxFileSize } = this.props,
            { images } = this.state;

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

            this.fileMap[src] = file;
            this.fileSize += file.size;

            images.push({
                original: src,
                thumbnail: src,
                renderItem: this.renderItem.bind(this)
            });
        }

        this.setState({ images : images });
        this._gallery.slideToIndex(images.length - 1);
    }

    onClose(src) {
        return () => {
            this.props.onClose(this.fileMap[src]);
            this.state.images.length > 1? this.removeImage(src) : this.backToDefault();
        }
    }

    removeImage(src) {
        let { images } = this.state,
            idx = images.findIndex((item) => item.original == src);

        images.splice(idx, 1);
        this.setState({ images : images });
        delete this.fileMap[src];
    }

    backToDefault() {
        delete this.fileMap;
        this.fileMap = [];
        this.isDefault = true;
        this.setState({ images : [this.props.defaultImage] });
    }

    appendToFormData(formData, argName) {
        let _self = this;
        Object.keys(_self.fileMap).forEach(key => {
            let file = _self.fileMap[key];
            console.log(`appendToFormData key[${key}]:file[${file.name}]`);
            formData.append(argName, file);
        });
    }

    submit(url, formData, method = 'post', argName = 'attaches') {
        this.appendToFormData(formData, argName);

        fetch(url, {
            method: method,
            body: formData
        }).then(res => {
            if (res.status == 200 || res.status == 201) {
                res.text().then(text => {
                    console.log(`result : ${text}`);
                });
            } else {
                console.error(`error : ${res.statusText}`);
            }
        }).catch(err => console.error(`exception : ${err.message}`));
    }

    submitByForm(formId, argName = 'attaches') {
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
            height: '350px'
        },
        fileView: {
            item : {
                height: '350px'
            },
            close : {
                position: 'fixed',top: '10px',left: '10px',
                cursor: 'pointer'
            },
            closeImg : {
                width: '40px', height: '40px'
            },
            img : {
                maxHeight:'300px',
                maxWidth: '300px',
                width: 'auto',
                height: 'auto'
            }
        }
    },
    defaultImage: {
        original: '/img/defaultImage.gif'
    },
    lazyLoad: true,
    maxFileSize: 31457280,
    onClose: () => {}
};