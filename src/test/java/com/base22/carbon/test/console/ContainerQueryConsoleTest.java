package com.base22.carbon.test.console;

public class ContainerQueryConsoleTest {

	public static void main(String[] args) {
		ContainerQueryConsoleTest consoleTest = new ContainerQueryConsoleTest();
		consoleTest.execute();
	}
	
	private void execute() {
		String documentURI = "";
		StringBuffer query = new StringBuffer();
		query
		.append("CONSTRUCT {")
			.append("\n\t<")
				.append(documentURI)
			.append("\n\t?membershipResource ?hasMemberRelation ?members.")
		.append("\n} WHERE {")
			.append("\n\t{")
				.append("\n\t\t GRAPH <")
					.append(documentURI)
				.append("> {")
					.append("\n\t\t\t <")
						.append(documentURI)
					.append(">")
						.append("\n\t\t\t\tldp:membershipResource ?membershipResource;")
						.append("\n\t\t\t\tldp:hasMemberRelation ?hasMemberRelation.")
				.append("\n\t\t}")
			.append("\n\t} UNION {")
				.append("\n\t\t GRAPH ?membershipResource {")
					.append("\n\t\t\t ?membershipResource ?hasMemberRelation ?members.")
				.append("\n\t\t}")
			.append("\n\t}")
		.append("\n}")
	;
		
		System.out.println(query.toString());
	}

}
