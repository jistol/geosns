'use strict';

import $ from 'jquery';
import Map from '../google/Map';
import React from 'react';
import ReactDOM from 'react-dom';
import '../css/map.scss';

ReactDOM.render(
    <Map login={ $('#login').text() == 'Y' }/>,
    //document.getElementById('root')
    $('#root')[0]
);