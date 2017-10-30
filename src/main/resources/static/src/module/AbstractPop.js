import React, { Component } from 'react';
import { Modal } from 'react-bootstrap';
import { safeFx } from '../util/Util';

export default class AbstractPop extends Component {
    constructor(props) {
        super(props);
        this.state = {
            show : false
        };
    }

    renderContent() {
        return (
            <Modal.Body>empty</Modal.Body>
        );
    }

    getDialogClassName() {
        return this.props.dialogClassName;
    }

    render() {
        return (
            <Modal show={this.state.show} onHide={this.hide.bind(this)} dialogClassName={this.getDialogClassName()}>
                {this.renderContent()}
            </Modal>
        );
    }

    hide() {
        safeFx(this.options.onHide)();
    }

    open(options) {
        this.setState(Object.assign({show: true}));
        this.options = options || {};
    }

    close() {
        this.setState({ show : false });
    }
}