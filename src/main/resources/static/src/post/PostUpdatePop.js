import React from 'react';
import PostInsertPop from "./PostInsertPop";
import $ from 'jquery';
import "react-image-gallery/styles/css/image-gallery.css";

export default class PostUpdatePop extends PostInsertPop {
    constructor(props) {
        super(props);
        this.state = { formGroupState: null };
        this.post = {};
    }

    beforeOpen(options) {
        if (!options || !options.post || !options.post.id) {
            console.log('PostUpdatePop.load - postId is null');
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
        let post = (result.post || {});
        this._mng.addAttachInfo(post.attachInfo, e => console.log(e.message));
        $('#message').val(post.message);
        this.scopeSelect.select(post.scope);
    }

    doSubmit() {
        if (!this.validate()) {
            return;
        }

        let formData = this.initFormData(),
            { lat, lng } = this.options.post;
        formData.append('lat', lat);
        formData.append('lng', lng);
        formData.append("id", this.options.post.id);
        this._mng.appendAttachIds('attachIds', formData);
        this._mng.appendFiles('files', formData);
        this.submit(formData, '/rest/map/post', 'put');
    }
}