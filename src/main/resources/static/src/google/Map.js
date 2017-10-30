import React, {Component} from 'react';
import $ from 'jquery';
import Marker from './Marker';
import GeoWatcher from '../module/GeoWatcher';
import LoginPop from '../module/LoginPop';
import PostPop from '../module/PostPop';
import User from './User';

export default class Map extends Component {
    constructor() {
        super();
        this.isInit = false;
        this.doDrag = false;
        this.watcher = new GeoWatcher();
        this.user = undefined;
        this.activeMarker = undefined;
        this.markers = [];
        this._pop = {
            login : undefined,
            post : undefined
        };
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
                    <LoginPop ref={pop => this._pop.login = pop} />
                    <PostPop ref={pop => this._pop.post = pop} />
                </div>
            );
        }
    }

    componentDidMount() {
        if (!this.map) {
            this.loadMap();
        }
    }

    moveCenter() {
        //this.map.panTo({ lat : this.watcher.lat, lng : this.watcher.lng });
        this.map.panTo(this.watcher.getPosition());
    }

    loadMap() {
        let self = this;
        this.watcher.start();
        this.watcher.addListener((lat, lng) => {
            if (self.isInit) {
                // 지도 위치가 현재 위치와 같으며 초기화 되어 있는 경우
                // 계속 현재 이동 위치와 맵의 위치를 동기화 한다.
                if (!self.doDrag) {
                    self.map.panTo({ lat : lat, lng : lng });
                }

                // 사용자 위치 이동
                if (self.user) {
                    self.user.move(lat, lng);
                }
            }
        });

        window[this.props.callback] = () => self.loadComplete();
        $.getScript(`/google/map/js?callback=${this.props.callback}`);
    }

    loadComplete() {
        let self = this, events = this.getEvents();
        this.map = new google.maps.Map(this.target, {
            center: this.watcher.getPosition(),
            zoom: this.props.zoom,
            language: this.props.language,
            scrollwheel: false,
            zoomControl: false,
            streetViewControl: false,
            rotateControl: false,
            mapTypeControl: false,
            fullscreenControl: false
        });

        Object.keys(events).forEach((name) => {
            self.map.addListener(name, events[name].bind(self));
        });

        // traffic layer
        /*
        // ref : https://developers.google.com/maps/documentation/javascript/examples/layer-traffic?hl=ko
        var trafficLayer = new google.maps.TrafficLayer();
        trafficLayer.setMap(this.map);
        */
    }

    getEvents() {
        let self = this;
        return {
            click : () => {
                self.hideActiveMarker();
            },
            dragend : () => {
                this.doDrag = true;
            },
            tilesloaded : () => {
                if (!this.user) {
                    this.user = new User(google.maps, this.map, {});
                }
                this.isInit = true;
            }
        };
    }

    hideActiveMarker() {
        if (this.activeMarker) {
            this.activeMarker.hide();
            this.activeMarker = undefined;
        }
    }

    addClick() {
        this.hideActiveMarker();

        if (!this.isInit) { return; }

        let self = this;
        this.moveCenter();
        this.activeMarker = new Marker(google.maps, this.map, {
            listener : {
                dragend : (pos, marker) => {
                    let listener = {
                        success : (result, status, xhr) => {
                            marker.disdraggable();
                            self._pop['post'].close();
                            self.activeMarker = undefined;
                        }
                    };
                    self.openPop('post')({ lat: pos.lat(), lng: pos.lng(), listener: listener });
                },
                complete : () => {
                    self.markers.push(self.activeMarker);
                    self.activeMarker = undefined;
                }
            }
        });
    }

    curTargetClick() {
        this.doDrag = false;
        if (this.isInit) {
            this.moveCenter();
        }
    }

    getIfLogin(callback) {
        return this.props.login ? callback : this.openPop('login');
    }

    openPop(name) {
        let self = this;
        return ((options) => {
            options.onHide = () => self._pop[name].close();
            self._pop[name].open(options);
        }).bind(this);
    }
}

Map.defaultProps = {
    callback : '__googleMap_geosns_callback__',
    zoom : 18,
    language : 'ko_KR'
};
