import RestCaller from './RestCaller';

class ShowDate {
    constructor() {
        this.restCaller = new RestCaller();
        this.app = document.getElementById('app');
        this.restCaller.dates().then(dates => this.render(dates));
    }

    render(dates) {
        console.log(dates);
    }
}