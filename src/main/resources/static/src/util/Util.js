
const safeFx = (fx) => fx || (()=>{}),
      safeObj = (obj) => obj || {},
      safeArr = (arr) => arr || [];


export { safeFx, safeObj, safeArr };