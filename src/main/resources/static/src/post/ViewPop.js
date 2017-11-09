import React from 'react';
import $ from 'jquery';
import AbstractPop from '../module/AbstractPop';
import ImageViewer from '../module/ImageViewer';
import { MdCreate } from 'react-icons/lib/md';
import { Modal, Panel, ButtonToolbar, ButtonGroup, Button } from 'react-bootstrap';
import { nTobrJsx } from '../util/Util';

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
        if (this.imageViewer) {
            console.log('image viewer clear');
            this.imageViewer.clear();
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
            attaches = (post.attachInfo||[]).map(info => info.url),
            existAttach = attaches.length > 0;

        if (existAttach) {
            this.imageViewer.add(...attaches);
            this.imageViewer.show();
        } else {
            this.imageViewer.hide();
        }

        post.message = nTobrJsx(post.message);

        if (!post.owner) {
            editStyle = { display : 'none' }
        }

        this.setState({
            post : post,
            editStyle: editStyle,
            profile : this.renderProfile(post)
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

    renderProfile(post) {
        console.log(`renderProfile`);
        return (
            <div className="profile box">
                <span className="px-pr-5">
                    <img className="profile img" src={post.user.thumbnailImage} onClick={this.profileClick.bind(this)}></img>
                </span>
                {post.user.nickname}
            </div>
        );
    }

    renderContent() {
        return (
            <Modal.Body style={{padding: '0'}}>
                <div>
                    { this.state.profile }
                    <ImageViewer ref={ref => this.imageViewer = ref} />
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