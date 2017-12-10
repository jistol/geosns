import React, { Component } from 'react';
import $ from 'jquery';
import {MdArrowBack} from "react-icons/lib/md/index";
import {NavLink} from "react-router-dom";
import Profile from "../module/Profile";
import LoginPop from "../module/LoginPop";

export default class Setup extends Component {
    constructor(...args) {
        super(...args);
        this.state = {
            profileImg : '',
            profileNickname : ''
        };
    }

    componentDidMount() {
        let self = this;
        $.get({
            url: '/rest/setup/info',
            success: (result, status, xhr) => {
                let user = result.user;
                self.setState({
                    profileImg : user.thumbnailImage,
                    profileNickname : user.nickname
                });
            },
            error: (xhr,status,error) => {
                console.log(`status : ${xhr.status}`);
            }
        });
    }

    /*success(result, status, xhr) {
    }

    error(xhr,status,error) {
        console.log(`postloader error - ${error}`);
    }

    complete() {
        console.log(`complete and release load post`);
    }*/

    renderHeader() {
        return (
            <div className="fixed lt-0 nav-header">
                <NavLink to="/map" className="item">
                    <MdArrowBack className="react-icon m"/>
                </NavLink>
            </div>
        );
    }

    render() {
        return (
            <div className="px-p-10 px-pt-43">
                {this.renderHeader()}
                <Profile src={this.state.profileImg} nickname={this.state.profileNickname} onChangeNickname={this.onChangeNickname.bind(this)} editable/>
                <LoginPop ref={pop => this.login = pop} />
            </div>
        );
    }

    onChangeNickname(nickname) {
        let self = this;
        $.ajax({
            url: '/rest/setup/user/nickname',
            method: 'put',
            data: $.param({'nickname': nickname}),
            success: (result, status, xhr) => {
                console.log(`nickname update success [${result.code}]`);
            },
            error: (xhr,status,error) => {
                console.log(`status : ${xhr.status}`);
                alert(error);
            }
        });
    }
}