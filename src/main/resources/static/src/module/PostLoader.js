import $ from 'jquery';
import 'js-marker-clusterer';
import Marker from '../google/Marker.js'
import { containLatLng } from "../util/Util"

export default class PostLoader {
    constructor(lib, map) {
        this.id = PostLoader.id++;
        this.lib = lib;
        this.map = map;
        this.center = undefined;
        this.posts = {};
        this.init = false;

        this.clusterOption = {
            //imagePath: '/img/cluster/marker',
            zoomOnClick: false,
            gridSize: 50,
            styles : [
                { url: '/img/cluster/marker1.png', width: 50, height: 50, anchor : [-14], textColor: '#F99' },
                { url: '/img/cluster/marker2.png', width: 50, height: 50, anchor : [-14], textColor: '#F77' },
                { url: '/img/cluster/marker3.png', width: 50, height: 50, anchor : [-14], textColor: '#F55' },
                { url: '/img/cluster/marker4.png', width: 50, height: 50, anchor : [-14], textColor: '#F33' },
                { url: '/img/cluster/marker5.png', width: 50, height: 50, anchor : [-14], textColor: '#F00' }
            ]
        };
        this.cluster = new MarkerClusterer(map, [], this.clusterOption);
        this.lib.event.addListener(this.cluster, 'clusterclick', (cluster) => {
            console.log(`click cluster : ${cluster.getMarkers().length}`)
        });
    }

    isBreakBounds(latlngBound) {
        let latDist = Math.abs(this.center.lat - latlngBound.lat) ,
            lngDist = this.calLng(this.center.lng, latlngBound.lng);

        return latlngBound.width < latDist || latlngBound.height < lngDist || !this.init;
    }

    calLng(a, b) {
        let absA = Math.abs(a), absB = Math.abs(b);
        if (absA > 100 && absB > 100 && Math.abs(absA - absB) != Math.abs(a - b)) {
            return (180 - Math.abs(a)) + (180 - Math.abs(b));
        } else {
            return Math.abs(a-b);
        }
    }

    getLng(lng, distance) {
        let p = lng + distance;

        if (Math.abs(p) > 180) {
            let p2 = Math.abs(p) - 180;
            return p < 0 ? p2 : -p2;
        } else {
            return p;
        }
    }

    getLatLngBound() {
        let bounds = this.map.getBounds(),
            ne = bounds.getNorthEast(),
            sw = bounds.getSouthWest(),
            center = this.map.getCenter(),
            width = this.calLng(sw.lng(), ne.lng()),
            height = Math.abs(ne.lat() - sw.lat());

        return {
            lat: center.lat(),
            lng: center.lng(),
            moveLat : this.calLng(this.center.lat, center.lat()),
            moveLng : this.calLng(this.center.lng, center.lng()),
            west: this.getLng(sw.lng(), -width),
            east: this.getLng(ne.lng(), width),
            north: ne.lat() + (height / 2),
            south: sw.lat() - (height / 2),
            width: width,
            height: height
        };
    }

    isRequiredInit() {
        let { moveLat, moveLng, width, height } = this.getLatLngBound();
        return !this.init || moveLat > width || moveLng > height;
    }

    loadPosting() {
        if (!this.map) {
            return;
        }

        if (!this.center) {
            let center = this.map.getCenter();
            this.center = { lat: center.lat(), lng: center.lng() };
        }

        let bound = this.getLatLngBound();
        console.log(`[${this.id}]loadPosting ${$.param(bound)}`);
        if (!this.isBreakBounds(bound)) {
            return;
        }

        console.log(`[${this.id}]do loadPosting`);

        this.center = { lat : bound.lat, lng : bound.lng };
        $.ajax({
            url: '/rest/map/post',
            type: 'get',
            data: $.param(bound),
            processData: false,
            contentType: false,
            success: this.success.bind(this),
            error: this.error.bind(this),
            complete: this.complete.bind(this)
        });
    }

    showPost(post) {
        let marker = new Marker(google.maps, this.map, {
            listener : {
                click : (pos, marker) => {
                    console.log(`click marker lat : ${pos.lat()}, lng : ${pos.lng()}, marker id : ${marker.id}`);
                }
            },
            initLat : post.lat,
            initLng : post.lng,
            isEditable : false,
            postId : post.id
        });

        console.log(`post show lat : ${post.lat}, lng : ${post.lng}, marker : ${marker}`);
        return marker;
    }

    isExist(lat, lng, postId) {
        let key = JSON.stringify({ lat:lat, lng:lng}),
            posts = this.posts[key];
        if(posts && Array.isArray(posts)) {
            for (let i=0,ilen=posts.length ; i<ilen ; i++) {
                if (posts[i].options.postId == postId) {
                    return true;
                }
            }
        }

        return false;
    }

    addMarker(lat, lng, marker) {
        let key = JSON.stringify({ lat:lat, lng:lng}),
            arr = (this.posts[key]||[]);
        arr.push(marker);
        this.cluster.addMarker(marker.marker);
        this.posts[key] = arr;
    }

    removeMarker(lat, lng) {
        let key = JSON.stringify({ lat:lat, lng:lng}),
            markers = (this.posts[key]||[]);

        markers.forEach(m => {
            this.cluster.removeMarker(m.marker);
            m.hide();
        });

        this.posts[key] = [];
    }

    success(result, status, xhr) {
        if (!result.posts || result.posts == null) {
            console.log('post not exist');
            return;
        }

        console.log(`post length : ${result.posts.length}`);

        let self = this,
            { west, east, north, south } = this.getLatLngBound(),
            viewBounds = this.map.getBounds(),
            ne = viewBounds.getNorthEast(),
            sw = viewBounds.getSouthWest();

        // 범위 외 마커를 제거
        Object.entries(this.posts).forEach(kv => {
            let key = JSON.parse(kv[0]),
                {lat, lng} = key;

            console.log(`remove target - lat : ${lat}, lng : ${lng}, west : ${west}, north : ${north}, east : ${east}, south : ${south}`);
            if (!containLatLng(lat, lng, west, north, east, south)) {
                console.log('removed');
                self.removeMarker(lat, lng);
            }
        });

        // 현재 view에 해당되지 않는 post만 추출후 나머지 생성
        // 초기화 시에는 현재 위치의 view도 보여줌
        result.posts.forEach(post => {
            let lat = post.lat,
                lng = post.lng,
                exclude = this.isRequiredInit() && containLatLng(lat, lng, sw.lng(), ne.lat(), ne.lng(), sw.lat()),
                isInArea = containLatLng(lat, lng, west, north, east, south);

            console.log(`exclude : ${exclude}, isInArea : ${isInArea}, self.isExist(${lat}, ${lng}, ${post.id}) : ${self.isExist(lat, lng, post.id)}`);

            if (!exclude && isInArea && !self.isExist(lat, lng, post.id)) {
                let marker = self.showPost(post);
                self.addMarker(lat, lng, marker);
            }
        });

        this.init = true;
    }

    error(xhr,status,error) {
        console.log(`postloader error - ${error}`);
    }

    complete() {
        console.log(`complete and release load post`);
    }
}

PostLoader.id = 1;
