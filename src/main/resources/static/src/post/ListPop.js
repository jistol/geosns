import React from 'react';
import AbstractPop from "../module/AbstractPop";
import { Modal, ListGroup, ListGroupItem } from "react-bootstrap";

export default class ListPop extends AbstractPop {
    constructor(props) {
        super(props);
        this.state = {
            item : []
        };
    }

    renderItem(post) {
        console.log(`renderItem post:${post.id}`);
        let self = this;
        return (
            <ListGroupItem href="javascript:;" key={post.id} onClick={() => self.onItemClick(post)} style={{margin:'0', padding:'5px'}}>
                <span style={{padding:'5px'}}>
                    <img style={{borderRadius:'50%', maxWidth:'40px', maxHeight: '40px'}} src={post.thumbnailImage}></img>
                </span>
                {post.subject}...
            </ListGroupItem>
        );
    }

    renderContent() {
        console.log(`ListPop.renderContent item.length:${this.state.item.length}`);
        return (
            <Modal.Body style={{padding:'0'}}>
                <div>
                    <ListGroup componentClass="ul" style={{margin:'0', padding:'0', maxHeight:'500px', overflowY:'auto'}}>
                        {this.state.item}
                    </ListGroup>
                </div>
            </Modal.Body>
        );
    }

    beforeOpen({ post }) {
        let self = this;
        this.state.item = [];
        (post||[]).forEach(p => self.addItem(p));
        return true;
    }

    addItem(post) {
        console.log(`addItem item.length:${this.state.item.length}`);
        let { item } = this.state;
        item.push(this.renderItem(post));
        this.setState({ item : item });
    }

    onItemClick(post) {
        this.close();
        this.props.onItemClick(post);
    }

    getDialogClassName() {
        return 'list-pop-container';
    }
}

ListPop.defaultProps = {
    onItemClick : () => {}
};