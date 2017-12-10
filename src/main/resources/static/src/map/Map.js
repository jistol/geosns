import React, {Component} from 'react';
import $ from 'jquery';
import Marker from './Marker';
import LoginPop from '../module/LoginPop';
import PostInsertPop from './post/PostInsertPop';
import PostUpdatePop from './post/PostUpdatePop';
import ViewPop from './post/ViewPop';
import ListPop from './post/ListPop';
import User from './User';
import PostLoader from './post/PostLoader';
import MapMenu from "./MapMenu";
import {MdAdd} from "react-icons/lib/md/index";

export default class Map extends Component {
    constructor(...args) {
        super(...args);
        this.isInit = false;
        this.doDrag = false;
        this.postLoader = undefined;
        this.watcher = this.props.watcher;
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
            this.watcher.addListener('position', (lat, lng) => {
                self.map.panTo({ lat : lat, lng : lng });
            });
            this.watcher.addListener('watch', (lat, lng) => {
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

    onMapMenuClick(e, btnType) {
        switch(btnType) {
            case 'myLocation' : this.curTargetClick(); break;
            case 'login' : this.openPop('login')(); break;
        }
    }

    render() {
        if (!navigator.geolocation) {
            return (
                <div/>
            );
        } else {
            return (
                <div className="full">
                    <div ref={node => this.target = node} className="full"></div>
                    <MdAdd className="react-icon-menu fixed rb-10" onClick={this.getIfLogin(this.addClick).bind(this)}/>
                    <MapMenu rightTop ref={menu => this.menu = menu} container={this} onClick={this.onMapMenuClick.bind(this)} login={this.props.login}/>
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
        this.target.innerHTML = "loading...";
        if (!this.isLoadScript()) {
            this.loadMap();
        } else {
            this.loadComplete();
        }
    }

    moveCenter() {
        this.map.panTo(this.watcher.getPosition());
    }

    isLoadScript() {
        let scripts = $('script');
        for (let i=0,ilen=scripts.length ; i<ilen ; i++) {
            let script = scripts[i];
            console.log(`script.src = ${script.src}`);
            if (script.src.indexOf(`https://maps.googleapis.com`) != -1) {
                console.log(`check isLoadScript - result : true`);
                return true;
            }
        }
        console.log(`check isLoadScript - result : false`);
        return false;
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

        this.moveCenter();

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
                self.menu.hide();
            },
            dragend : () => {
                self.doDrag = true;
                self.menu.hide();
            },
            tilesloaded : () => {
                let center = this.map.getCenter();
                console.log(`event - tilesloaded - lat : ${center.lat()}, lng : ${center.lng()}`);
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
            let opts = options || {};
            opts.onHide = () => self._pop[name].close();
            self._pop[name].open(opts);
        }).bind(this);
    }
}

Map.defaultProps = {
    callback : '__googleMap_geosns_callback__',
    zoom : 18,
    language : 'ko_KR'
};
