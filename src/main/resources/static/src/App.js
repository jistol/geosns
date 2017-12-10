'use strict';

import $ from 'jquery';
import React, { Component } from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import ReactDOM from 'react-dom';
import Map from './map/Map';
import Setup from "./setup/Setup";
import GeoWatcher from './module/GeoWatcher';
import './css/app.scss';

class App extends Component {
    constructor(...args) {
        super(...args);
        this.watcher = new GeoWatcher();
        this.watcher.start();
    }

    renderMap(props) {
        return (
            <Map {...props} watcher={this.watcher} login={ $('#login').text() == 'Y' }/>
        );
    }

    render() {
        return (
            <BrowserRouter>
                <div className="full">
                    <Switch>
                        <Route exact path="/map" render={this.renderMap.bind(this)}/>
                        <Route path="/setup" component={Setup}/>
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