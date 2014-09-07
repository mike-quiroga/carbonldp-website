package com.base22.carbon.test.console;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import com.base22.carbon.agents.AgentDAOJdbc;
import com.base22.carbon.authentication.AuthenticationUtil;

public class AuthenticationUtilConsoleTest {
	public static void main(String[] args) {
		AuthenticationUtilConsoleTest test = new AuthenticationUtilConsoleTest();
		test.execute();
	}

	public void execute() {
		String username = "limited";
		String password = "limited";

		UUID uuid = UUID.randomUUID();
		String uuidString = uuid.toString();
		uuidString = uuidString.replace("-", "");

		String key = AuthenticationUtil.generateRandomSalt();

		String salt = AuthenticationUtil.generateRandomSalt();
		String saltedPassword = AuthenticationUtil.saltPassword(password, salt);
		String hashedPassword = null;
		try {
			hashedPassword = AuthenticationUtil.hashPassword(saltedPassword);
		} catch (NoSuchAlgorithmException exception) {
			exception.printStackTrace();
			return;
		}

		//@formatter:off
		StringBuilder sqlStringBuilder = new StringBuilder();
		sqlStringBuilder
			.append("INSERT INTO ")
				.append(AgentDAOJdbc.TABLE)
			.append("(")
				.append(AgentDAOJdbc.UUID_FIELD)
				.append(", ")
				.append(AgentDAOJdbc.NAME_FIELD)
				.append(", ")
				.append(AgentDAOJdbc.PASSWORD_FIELD)
				.append(", ")
				.append(AgentDAOJdbc.KEY_FIELD)
				.append(", ")
				.append(AgentDAOJdbc.SALT_FIELD)
			.append(") VALUES (")
					.append("UNHEX(\"")
						.append(uuidString)
					.append("\")")
				.append(", ")
					.append("\"")
						.append(username)
					.append("\"")
				.append(", ")
					.append("\"")
						.append(hashedPassword)
					.append("\"")
				.append(", ")
					.append("\"")
						.append(key)
					.append("\"")
				.append(", ")
					.append("\"")
						.append(salt)
					.append("\"")
			.append(")")
		;
		//@formatter:on

		System.out.println(sqlStringBuilder.toString());
	}
}
