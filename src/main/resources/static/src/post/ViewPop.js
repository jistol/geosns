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
            post : {}
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
            success: this.success.bind(this),
        });

        return true;
    }

    success(result, status, xhr) {
        let post = (result.post || {}),
            attaches = (post.attachInfo||[]).map(info => info.url),
            existAttach = attaches.length > 0;

        if (existAttach) {
            this.imageViewer.add(...attaches);
            this.imageViewer.show();
        } else {
            this.imageViewer.hide();
        }

        post.message = nTobrJsx(post.message);
        this.setState({ post : post });
    }

    edit() {
        this.props.onEdit(this.getPost());
        this.close();
    }

    renderContent() {
        let panelStyle = {
            border: 0,
            padding: '5px',
            margin: 0,
            maxHeight: '200px',
            overflowY: 'auto'
        };
        return (
            <Modal.Body style={{padding: '0'}}>
                <div>
                    <ImageViewer ref={ref => this.imageViewer = ref} />
                    <Panel style={panelStyle}>
                        { this.state.post.message }
                    </Panel>
                    <ButtonToolbar className="m-5">
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