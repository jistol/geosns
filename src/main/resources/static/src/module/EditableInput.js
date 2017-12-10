import React, {Component} from 'react';
import EventKey from '../util/EventKey';

export default class EditableInput extends Component {
    constructor(...args) {
        super(...args);
        this.state = {
            value : this.props.value,
            isEdit : this.props.isEdit
        };
    }

    componentWillReceiveProps(props) {
        console.log(`EditableInput.componentWillReceiveProps : ${props.value}`);
        this.state.value = props.value;
    }

    componentDidUpdate() {
        console.log(`EditableInput.componentDidUpdate : ${this.props.value}`);
        if (this.state.isEdit === true && this.input !== undefined) {
            this.input.focus();
            this.input.select();
        }
    }

    render() {
        return this.state.isEdit? this.renderInput() : this.renderView();
    }

    renderView() {
        this.input = undefined;
        return (
            <span className={this.props.className} onClick={this.onClick.bind(this)}>{this.state.value}</span>
        );
    }

    renderInput() {
        return (
            <span className={this.props.className}>
                <input type="text"
                       ref={ref => this.input = ref}
                       onBlur={this.onBlur.bind(this)}
                       onKeyUp={this.onKeyUp.bind(this)}
                       defaultValue={this.state.value} />
            </span>
        );
    }

    setValue(v) {
        this.setState({ value : v });
    }

    onClick() {
        this.setState({
            isEdit : true
        });
    }

    onBlur(e) {
        if (this.state.value !== e.target.value) {
            this.props.onChange(e.target.value);
        }

        this.setState({
           isEdit : false,
           value : e.target.value
        });
    }

    onKeyUp(e) {
        if (EventKey.isEnter(e)) {
            if (this.state.value !== e.target.value) {
                this.props.onChange(e.target.value);
            }

            this.setState({
                isEdit : false,
                value : e.target.value
            });
        } else if (EventKey.isEscape(e)) {
            this.setState({
                isEdit : false
            });
        }
    }
}

EditableInput.defaultProps = {
    value : '',
    onChange : () => {},
    isEdit : false,
    className : 'editable-input'
}