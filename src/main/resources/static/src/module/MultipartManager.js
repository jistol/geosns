import React, { Component } from 'react';
import ImageViewer from './ImageViewer';
import MdCancel from 'react-icons/lib/md/cancel';

export default class MultipartManager extends Component {
    constructor(props) {
        super(props);
        this.state = {
            closeStyle : Object.assign({display:'none'}, this.props.close)
        };
    }

    componentDidMount() {
        this.init();
    }

    init() {
        this.setState({
            closeStyle : Object.assign({display:'none'}, this.props.close)
        });

        this.imageViewer.clear();
        this.imageViewer.add(this.props.defaultImage);

        delete this.infos;
        this.infos = [];
        this.isDefault = true;
        this.fileSize = 0;
    }

    render() {
        return (
            <div className="multipart-manager-container" style={this.props.container}>
                <MdCancel style={this.state.closeStyle} className="react-icon sm" onClick={this.onClose().bind(this)}/>
                <ImageViewer ref={ref => this.imageViewer = ref}/>
            </div>
        );
    }

    addAttachInfo(attachInfo, onError) {
        let infos = (attachInfo || []).map(attach => {
            return { id : attach.id, size : attach.size, src : attach.url };
        });
        if (infos.length > 0) {
            this.add(infos, onError);
        }
    }

    addFile(files, onError) {
        let infos = [];
        for (let i=0,ilen=files.length ; i<ilen ; i++) {
            infos.push({ file : files[i], size : files[i].size, src : URL.createObjectURL(files[i]) });
        }

        if (infos.length > 0) {
            this.add(infos, onError);
        }
    }

    add(infos, onError) {
        let { maxFileSize } = this.props, { closeStyle } = this.state;
        for (let i=0,ilen=infos.length ; i<ilen ; i++) {
            let info = infos[i];

            // validation
            if (maxFileSize && maxFileSize > 0 && this.fileSize + info.size > maxFileSize) {
                onError(-1, 'exceed file size limit.');
                return;
            }

            if (this.isDefault) {
                this.isDefault = false;
                this.imageViewer.clear();
                this.infos = [];
                this.fileSize = 0;
            }

            this.infos.push(info);
            this.fileSize += info.size;
            this.imageViewer.add(info.src);
        }

        closeStyle.display = '';
        this.setState({
            closeStyle : closeStyle
        });
        this.imageViewer.slideToLast();
    }

    onClose() {
        return () => {
            let idx = this.imageViewer.getCurrentIndex();
            this.props.onClose(this.infos[idx]);
            this.imageViewer.getSize() > 1? this.removeImage(idx) : this.init();
        }
    }

    removeImage(idx) {
        this.infos.splice(idx, 1);
        this.imageViewer.remove(idx);
    }

    appendFiles(argName, formData) {
        this.infos
            .filter(info => info.file && info.file != null)
            .forEach(info => {
                formData.append(argName, info.file);
            });
    }

    appendAttachIds(argName, formData) {
        this.infos
            .filter(info => info.id)
            .forEach(info => {
                formData.append(argName, info.id);
            });
    }

}

MultipartManager.defaultProps = {
    container: {
        height: '300px',
        display: 'table',
        margin: '0 auto',
        width: '100%'
    },
    close : {
        position: 'absolute',top: '0',left: '3px', cursor: 'pointer',
        width: '30px', height: '30px', zIndex: '1000', display: 'none'
    },
    defaultImage: '/img/gallery/default.png',
    maxFileSize: 31457280,
    onClose: () => {}
};