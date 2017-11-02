
const safeFx = (fx) => fx || (()=>{}),
      safeObj = (obj) => obj || {},
      safeArr = (arr) => arr || [];

const containLatLng = (lat, lng, w, n, e, s) => {
    return lat <= n && lat >= s && w <= lng && e >= lng;
};




export { safeFx, safeObj, safeArr, containLatLng };