
export default class GeoWatcher {
    constructor() {
        this.events = {};
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

        let watchSuccess = (position) => {
                console.log(`watchPosition : ${position.coords.latitude}, ${position.coords.longitude}`);
                this.lat = position.coords.latitude;
                this.lng = position.coords.longitude;

                (this.events['watch']||function(){})(_self.lat, _self.lng);
            },
            posSuccess = (position) => {
                console.log(`getCurrentPosition : ${position.coords.latitude}, ${position.coords.longitude}`);
                this.lat = position.coords.latitude;
                this.lng = position.coords.longitude;

                (this.events['position']||function(){})(_self.lat, _self.lng);
            },
            error = (e) => {
                console.log(`watch error - code : ${e.code}, message : ${e.message}`);
            };

        let execute = () => {
            navigator.geolocation.getCurrentPosition(posSuccess, error);
            this.watchId = navigator.geolocation.watchPosition(watchSuccess, error);
        };

        setTimeout(execute, 1000);
    }

    stop() {
        console.log('watcher stop');
        if (this.watchId) {
            navigator.geolocation.clearWatch(this.watchId);
            this.watchId = undefined;
        }
    }

    addListener(name, event) {
        this.events[name] = event;
    }
}

