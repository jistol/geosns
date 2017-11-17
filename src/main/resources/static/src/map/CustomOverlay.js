
export default class CustomOverlay {
    constructor(lib, map, options) {
        this.lib = lib;
        this.map = map;
        this.options = Object.assign({
            level : 'floatPane',
            width: '60px',
            height: '60px'
        }, options);
        this.div = null;
        this.overlay = this.getOverlay();
        this.create();
    }

    getOverlay() {
        let self = this,
            Overlay = function(){};

        Overlay.prototype = new this.lib.OverlayView();
        Overlay.prototype.onAdd = function() {
            let div = document.createElement('div'),
                elem = null;

            div.style.borderStyle = 'none';
            div.style.borderWidth = '0px';
            div.style.position = 'absolute';

            if (self.options.icon) {
                elem = (typeof self.options.icon === 'function')? self.options.icon() : self.options.icon;
            } else if (self.options.url) {
                elem = document.createElement('img');
                elem.src = self.options.url;
                elem.style.width = '100%';
                elem.style.height = '100%';
                elem.style.position = 'absolute';
            }

            if (self.options.className) {
                elem.className = className;
            }

            if (self.options.style) {
                Object.entries(self.options.style).forEach(kv => elem.style[kv[0]] = kv[1]);
            }

            if (elem && elem != null) {
                div.appendChild(elem);
            }

            self.div = div;
            this.getPanes()[self.options.level].appendChild(div);
        };

        Overlay.prototype.pxToNum = function(px) {
            return Number(px.replace('px', ''));
        };

        Overlay.prototype.draw = function() {
            let overlayProjection = this.getProjection(),
                sw = overlayProjection.fromLatLngToDivPixel(self.bounds.getSouthWest()),
                ne = overlayProjection.fromLatLngToDivPixel(self.bounds.getNorthEast()),
                div = self.div;

            div.style.left = (sw.x - (this.pxToNum(self.options.width)/2)) + 'px';
            div.style.top = (ne.y - (this.pxToNum(self.options.height)/2)) + 'px';
            div.style.width = self.options.width;
            div.style.height = self.options.height;
        };

        Overlay.prototype.onRemove = function() {
            self.div.parentNode.removeChild(self.div);
            self.div = null;
        };
        return new Overlay();
    }

    getLatlng(lat, lng) {
        let latlng = new this.lib.LatLng({lat: lat, lng: lng});
        return new this.lib.LatLngBounds(latlng, latlng);
    }

    remove() {
        this.overlay.setMap(null);
    }

    create() {
        let center = this.map.getCenter(),
            bounds = this.getLatlng(center.lat(), center.lng());
        this.bounds = bounds;
        this.overlay.setMap(this.map);
    }

    hide() {
        if (this.div) {
            this.div.style.visibility = 'hidden';
        }
    };

    show() {
        if (this.div) {
            this.div.style.visibility = 'visible';
        }
    };

    move(lat, lng) {
        let bounds = this.getLatlng(lat, lng);
        this.bounds = bounds;
        this.overlay.draw();
    }
}