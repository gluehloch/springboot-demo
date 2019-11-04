import RestCaller from './RestCaller.js';

export default class Client {
    constructor() {
        this.$dateTime = null;
        this.restCaller = new RestCaller();
        this.app = document.getElementById('app');
        this.getCurrentDateTime();
    }

    getCurrentDateTime() {
        this.restCaller.dates().then(dates => this.storeDate(dates));
    }

    storeDate(dates) {
        this.$dateTime = dates;
        console.log(dates);
    }
}

const template = document.createElement('template');
template.innerHTML = `<button>Get Time</button><br/><b>Uhrzeit:</b><div id="dateTime"></div>`;

class DateTimeElement extends HTMLElement {
    constructor() {
        // always call super() first
        super(); 
        console.log('constructed!');

        this.client = new Client();

        this._shadowRoot = this.attachShadow({ 'mode': 'open' });
        this._shadowRoot.appendChild(template.content.cloneNode(true));
        this.$dateTime = this._shadowRoot.querySelector('div');

        this.$getTimeButton = this._shadowRoot.querySelector('button');
        this.$getTimeButton.addEventListener('click', (e) => {
            this.client.getCurrentDateTime();
        });
    }

    render(dateTime) {
        this.$dateTime.innerHTML = dateTime;
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

