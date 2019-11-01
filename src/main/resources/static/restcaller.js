export default class RestCaller {

    constructor() {
        this.uri = './demo/ping';
    }

    dates() {
        let result = new Promise((resolve, reject) => {
            fetch('./demo/ping').then(response => {
                    return response.json();
                }).then(data => {
                    resolve(data);
                    /*
                    const dateTimeExample = data.dateTimeBerlin;
                    console.log("Berlin: " + dateTimeExample);        
                    console.log(data);
                    */
                }).catch(err => {
                    console.error(err);
                    reject(err);
                });
        });
        return result;
    }

}