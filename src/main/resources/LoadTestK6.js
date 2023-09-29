import http from 'k6/http';
import { sleep } from 'k6';

export default function () {
    let params = {
        headers: {
            'Content-Type': 'application/json',
            'User-Agent': 'k6',
        },
        http1: true, // HTTP 1.1 프로토콜 사용
    };
    const httpRes = http.get('http://localhost:8080/');

}

export const options = {
    vus: 20,
    duration: '1m',
    thresholds: {
        http_req_failed: ['rate<0.01'], // http error가 1% 이하여야 한다.
        http_req_duration: ['p(95)<200'], // 95%의 요청이 200ms 아래여야 한다.
    },
}