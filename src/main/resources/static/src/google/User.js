import CustomOverlay from './CustomOverlay';

export default class User {
    constructor(googleMaps, map) {
        this.map = map;
        this.lib = googleMaps;
        let options = {
            url: '/img/map/user.gif',
            style: {
                width: 20,
                height: 20
            }
        };
        this.overlay = new CustomOverlay(googleMaps, map, options);
    }

    move(lat, lng) {
        this.overlay.move(lat, lng);
    }

}