import React, {Component} from 'react';
import { Modal } from 'react-bootstrap';
import AbstractPop from "./AbstractPop";

export default class LoginPop extends AbstractPop {
    constructor(props) {
        super(props);
    }

    renderContent() {
        return (
            <Modal.Body>
                <div>
                    <img src="/img/login/kakao.png" className="btn" onClick={this.kakaoLogin}/>
                </div>
            </Modal.Body>
        );
    }

    getDialogClassName() {
        return 'login-pop-container';
    }

    kakaoLogin() {
        location.href = "/login/kakao";
    }

}