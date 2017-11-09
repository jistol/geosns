import React, {Component} from 'react';
import { Modal, Button } from 'react-bootstrap';
import AbstractPop from "./AbstractPop";

export default class LoginPop extends AbstractPop {
    constructor(props) {
        super(props);
    }

    renderContent() {
        let self = this;
        return (
            <Modal.Body>
                <div style={{padding:'10px'}}>
                    <Button className="btn login google" bsSize="large" block onClick={() => self.login('google')}>Google로 로그인</Button>
                    <Button className="btn login facebook" bsSize="large" block onClick={() => self.login('facebook')}>Facebook으로 로그인</Button>
                    <Button className="btn login kakao" bsSize="large" block onClick={() => self.login('kakao')}>Kakao로 로그인</Button>
                </div>
            </Modal.Body>
        );
    }

    getDialogClassName() {
        return 'login-pop-container';
    }

    login(social) {
        location.href = `/login/${social}`;
    }

}