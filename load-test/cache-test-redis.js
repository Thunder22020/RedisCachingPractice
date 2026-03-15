import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://localhost:8080/products';

export const options = {
    vus: 100,
    duration: '30s',
};

function hotProductId() {
    return Math.floor(Math.random() * 1000) + 1;
}

export default function () {
    const id = hotProductId();

    const res = http.get(`${BASE_URL}/${id}?cacheMode=MANUAL`);

    check(res, {
        'status 200': (r) => r.status === 200,
    });

    sleep(0.01);
}