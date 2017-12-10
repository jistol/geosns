import React from 'react';
import $ from 'jquery';
import AbstractPop from '../../module/AbstractPop';
import ImgViewer from '../../module/ImgViewer';
import { MdCreate } from 'react-icons/lib/md';
import { Modal, Panel, ButtonToolbar, ButtonGroup, Button } from 'react-bootstrap';
import { nTobrJsx } from '../../util/Util';
import Profile from "../../module/Profile";

export default class ViewPop extends AbstractPop {
    constructor(props) {
        super(props);
        this.state = {
            post : {},
            editStyle : {},
            profile : (<b/>)
        };
    }

    init() {
        $('#message').val('');
        if (this.imgViewer) {
            console.log('image viewer clear');
            this.imgViewer.clear();
        }
        this.setState({ post : {} });
    }


    beforeOpen(options) {
        this.init();

        if (!options || !options.post || !options.post.id) {
            console.log('ViewPop.load - postId is null');
            return false;
        }

        $.ajax({
            url: '/rest/map/post/view',
            type: 'get',
            data: $.param({ id : options.post.id }),
            processData: false,
            contentType: false,
            error: this.error.bind(this),
            success: this.success.bind(this),
        });

        return true;
    }

    error(xhr,status,error) {
        if (xhr.status == 400 && xhr.responseText) {
            let result = JSON.parse(xhr.responseText);
            alert(result.message);
        } else {
            alert("you can't this posting.");
        }

        this.close();
    }

    success(result, status, xhr) {
        let post = (result.post || {}),
            { editStyle } = this.state,
            attaches = post.attachInfo||[],
            existAttach = attaches.length > 0;

        console.log('ViewPop.js attaches : ' + attaches.length);

        if (existAttach) {
            this.imgViewer.add(...attaches);
            this.imgViewer.show();
        } else {
            this.imgViewer.hide();
        }

        post.message = nTobrJsx(post.message);

        if (!post.owner) {
            editStyle = { display : 'none' }
        }

        this.setState({
            post : post,
            editStyle: editStyle,
            profileImg : post.user.thumbnailImage,
            profileNickname : post.user.nickname
        });
    }

    edit() {
        this.props.onEdit(this.getPost());
        this.close();
    }

    profileClick() {
        let { post } = this.state;
        $.ajax('/rest/map/friend/request', {
            type: 'post',
            data: {encId : post.encId}
        });
    }

    renderContent() {
        return (
            <Modal.Body style={{padding: '0'}}>
                <div>
                    <Profile src={this.state.profileImg} nickname={this.state.profileNickname} onClick={this.profileClick.bind(this)}/>
                    <ImgViewer ref={ref => this.imgViewer = ref} disabled={true}/>
                    <Panel style={this.props.msgStyle}>
                        { this.state.post.message }
                    </Panel>
                    <ButtonToolbar style={this.state.editStyle} className="px-m-5">
                        <ButtonGroup style={{float:'right'}} bsSize="xsmall">
                            <Button className="b-0">
                                <MdCreate className="react-icon m" onClick={this.edit.bind(this)}/>
                            </Button>
                        </ButtonGroup>
                    </ButtonToolbar>
                </div>
            </Modal.Body>
        );
    }

    getPost() {
        return this.state.post;
    }

    getDialogClassName() {
        return 'view-pop-container';
    }
}

ViewPop.defaultProps = {
    msgStyle : {
        border: 0,
        padding: '5px',
        margin: 0,
        maxHeight: '200px',
        overflowY: 'auto'
    }
}