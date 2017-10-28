import React, {Component} from 'react';
import { Modal } from 'react-bootstrap';

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
            <Modal show={this.state.show} onHide={this.props.onHide} dialogClassName={this.getDialogClassName()}>
                {this.renderContent()}
            </Modal>
        );
    }

    open() {
        console.log('abstractPop open');
        this.setState({ show : true });
    }

    close() {
        this.setState({ show : false });
    }
}