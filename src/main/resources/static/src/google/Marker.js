import $ from 'jquery';

export default class Marker {
    constructor(googleMaps, map, options) {
        this.map = map;
        this.lib = googleMaps;
        this.options = $.extend({
            animation : this.lib.Animation.DROP,
            icon : undefined,
            iconImg : {
                enabled : '/img/marker1.png',
                disabled : '/img/marker2.png'
            },
            iconSize : { w : 60, h : 60 }
        }, options);

        let _self = this,
            center = this.map.getCenter(),
            events = this.getEvent();

        this.marker = new this.lib.Marker({
            map: this.map,
            draggable: true,
            animation: this.options.animation,
            icon : this.getIcon(true),
            position : {lat: center.lat(),lng: center.lng()}
        });

        Object.keys(events).forEach((name) => {
            _self.marker.addListener(name, events[name].bind(_self));
        });
    }

    getEvent() {
        return {
            click : () => {
                this.disdraggable();
                (this.options.listener.complete || (()=>{}))();
            },
            dragend : () => {
                (this.options.listener.dragend || (()=>{}))(this.marker.getPosition(), this);
            }
        };
    }

    getIcon(enabled) {
        if (typeof this.options.icon === 'function') {
            return this.options.icon(enabled);
        } else if ((this.options.icon||{}).constructor == this.lib.Size) {
            return this.options.icon;
        } else {
            return {
                url : enabled? this.options.iconImg.enabled : this.options.iconImg.disabled,
                scaledSize : new google.maps.Size(this.options.iconSize.w, this.options.iconSize.h)
            };
        }
    }

    disdraggable() {
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