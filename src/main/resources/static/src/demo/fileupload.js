'use strict';

import $ from 'jquery';
import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { Form, Col, FormGroup, FormControl, ControlLabel } from 'react-bootstrap';
import FileInput from '../module/FileInput';
import MultipartManager from '../module/MultipartManager';

class Camera extends Component {
    onChange(e) {
        this._mng.addFile(e.target.files);
    }

    doSubmit() {
        let formData = new FormData(),
            url = '/rest/map/post';
        formData.append('message', document.querySelector('#message'));
        this._mng.submit(url, formData);
    }

    renderForm() {
        return (
            <Form horizontal>
                <FormGroup controlId="message">
                    <Col componentClass={ControlLabel} sm={2}>
                        Message
                    </Col>
                    <Col sm={10}>
                        <FormControl type="textarea"/>
                    </Col>
                </FormGroup>
            </Form>
        );
    }

    render() {
        let images = {
                camera : '/img/camera.png',
                video: '/img/video.png',
                camcorder: '/img/camcorder.png',
                gallery: '/img/gallery.png'
            };

        return (
            <div>
                <MultipartManager ref={ ref => this._mng = ref }/>
                {this.renderForm()}
                <div className="file-inputs">
                    <FileInput capture="camera" accept="image/*" src={images.camera} onChange={this.onChange.bind(this)}/>
                    <FileInput capture="camcorder" accept="video/*" src={images.camcorder} onChange={this.onChange.bind(this)}/>
                    <FileInput accept="image/*" src={images.gallery} onChange={this.onChange.bind(this)} multiple/>
                    <FileInput accept="video/*" src={images.video} onChange={this.onChange.bind(this)} multiple/>
                </div>
                <button name="submit" value="submit" onClick={this.doSubmit.bind(this)}>Submit</button>
            </div>
        );
    }
}

const testFun = () => {
    let map = {},
        key1 = JSON.stringify({a:'a', b:'b'}),
        key2 = JSON.stringify({a:'b', b:'b'});

    map[key1] = 1;
    map[key2] = 2;

    console.log(map[JSON.stringify({a:'a', b:'b'})]);
    console.log(map[JSON.stringify({a:'b', b:'b'})]);

};


ReactDOM.render(
    <Camera/>,
    $('#reactRoot')[0]
);

testFun();

