
export default class GeoWatcher {
    constructor() {
        this.events = [];
        this.watchId = undefined;
        this.lat = 0;
        this.lng = 0;
    }

    getPosition() {
        return { lat : this.lat, lng : this.lng };
    }

    start() {
        if (this.watchId) { return; }

        let _self = this;
        this.watchId = navigator.geolocation.watchPosition(
            (position) => {
                this.lat = position.coords.latitude;
                this.lng = position.coords.longitude;

                this.events.forEach((event) => {
                    if (typeof event === 'function') {
                        event(_self.lat, _self.lng);
                    }
                });
            },
        );
    }

    stop() {
        navigator.geolocation.clearWatch(this.watchId);
        this.watchId = undefined;
    }

    addListener(event) {
        this.events.push(event);
    }
}

