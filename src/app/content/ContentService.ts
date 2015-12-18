import { Injectable } from 'angular2/angular2';
import { Http, Response, Request } from 'angular2/http';

import Carbon from 'carbonldp-sdk';

@Injectable()
export default class ContentService {

    static parameters = [[ Carbon ], [Http]];
    static dependencies = ContentService.parameters;

    carbon:Carbon;

    http:Http;

    data:string;

    constructor(carbon:Carbon, http:Http)
    {
        this.carbon = carbon;
        this.http = http;
    }

    getDocumentById(id:string):Promise<string> {

        return new Promise<string>( ( resolve, reject ) => {


            let url = window.location.href;
            let arr = url.split("/");
            let protocolHostAndPort = arr[0] + "//" + arr[2]

            this.http.get(protocolHostAndPort + '/assets/documents/' + id)
                .map(res => res.text())
                .subscribe(
                    data => this.data = data,
                    err => console.log(err),
                    () => resolve( this.data )
                );

        });
    }

}
