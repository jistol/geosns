import $ from 'jquery';

export default class Marker {
    constructor(googleMaps, map, options) {
        let self = this,
            center = map.getCenter();

        this.id = Marker.id++;
        this.map = map;
        this.lib = googleMaps;
        this.options = $.extend({
            animation : null,
            icon : undefined,
            iconImg : {
                editable : '/img/marker1.png',
                pinned : '/img/marker2.png'
            },
            iconSize : { w : 60, h : 60 },
            initLat : center.lat(),
            initLng : center.lng(),
            isEditable : true,
            listener : {},
            postId : -1
        }, options);

        this.marker = new this.lib.Marker({
            map: this.map,
            draggable: this.options.isEditable,
            animation: this.options.animation,
            icon : this.getIcon(this.options.isEditable),
            position : {lat: this.options.initLat,lng: this.options.initLng}
        });

        Object.entries(this.options.listener).forEach(kv => {
            self.marker.addListener(kv[0], self.wrapEvent(self, kv[1]));
        });
    }

    wrapEvent(self, event) {
       return (...args) => {
           event(self.marker.getPosition(), self, ...args);
       };
    }

    getIcon(editable) {
        if (typeof this.options.icon === 'function') {
            return this.options.icon(editable);
        } else if ((this.options.icon||{}).constructor == this.lib.Size) {
            return this.options.icon;
        } else {
            return {
                url : editable? this.options.iconImg.editable : this.options.iconImg.pinned,
                scaledSize : new google.maps.Size(this.options.iconSize.w, this.options.iconSize.h)
            };
        }
    }

    pinned() {
        if (!this.marker) { return; }
        this.marker.setDraggable(false);
        this.marker.setIcon(this.getIcon(false));
    }

    show() {
        this.marker.setMap(this.map);
    }

    hide() {
        this.marker.setMap(null);
    }
}

Marker.id = 0;