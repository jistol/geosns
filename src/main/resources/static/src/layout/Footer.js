
import React, { Component } from 'react';
import { NavLink } from 'react-router-dom';
import {Button, ButtonGroup} from "react-bootstrap";

class Footer extends Component {
    render() {
        return (
            <div className="header" style={{position:'fixed',bottom:0,left:0}}>
                <ButtonGroup>
                    <NavLink exact to="/map" className="item" activeClassName="active">
                        <Button>
                        </Button>
                    </NavLink>
                    <NavLink to="/setup" className="item">Setup</NavLink>
                </ButtonGroup>
            </div>
        );
    }
}

export default Footer;