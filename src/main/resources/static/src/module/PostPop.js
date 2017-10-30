import React, { Component } from 'react';
import { Modal, Form, FormGroup, FormControl, InputGroup, OverlayTrigger, Panel, Popover } from 'react-bootstrap';
import AbstractPop from "./AbstractPop";
import FileInput from './FileInput';
import MultipartManager from './MultipartManager';
import $ from 'jquery';
import MdSend from 'react-icons/lib/md/send';
import MdAdd from 'react-icons/lib/md/add';
import MdInsertPhoto from 'react-icons/lib/md/insert-photo';
import MdCameraAlt from 'react-icons/lib/md/camera-alt';

import "react-image-gallery/styles/css/image-gallery.css";

export default class PostPop extends AbstractPop {
    constructor(props) {
        super(props);
        this.state = { formGroupState: null };
    }

    onChange(e) {
        this._mng.addFile(e.target.files);
        this._files.hide();
    }

    onError(xhr,status,error) {
        if (xhr.status == 400 && xhr.readyState == 4) {
            let result = JSON.parse(xhr.responseText),
                errors = Object.entries(result.errors || {});

            if (errors.length > 0) {
                switch(errors[0][0]) {
                    case 'message':
                        this.showError('메시지를 입력하세요.');
                        break;
                    default:
                        alert(errors[0][1]);
                }
            }
        }
    }

    showError(msg, timeout = 3000) {
        this.setState({ formGroupState: 'error', errorOpen: true, errorMsg: msg });
        setTimeout((() => {
            this.setState({ errorOpen: false, errorMsg: '' });
        }).bind(this), timeout);
    }

    doSubmit() {
        let formData = new FormData(),
            { lat, lng, listener } = this.options,
            url = '/rest/map/post',
            message = $('#message').val();

        if (!message) {
            this.showError('메시지를 입력하세요.');
            return;
        }

        formData.append('message', message);
        formData.append('lat', lat);
        formData.append('lng', lng);
        this._mng.submit(url, formData, Object.assign(listener, {error : this.onError.bind(this)}));
    }

    renderFiles() {
        let camera = () => (<MdCameraAlt className='react-icon'/>),
            gallery = () => (<MdInsertPhoto className='react-icon'/>);

        return (
            <Popover id="popover-trigger-focus">
                <FileInput capture="camera" accept="image/*" icon={camera} onChange={this.onChange.bind(this)}/>
                <FileInput accept="image/*" icon={gallery} onChange={this.onChange.bind(this)} multiple/>
            </Popover>
        );
    }

    renderContent() {
        let self = this,
            filesClick = () => self._files.handleToggle();

        return (
            <Modal.Body style={{padding:'0'}}>
                <div>
                    <MultipartManager ref={ ref => this._mng = ref }/>
                    <Panel id="errorMsg" collapsible expanded={this.state.errorOpen} className="panel-error-msg">{this.state.errorMsg}</Panel>
                    <div style={{minHeight:'100px',maxWidth:'100%', maxHeight:'100%'}}>
                        <Form horizontal>
                            <FormGroup style={{margin: '0 auto'}} validationState={this.state.formGroupState}>
                                <InputGroup style={{position:'relative', bottom:0, left:0, right:0}}>
                                    <InputGroup.Addon className="pop-btn left" onClick={filesClick}>
                                        <OverlayTrigger ref={ ref => this._files = ref } trigger="click" rootClose placement="top" overlay={this.renderFiles()}>
                                            <MdAdd className="react-icon sm"/>
                                        </OverlayTrigger>
                                    </InputGroup.Addon>
                                    <FormControl id="message" componentClass="textarea" placeholder="message here..." className="bg-base" style={{minHeight:'100px'}}/>
                                    <InputGroup.Addon className="pop-btn right" onClick={this.doSubmit.bind(this)}>
                                        <MdSend className="react-icon sm"/>
                                    </InputGroup.Addon>
                                </InputGroup>
                            </FormGroup>
                        </Form>
                    </div>
                </div>
            </Modal.Body>
        );
    }

    getDialogClassName() {
        return 'post-pop-container';
    }

    open(options) {
        this.setState({ show: true, formGroupState: null, errorOpen: false, errorMsg: '' });
        this.options = options || {};
    }
}