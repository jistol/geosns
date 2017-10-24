import $ from 'jquery';

export default class User {
    constructor(googleMaps, map, options) {
        this.map = map;
        this.lib = googleMaps;
        this.options = $.extend({
            animation : google.maps.Animation.DROP,
            icon : undefined,
            iconImg : '/img/user.gif',
            iconSize : { w : 60, h : 60 }
        }, options);

        let center = this.map.getCenter();
        this.marker = new this.lib.Marker({
            map: this.map,
            animation: this.options.animation,
            optimized: false,
            icon : this.getIcon(true),
            position : {lat: center.lat(),lng: center.lng()}
        });
    }

    getIcon(enabled) {
        if (typeof this.options.icon === 'function') {
            return this.options.icon(enabled);
        } else if ((this.options.icon||{}).constructor == this.lib.Size) {
            return this.options.icon;
        } else {
            return {
                url : this.options.iconImg,
                scaledSize : new this.lib.Size(this.options.iconSize.w, this.options.iconSize.h)
            };
        }
    }

    move(lat, lng) {
        if (this.marker) {
            this.marker.setPosition({lat:lat, lng:lng});
        }
    }

}