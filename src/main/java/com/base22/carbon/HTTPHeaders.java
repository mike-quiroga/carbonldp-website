package com.base22.carbon;

public abstract class HTTPHeaders {
	public static final String ACCEPT = "Accept";
	public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
	public static final String ACCEPT_PATCH = "Accept-Patch";
	public static final String ACCEPT_POST = "Accept-Post";
	public static final String ACCEPT_PUT = "Accept-Put";
	public static final String ALLOW = "Allow";
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String ETAG = "ETag";
	public static final String IF_MATCH = "If-Match";
	public static final String LINK = "Link";
	public static final String LOCATION = "Location";
	public static final String PREFER = "Prefer";
	public static final String PREFERENCE_APPLIED = "Preference-Applied";
	public static final String SLUG = "Slug";

	public static enum CORSHeader {
		//@formatter:off
        ACCEPT("Accept"),
        ACCEPT_PATCH("Accept-Patch"),
        ACCEPT_POST("Accept-Post"),
        ACCEPT_PUT("Accept-Put"),
        ALLOW("Allow"),
        CONTENT_LENGTH("Content-Length"),
        CONTENT_TYPE("Content-Type"),
        ETAG("ETag"),
        LINK("Link"),
        LOCATION("Location"),
        PREFERENCE_APPLIED("Preference-Applied");
        //@formatter:on

		private final String key;

		CORSHeader(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}
}
