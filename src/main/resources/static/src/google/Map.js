import React, {Component} from 'react';
import $ from 'jquery';
import Marker from './Marker';
import GeoWatcher from '../module/GeoWatcher';
import LoginPop from '../module/LoginPop';
import PostInsertPop from '../post/PostInsertPop';
import PostUpdatePop from '../post/PostUpdatePop';
import ViewPop from '../post/ViewPop';
import ListPop from '../post/ListPop';
import User from './User';
import PostLoader from '../module/PostLoader';

export default class Map extends Component {
    constructor(...args) {
        super(...args);
        this.isInit = false;
        this.doDrag = false;
        this.postLoader = undefined;
        this.watcher = new GeoWatcher();
        this.user = undefined;
        this.activeMarker = undefined;
        this._pop = {
            login : undefined,
            post : undefined
        };
    }

    init() {
        let self = this;
        if (!this.isInit) {
            this.watcher.stop();
            this.watcher.start();
            this.watcher.addListener((lat, lng) => {
                if (self.isInit) {
                    // 지도 위치가 현재 위치와 같으며 초기화 되어 있는 경우
                    // 계속 현재 이동 위치와 맵의 위치를 동기화 한다.
                    if (!self.doDrag) {
                        self.map.panTo({ lat : lat, lng : lng });
                    }

                    // 사용자 위치 이동
                    if (this.user) {
                        self.user.move(lat, lng);
                    }
                }
            });

            this.user = new User(google.maps, this.map, {});
            this.postLoader = new PostLoader(google.maps, this.map, {
                onClick : this.onMarkerClick.bind(this),
                onClusterClick : this.onClusterClick.bind(this)
            });
        }
        this.isInit = true;
    }

    onItemClick(post) {
        this.openPop('view')({ post: post });
    }

    onEdit(post) {
        console.log(`viewPop edit - post : ${post.id}`);
        this.openPop('postUpdate')({ post: post });
    }

    onMarkerClick(marker) {
        console.log(`marker click - post : ${marker.getPost().id}`);
        this.openPop('view')({ post: marker.getPost() });
    }

    onClusterClick(markers) {
        console.log(`cluster click - marker.length : ${markers.length}`);
        this.openPop('list')({ post: (markers||[]).map(marker => marker.getPost()) });
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
                    <img src="/img/map/add.png" className="btn w-70 b-r-5 fixed" onClick={this.getIfLogin(this.addClick).bind(this)}/>
                    <img src="/img/map/target.png" className="btn w-65 t-l-5 fixed" onClick={this.curTargetClick.bind(this)}/>
                    <LoginPop ref={pop => this._pop.login = pop} />
                    <PostInsertPop ref={pop => this._pop.postInsert = pop} />
                    <PostUpdatePop ref={pop => this._pop.postUpdate = pop} />
                    <ViewPop ref={pop => this._pop.view = pop} onEdit={this.onEdit.bind(this)}/>
                    <ListPop ref={pop => this._pop.list = pop} onItemClick={this.onItemClick.bind(this)} />
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

        window[this.props.callback] = (() => this.loadComplete()).bind(this);
        $.getScript(`/google/map/js?callback=${this.props.callback}`);
    }

    loadComplete() {
        let self = this, events = this.getEvents();
        this.map = new google.maps.Map(this.target, {
            center: this.watcher.getPosition(),
            zoom: this.props.zoom,
            maxZoom: this.props.zoom,
            minZoom: this.props.zoom,
            language: this.props.language,
            scrollwheel: false,
            zoomControl: false,
            streetViewControl: false,
            rotateControl: false,
            mapTypeControl: false,
            fullscreenControl: false,
            disableDoubleClickZoom: true
        });

        Object.keys(events).forEach((name) => {
            self.map.addListener(name, events[name]);
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
                self.removeActiveMarker();
            },
            dragend : () => {
                self.doDrag = true;
            },
            tilesloaded : () => {
                self.init();
                self.postLoader.loadPosting();
            }
        };
    }

    removeActiveMarker() {
        if (this.activeMarker) {
            this.activeMarker.hide();
            this.activeMarker = undefined;
        }
    }

    addClick() {
        this.removeActiveMarker();

        if (!this.isInit) { return; }

        let self = this;
        this.moveCenter();
        this.activeMarker = new Marker(google.maps, this.map, {
            animation: google.maps.Animation.DROP,
            listener : {
                dragend : (pos, marker) => {
                    let listener = {
                        success : ({ post }, status, xhr) => {
                            marker.pinned();
                            marker.addListener('click', () => self.onMarkerClick(marker));
                            marker.setPost(post);
                            self.postLoader.addMarker(pos.lat(), pos.lng(), marker);
                            self.activeMarker = undefined;
                        }
                    };
                    self.openPop('postInsert')({ lat: pos.lat(), lng: pos.lng(), listener: listener });
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
