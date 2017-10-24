import React, { Component } from 'react';
import { Modal, Form, FormGroup, FormControl, Col } from 'react-bootstrap';
import ImageGallery from 'react-image-gallery';
import AbstractPop from "./AbstractPop";

import "react-image-gallery/styles/css/image-gallery.css";

export default class WritePop extends AbstractPop {
    constructor(props) {
        super(props);

        this.state = {
            images : []
        };

        this._imageGallery = undefined;
    }

    renderContent() {
        return (
            <Modal.Body>
                <div id="gallery">
                    <ImageGallery
                        ref={ref => this._imageGallery = ref}
                        items={this.state.images}
                    />
                </div>
            </Modal.Body>
        );
    }

    getDialogClassName() {
        return 'write-pop-container';
    }

}