import React, {Component} from 'react';
import {Overlay} from "react-bootstrap";
import {MdMenu, MdMyLocation, MdPerson} from "react-icons/lib/md/index";
import {NavLink} from "react-router-dom";

class MapMenuItem extends Component {
    render() {
        let self = this;
        return (
            <ul style={{listStyle:'none'}}>
                <li>
                    <MdMyLocation className={this.props.iconClass} onClick={(e) => self.props.onClick(e,'myLocation')}/>
                </li>
                <li>
                    <NavLink to="/my" className="item">
                        <MdPerson className={this.props.iconClass}/>
                    </NavLink>
                </li>
            </ul>
        );
    }
}

MapMenuItem.defaultProps = {
    iconClass : 'react-icon-menu',
    onClick : () => {}
}

export default class MapMenu extends Component {
    constructor(...args) {
        super(...args);
        this.state = {
            show : false
        };
    }

    render() {
        let style = this.getStyle(),
            iconClass = 'react-icon-menu';

        return (
            <div style={style.container}>
                <MdMenu onClick={this.toggle.bind(this)} style={style.target} className={iconClass}/>
                <Overlay
                    show={this.state.show}
                    onHide={this.hide.bind(this)}
                    placement="bottom"
                    container={this}>
                    <MapMenuItem onClick={this.props.onClick} iconClass={iconClass}/>
                </Overlay>
            </div>
        );
    }

    getStyle() {
        let style = {},
            pos = ['rightTop', 'leftTop', 'rightBottom', 'leftBottom'];

        pos.forEach(p => {
            if (this.props[p]) {
                style = Object.assign(style, this.props.style[p]);
            }
        });

        return style;
    }

    toggle() {
        this.setState({ show: !this.state.show });
    }

    hide() {
        this.setState({ show : false });
    }
}

MapMenu.defaultProps = {
    onClick : ()=>{},
    style : {
        rightTop : {
            container : { position: 'fixed', right : '10px', top : '10px' },
            target : { float: 'right' },
            menu : { marginTop: '5px'}
        },
        leftTop : {
            container : { position: 'fixed', left : '10px', top : '10px' },
            target : { float: 'left' },
            menu : { marginTop: '5px'}
        },
        rightBottom : {
            container : { position: 'fixed', right : '10px', bottom : '10px' },
            target : { float: 'right' },
            menu : { marginBottom: '5px'}
        },
        leftBottom : {
            container : { position: 'fixed', left : '10px', bottom : '10px' },
            target : { float: 'left' },
            menu : { marginBottom: '5px'}
        }
    }
};
