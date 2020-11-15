package accounting.entities;

import accounting.components.Status;
import accounting.exceptions.InvalidInputException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Queue;
import java.util.Set;

import static accounting.components.Constant.COUNT_PASSWORD;
import static accounting.components.Constant.EXPIRE_PERIOD;

@Document(collection="accounts")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Account {
	@Id
	String username;
	String password;

	Set<String> roles;
	Status status;

	LocalDate dateCreate;
	LocalDate dateExpire;
	LocalDate dateActivate;
	Queue<String> oldPasswords;

	public boolean isActive()
	{
		return status == Status.ACTIVE;
	}

	public boolean isInactive()
	{
		return status == Status.INACTIVE;
	}

	public boolean isRevoked()
	{
		return status == Status.REVOKED;
	}

	public boolean isExpired()
	{
		return dateExpire != null && dateExpire.isBefore(LocalDate.now());
	}

	public void activate()
	{
		status = Status.ACTIVE;
		dateActivate = LocalDate.now();
		dateExpire = dateActivate.plusDays(EXPIRE_PERIOD);
	}


	public void addPassword(String passwordHash)
	{
		if (oldPasswords.contains(passwordHash)) {
			throw new InvalidInputException("Password was used recently");
		}

		oldPasswords.add(passwordHash);

		if (oldPasswords.size() > COUNT_PASSWORD) {
			oldPasswords.remove();
		}

		password = passwordHash;
	}

	public void addRole(String role)
	{
		if (roles.contains(role)) {
			throw new InvalidInputException("Account already has this role");
		}

		roles.add(role);
	}

	public void deleteRole(String role)
	{
		if (!roles.contains(role)){
			throw new InvalidInputException("Account doesn't has this role");
		}

		roles.remove(role);
	}
}
