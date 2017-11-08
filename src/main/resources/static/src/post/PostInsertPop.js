import React from 'react';
import { Modal, Form, FormGroup, FormControl, InputGroup, Panel, ButtonToolbar, ButtonGroup, Button } from 'react-bootstrap';
import AbstractPop from "../module/AbstractPop";
import FileInput from '../module/FileInput';
import MultipartManager from '../module/MultipartManager';
import ScopeSelect from '../module/ScopeSelect';
import $ from 'jquery';
import MdSend from 'react-icons/lib/md/send';
import MdInsertPhoto from 'react-icons/lib/md/insert-photo';
import MdCameraAlt from 'react-icons/lib/md/camera-alt';

import "react-image-gallery/styles/css/image-gallery.css";

export default class PostInsertPop extends AbstractPop {
    constructor(props) {
        super(props);
        this.state = { formGroupState: null };
    }

    onChange(e) {
        this._mng.addFile(e.target.files, e => console.log(e.message));
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

    onSuccess(result, status, xhr) {
        this.close();
    }

    showError(msg, timeout = 3000) {
        this.setState({ formGroupState: 'error', errorOpen: true, errorMsg: msg });
        setTimeout((() => {
            this.setState({ errorOpen: false, errorMsg: '' });
        }).bind(this), timeout);
    }

    doSubmit() {
        if (!this.validate()) {
            return;
        }

        let formData = this.initFormData(),
            { lat, lng } = this.options;
        formData.append('lat', lat);
        formData.append('lng', lng);
        this._mng.appendFiles('files', formData);
        this.submit(formData, '/rest/map/post', 'post');
    }

    validate() {
        if (!$('#message').val()) {
            this.showError('메시지를 입력하세요.');
            return false;
        }

        return true;
    }

    initFormData() {
        let formData = new FormData(),
            { lat, lng } = this.options;

        formData.append('message', $('#message').val());
        formData.append('scope', $('#scope').val());
        return formData;
    }

    submit(formData, url, method) {
        let self = this,
            listener = (this.options.listener || {}),
            { complete = () => {}, success = () => {}, error = () => {} } = listener;

        $.ajax({
            url : url,
            type : method,
            data : formData,
            processData: false,
            contentType: false,
            complete : (xhr, status) => {
                complete(xhr, status);
            },
            success : (result, status, xhr) => {
                self.onSuccess(result, status, xhr);
                success(result, status, xhr);
            },
            error : (xhr,status,err) => {
                self.onError(xhr, status, err);
                error(xhr,status,err);
            }
        });
    }

    renderContent() {
        let camera = () => (<MdCameraAlt className='react-icon m'/>),
            gallery = () => (<MdInsertPhoto className='react-icon m'/>),
            txGroup = {position:'relative', bottom:0, left:0, right:0, width:'100%'},
            selGroup = {position:'relative', bottom:0, left:0, right:0, width:'100%', padding: '5px'};

        return (
            <Modal.Body style={{padding:'0'}}>
                <div>
                    <MultipartManager ref={ ref => this._mng = ref }/>
                    <Panel id="errorMsg" collapsible expanded={this.state.errorOpen} className="panel-error-msg">{this.state.errorMsg}</Panel>
                    <div style={{minHeight:'100px',maxWidth:'100%', maxHeight:'100%'}}>
                        <Form horizontal>
                            <FormGroup style={{margin: '0 auto'}} validationState={this.state.formGroupState}>
                                <InputGroup style={txGroup}>
                                    <FormControl id="message" componentClass="textarea" placeholder="message here..." className="bg-base" style={{minHeight:'150px'}}/>
                                </InputGroup>
                                <InputGroup style={selGroup}>
                                    <InputGroup.Addon>공개범위</InputGroup.Addon>
                                    <ScopeSelect ref={ref => this.scopeSelect = ref} scopeType={"post"}/>
                                </InputGroup>
                            </FormGroup>
                        </Form>
                    </div>
                    <ButtonToolbar className="m-5">
                        <ButtonGroup style={{float:'left'}} bsSize="xsmall">
                            <Button className="b-0">
                                <FileInput capture="camera" accept="image/*" icon={camera} onChange={this.onChange.bind(this)}/>
                            </Button>
                            <Button className="b-0">
                                <FileInput accept="image/*" icon={gallery} onChange={this.onChange.bind(this)} multiple/>
                            </Button>
                        </ButtonGroup>
                        <ButtonGroup style={{float:'right'}} bsSize="xsmall">
                            <Button className="b-0" onClick={this.doSubmit.bind(this)}>
                                <MdSend className="react-icon m"/>
                            </Button>
                        </ButtonGroup>
                    </ButtonToolbar>
                </div>
            </Modal.Body>
        );
    }

    getDialogClassName() {
        return 'post-pop-container';
    }

    beforeOpen(options) {
        console.log('PostInsertPop.beforeOpen');
        this.setState({ formGroupState: null, errorOpen: false, errorMsg: '' });
        return true;
    }
}