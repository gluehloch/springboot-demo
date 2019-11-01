import RestCaller from './RestCaller.js';

export default class Client {
    constructor() {
        this.restCaller = new RestCaller();
        this.app = document.getElementById('app');
        this.restCaller.dates().then(dates => this.render(dates));
    }

    render(dates) {
        console.log(dates);
    }
}

let client = new Client();
