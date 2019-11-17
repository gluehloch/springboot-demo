/* jshint esversion: 6 */
/* jshint -W097 */
'use strict';

// import RestCaller from './RestCaller.js';

export default class DateTimeController {
    constructor() {
        this.$dateTime = null;
        // this.app = document.getElementById('app');
        // this.getCurrentDateTime();
    }

    date() {
        return new Promise((resolve, reject) => {
            fetch('./demo/ping').then(response => {
                    return response.json();
                }).then(data => {
                    resolve(data);
                }).catch(err => {
                    console.error(err);
                    reject(err);
                });
        });
    }

    getCurrentDateTime(callback) {
        this.date().then(dateTime => {
            this.storeDate(dateTime);
            callback(dateTime);
        })
    }

    storeDate(dateTime) {
        this.$dateTime = dateTime;
        console.log(dateTime);
    }
}

const template = document.createElement('template');
template.innerHTML = `<button>Get Time</button><br/><h3>Uhrzeit:</h3><div id="dateTime"></div>`;

class DateTimeElement extends HTMLElement {

    constructor() {
        // always call super() first
        super(); 
        console.log('constructed!');

        this.dateTimeController = new DateTimeController();

        this._shadowRoot = this.attachShadow({ 'mode': 'open' });
        this._shadowRoot.appendChild(template.content.cloneNode(true));
        this.$dateTime = this._shadowRoot.querySelector('#dateTime');

        this.$getTimeButton = this._shadowRoot.querySelector('button');
        this.$getTimeButton.addEventListener('click', (e) => {
            this.getDateTime();
        });
    }

    getDateTime() {
        this.dateTimeController.getCurrentDateTime((dateTime) => {
            this.$dateTime.innerHTML = dateTime.dateTimeBerlinWithMilli;
        });
    }

    render(dateTime) {
        this.$dateTime.innerHTML = dateTime.dateTimeBerlin;
    }

    connectedCallback() {
        console.log('connected!');
    }

    disconnectedCallback() {
        console.log('disconnected!');
    }

    attributeChangedCallback(name, oldVal, newVal) {
        console.log(`Attribute: ${name} changed!`);
    }

    adoptedCallback() {
        console.log('adopted!');
    }
}

window.customElements.define('date-time', DateTimeElement);

