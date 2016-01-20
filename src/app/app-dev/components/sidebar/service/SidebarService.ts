import { Injectable, ElementRef } from 'angular2/angular2';


@Injectable()
export default class SidebarService {

	static parameters = [];
	static dependencies = SidebarService.parameters;

	data:string;
	element:ElementRef;
	$element:JQuery;

	counter:number = 0;

	constructor() {
		//this.element = elementRef;
		//this.$element = $( this.element.nativeElement );
	}

	add():void {
		this.counter ++;
	}

	remove():void {
		this.counter --;
	}

	show():void {
		alert( this.counter );
	}

	//getPost( id:number ):Promise<BlogPost> {
	//
	//	return new Promise<BlogPost[]>( ( resolve, reject ) => {
	//		this.getPostsList().then(
	//			( posts )=> {
	//				let post:BlogPost = posts[ id ];
	//				post.creationDate = new Date( Date.parse( post.creationDate.toString() ) );
	//				resolve( post );
	//			},
	//			( error )=> {
	//				console.log( error );
	//			}
	//		).catch( console.error );
	//	} ).catch( console.error );
	//}

}
