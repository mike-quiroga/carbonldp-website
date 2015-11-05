import { View, Component } from 'angular2/angular2';

import $ from 'jquery';
import 'semantic-ui/semantic';

import template from './template.html!';

@Component({
	selector: 'modal',
	template: template
})
export default class ModalComponent {
	afterViewInit():void {
		$( '.ui.modal' ).modal( 'show' );
	}
}
