import { Pipe, PipeTransform } from '@angular/core';
/*
 * Return array of object (key, value) of a map
 * Takes a map.
 * Usage:
 *   map | iterablemap:Map
 * Example:
 *   {{ myMap |  iterablemap}}
 *   returns to: [{ key:string, value:any}, ...]
*/
@Pipe( { name: "iterablemap" } )
export class IterableMapPipe implements PipeTransform {
	transform( value:Map<string, any>, args?:any ):{key:string, value:any}[] {
		let returnArray:{key:string, value:any}[] = [];

		value.forEach( ( entryVal, entryKey ) => {
			returnArray.push( {
				key: entryKey,
				value: entryVal
			} );
		} );

		return returnArray;
	}
}