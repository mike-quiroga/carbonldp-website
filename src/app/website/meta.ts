import { getDOM } from "@angular/platform-browser/src/dom/dom_adapter";

export class Meta {
	//createElement():HTMLElement
	//setMetaDescription(): any { getDOM().createElement("META");}
	setMeta():any {
		let el = getDOM().query('head');
		let meta = getDOM().createElement("META");
		meta.setAttribute("name", "description");
		meta.setAttribute("content", "Free Web tutorials");
		getDOM().appendChild(meta);
		console.log("meta", meta);
	}

}