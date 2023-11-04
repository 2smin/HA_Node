import http from 'k6/http';
import { sleep } from 'k6';

var count = 0;
export default function () {
    let params = {
        headers: {
            'Content-Type': 'application/json',
            'User-Agent': 'k6',
            'apiKey' : "aaabbb"
        },
        http1: true, // HTTP 1.1 프로토콜 사용
    };
    const httpRes = http.get('http://localhost:8888/', params);

    count++;
    console.log("count : " + count + " res : " + httpRes.body);

}

export const options = {
    vus: 1,
    duration: '10s',
    thresholds: {
        http_req_failed: ['rate<0.01'], // http error가 1% 이하여야 한다.
        http_req_duration: ['p(95)<200'], // 95%의 요청이 200ms 아래여야 한다.
    },
}