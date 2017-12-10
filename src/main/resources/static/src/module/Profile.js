import React, {Component} from 'react';
import EditableInput from './EditableInput';

export default class Profile extends Component {
    constructor(props) {
        super(props);
    }

    renderNickname() {
        return (this.props.editable)?
            (<EditableInput value={this.props.nickname} onChange={this.props.onChangeNickname}/>) : this.props.nickname;
    }

    render() {
        return (
            <div className="profile box">
                <span className="px-pr-10">
                    <img className="profile img" src={this.props.src} onClick={this.props.onClick}></img>
                </span>
                <span>
                    {this.renderNickname()}
                </span>
            </div>
        );
    }
}

Profile.defaultProps = {
    onChangeNickname : (nickname) => {}
};