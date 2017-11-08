import React, { Component } from 'react';
import { FormControl } from 'react-bootstrap';

export default class ScopeSelect extends Component {
    constructor(props) {
        super(props);
        this.state = { selectedValue : 'PUB' };
    }

    select(value) {
        let selectObj = this.control;
        selectObj.value = value;
    }

    render() {
        let scopes = this.props.scopes[this.props.scopeType];
        return (
            <FormControl id={this.props.id} componentClass="select" inputRef={ref => this.control = ref} placeholder={this.props.placeholder} defaultValue={this.state.selectedValue}>
                {
                    scopes.map((opt, idx) => (<option key={idx} value={opt.value}>{opt.text}</option>))
                }
            </FormControl>
        );
    }
}

ScopeSelect.defaultProps = {
    id : 'scope',
    scopeType : 'all',
    scopes : {
        post : [
            { value : 'PUB', text : '전체공개' },
            { value : 'FRD', text : '친구공개' },
            { value : 'PRV', text : '나만보기' }
        ],
        all : [
            { value : 'PUB', text : '전체공개' },
            { value : 'FRD', text : '친구공개' },
            { value : 'GRP', text : '그룹공개' },
            { value : 'SUB', text : '하위그룹공개' },
            { value : 'PRV', text : '나만보기' }
        ]
    }
};

