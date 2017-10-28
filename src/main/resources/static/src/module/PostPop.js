import React, { Component } from 'react';
import { Modal, Form, FormGroup, FormControl, Col } from 'react-bootstrap';
import AbstractPop from "./AbstractPop";
import FileInput from './FileInput';
import MultipartManager from './MultipartManager';
import $ from 'jquery';

import "react-image-gallery/styles/css/image-gallery.css";

export default class PostPop extends AbstractPop {
    constructor(props) {
        super(props);
    }

    onChange(e) {
        this._mng.addFile(e.target.files);
    }

    doSubmit() {
        let formData = new FormData(),
            url = '/rest/map/post',
            listener = {
                complete : (xhr, status) => {},
                success : (result, status, xhr) => {

                },
                error : (xhr,status,error) => {

                }
            };
        formData.append('message', $('#message').val());
        this._mng.submit(url, formData, listener);
    }

    renderContent() {
        console.log('do renderContent');

        let images = {
            camera : '/img/camera.png',
            video: '/img/video.png',
            camcorder: '/img/camcorder.png',
            gallery: '/img/gallery.png'
        };

        return (
            <Modal.Body>
                <div>
                    <MultipartManager ref={ ref => this._mng = ref }/>
                    <Form horizontal>
                        <FormGroup controlId="message">
                            <Col sm={10}>
                                <FormControl type="textarea"/>
                            </Col>
                        </FormGroup>
                    </Form>
                    <div className="file-inputs">
                        <FileInput capture="camera" accept="image/*" src={images.camera} onChange={this.onChange.bind(this)}/>
                        <FileInput capture="camcorder" accept="video/*" src={images.camcorder} onChange={this.onChange.bind(this)}/>
                        <FileInput accept="image/*" src={images.gallery} onChange={this.onChange.bind(this)} multiple/>
                        <FileInput accept="video/*" src={images.video} onChange={this.onChange.bind(this)} multiple/>
                    </div>
                    <button name="submit" value="submit" onClick={this.doSubmit.bind(this)}>Submit</button>
                </div>
            </Modal.Body>
        );
    }

    getDialogClassName() {
        return 'post-pop-container';
    }

}