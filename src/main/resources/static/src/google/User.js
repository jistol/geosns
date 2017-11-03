import $ from 'jquery';
import CustomOverlay from './CustomOverlay';

export default class User {
    constructor(googleMaps, map) {
        this.map = map;
        this.lib = googleMaps;
        let options = {
            url: '/img/map/user.gif',
            style: {
                width: 60,
                height: 60
            }
        };
        this.overlay = new CustomOverlay(googleMaps, map, options);
    }

    move(lat, lng) {
        this.overlay.move(lat, lng);
    }

}