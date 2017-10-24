import React, {Component} from 'react';
import $ from 'jquery';
import Marker from './Marker';
import GeoWatcher from '../module/GeoWatcher';
import LoginPop from '../module/LoginPop';
import User from './User';

export default class Map extends Component {
    constructor() {
        super();
        this.doDrag = false;
        this.watcher = new GeoWatcher();
        this.user = undefined;
        this.activeMarker = undefined;
        this.markers = [];
        this._loginPop = undefined;
    }

    render() {
        if (!navigator.geolocation) {
            return (
                <div/>
            );
        } else {
            return (
                <div className="full">
                    <div ref={node => this.target = node} className="full">loading...</div>
                    <img src="/img/add.png" className="btn w-70 b-r-5 fixed" onClick={this.getIfLogin(this.addClick).bind(this)}/>
                    <img src="/img/curTarget.png" className="btn w-65 t-l-5 fixed" onClick={this.curTargetClick.bind(this)}/>
                    <LoginPop ref={pop => this._loginPop = pop} onHide={this.closeLoginPop.bind(this)}/>
                </div>
            );
        }
    }

    componentDidMount() {
        if (!this.map) {
            this.loadMap();
        }
    }

    isInitMap() {
        return this.map && this.map.constructor == google.maps.Map;
    }

    moveCenter() {
        this.map.panTo({ lat : this.watcher.lat, lng : this.watcher.lng });
    }

    loadMap() {
        let _self = this;
        this.watcher.start();
        this.watcher.addListener((lat, lng) => {
            // 지도 위치가 현재 위치와 같으며 초기화 되어 있는 경우
            // 계속 현재 이동 위치와 맵의 위치를 동기화 한다.
            if (!_self.doDrag && _self.isInitMap()) {
                _self.map.panTo({ lat : lat, lng : lng });
            }

            // 사용자 위치 이동
            if (_self.user) {
                _self.user.move(lat, lng);
            }
        });

        window[this.props.callback] = () => _self.loadComplete();
        $.getScript(`/google/map/js?callback=${this.props.callback}`);
    }

    loadComplete() {
        let _self = this, events = this.getEvents();
        this.map = new google.maps.Map(this.target, {
            zoom: this.props.zoom,
            language: this.props.language,
            zoomControl: false,
            streetViewControl: false,
            rotateControl: false,
            mapTypeControl: false,
            fullscreenControl: false
        });

        Object.keys(events).forEach((name) => {
            _self.map.addListener(name, events[name].bind(_self));
        });

        // traffic layer
        /*
        // ref : https://developers.google.com/maps/documentation/javascript/examples/layer-traffic?hl=ko
        var trafficLayer = new google.maps.TrafficLayer();
        trafficLayer.setMap(this.map);
        */
    }

    getEvents() {
        return {
            click : () => {
                if (this.activeMarker) {
                    this.activeMarker.hide();
                    this.activeMarker = undefined;
                }
            },
            dragend : () => {
                this.doDrag = true;
            },
            tilesloaded : () => {
                if (!this.user) {
                    this.user = new User(google.maps, this.map, {});
                }
            }
        };
    }

    addClick() {
        console.log('do addClick...');
        if (this.activeMarker || !this.map) { return; }
        let _self = this;
        this.moveCenter();
        this.activeMarker = new Marker(google.maps, this.map, {
            listener :
                { complete : () => {
                    _self.markers.push(_self.activeMarker);
                    _self.activeMarker = undefined;
                }
            }
        });
    }

    curTargetClick() {
        this.doDrag = false;
        if (this.isInitMap()) {
            this.moveCenter();
        }
    }

    getIfLogin(callback) {
        console.log(`props.login = ${this.props.login}`);
        return this.props.login ? callback : this.openLoginPop;
    }

    openLoginPop() {
        this._loginPop.open();
    }

    closeLoginPop() {
        this._loginPop.close();
    }
}

Map.defaultProps = {
    callback : '__googleMap_geosns_callback__',
    zoom : 18,
    language : 'ko_KR'
};