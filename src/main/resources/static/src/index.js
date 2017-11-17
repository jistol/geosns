'use strict';

import $ from 'jquery';
import Map from './map/Map';
import React, { Component } from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import ReactDOM from 'react-dom';
import Profile from "./setup/Profile";

class App extends Component {
    renderMap(props) {
        return (
            <Map {...props} login={ $('#login').text() == 'Y' }/>
        );
    }

    render() {
        return (
            <BrowserRouter>
                <div className="full">
                    <Switch>
                        <Route exact path="/map" render={this.renderMap.bind(this)}/>
                        <Route path="/my" component={Profile}/>
                    </Switch>
                </div>
            </BrowserRouter>
        );
    }
}

ReactDOM.render(
    <App />,
    $('#root')[0]
);