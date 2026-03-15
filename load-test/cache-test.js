import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://localhost:8080/products';

export const options = {
    vus: 50,
    duration: '30s',
};

function randomProductId() {
    return Math.floor(Math.random() * 10000000) + 1;
}

export default function () {
    const id = randomProductId();

    const url = `${BASE_URL}/${id}?cacheMode=NONE_CACHE`;

    const res = http.get(url);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(0.1);
}