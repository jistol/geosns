import React, { Component } from 'react';
import ImgViewer from './ImgViewer';
import MdCancel from 'react-icons/lib/md/cancel';

export default class MultipartManager extends Component {
    constructor(props) {
        super(props);
        this.state = {
            closeStyle : Object.assign({display:'none'}, this.props.close)
        };
    }

    componentDidMount() {
        console.log(`MultipartManager.componentDidMount`);
        this.init();
    }

    init() {
        this.setState({
            closeStyle : Object.assign({display:'none'}, this.props.close)
        });

        this.fileSize = 0;
    }

    render() {
        return (
            <div className="multipart-manager-container" style={this.props.container}>
                <MdCancel style={this.state.closeStyle} className="react-icon sm" onClick={this.onClose().bind(this)}/>
                <ImgViewer ref={ref => this.imgViewer = ref} defaultImage={this.props.defaultImage}/>
            </div>
        );
    }

    addAttachInfo(attachInfo, onError) {
        let infos = attachInfo || [];
        if (infos.length > 0) {
            this.add(infos, onError);
        }
    }

    addFile(files, onError) {
        let infos = [];
        for (let i=0,ilen=files.length ; i<ilen ; i++) {
            infos.push({ file: files[i], size: files[i].size, url: URL.createObjectURL(files[i]), deltaX: 0, deltaY: 0 });
        }

        if (infos.length > 0) {
            this.add(infos, onError);
        }
    }

    add(infos, onError) {
        console.log(`MultipartManager.add infos.length : ${infos.length}`);
        let { maxFileSize } = this.props, { closeStyle } = this.state;
        for (let i=0,ilen=infos.length ; i<ilen ; i++) {
            let info = infos[i];

            // validation
            if (maxFileSize && maxFileSize > 0 && this.fileSize + info.size > maxFileSize) {
                onError(-1, 'exceed file size limit.');
                return;
            }

            this.fileSize += info.size;
            this.imgViewer.add(info);
        }

        closeStyle.display = '';
        this.setState({
            closeStyle : closeStyle
        });
        this.imgViewer.slideToLast();
    }

    onClose() {
        return () => {
            let idx = this.imgViewer.getCurrentIndex();
            this.props.onClose(this.imgViewer.getCurrentImage());
            //this.imgViewer.getSize() > 1? this.removeImage(idx) : this.init();
            this.removeImage(idx);
        }
    }

    removeImage(idx) {
        let self = this;
        this.imgViewer.remove(idx).forEach(info => self.fileSize -= info.size);
        if (this.imgViewer.isEmpty()) {
            this.init();
        }
    }

    appendAll(formData, { fileName, metaName, idName } = { fileName : 'files', metaName : 'metas', idName : 'attachIds' }) {
        let getMeta = (type, key, {deltaX, deltaY}) => {
            return JSON.stringify({type:type, key: key, deltaX:deltaX,deltaY:deltaY});
        }, fileSeq = 0;

        this.imgViewer.getImage().forEach(image => {
            let info = image.data;
            if (info.id) {
                formData.append(idName, info.id);
                formData.append(metaName, getMeta('id', info.id, image));
            } else if (info.file && info.file != null) {
                formData.append(fileName, info.file);
                formData.append(metaName, getMeta('file', fileSeq++, image));
            }
        });
    }
}

MultipartManager.defaultProps = {
    container: {
        height: '300px',
        display: 'block',
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

