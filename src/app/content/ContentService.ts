import { Injectable } from 'angular2/angular2';

import Carbon from 'carbonldp-sdk';

@Injectable()
export default class ContentService {

    static parameters = [[ Carbon ]];
    static dependencies = ContentService.parameters;

    carbon:Carbon;

    items:string[] = [];

    constructor(carbon:Carbon)
    {

        console.log(">> ContentService -> constructed");

        this.carbon = carbon;

    }

}
