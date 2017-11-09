import React from 'react';

const safeFx = (fx) => fx || (()=>{}),
      safeObj = (obj) => obj || {},
      safeArr = (arr) => arr || [];

const containLatLng = (lat, lng, w, n, e, s) => {
    return lat <= n && lat >= s && w <= lng && e >= lng;
};

const nTobrJsx = (txt) => txt.split('\n').map((item, key) => (<span key={key}>{item}<br/></span>));

export { safeFx, safeObj, safeArr, containLatLng, nTobrJsx };