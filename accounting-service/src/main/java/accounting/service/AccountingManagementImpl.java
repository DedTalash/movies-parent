package accounting.service;

import accounting.components.Status;
import accounting.dto.AccountDto;
import accounting.entities.Account;
import accounting.exceptions.InvalidInputException;
import accounting.repo.AccountRepo;
import accounting.service.api.AccountingManagement;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Service
public class AccountingManagementImpl implements AccountingManagement
{
	final AccountRepo accountRepo;
	final PasswordEncoder encoder;

	public AccountingManagementImpl(
		AccountRepo accountRepo,
		PasswordEncoder encoder
	) {
		this.accountRepo = accountRepo;
		this.encoder = encoder;
	}

	/**
	 * Adding new account-document
	 * validation:
	 * 1. Account should have unique username
	 * content of account-document should be the following
	 * 1.username
	 * 2.encoded password hash
	 * 3. Activation date time
	 * 4. Expiration date time (Service implementation should have configurable property of active durability
	 * 5. state ("ACTIVE", "REVOKED")
	 * 6. Array of three last password codes
	 *
	 * @param accountDto
	 */
	@Override
	public void addAccount(AccountDto accountDto)
	{
		if (accountRepo.existsById(accountDto.username)) {
			throw new InvalidInputException("Account already exist" + accountDto.username);
		}

		accountRepo.save(toAccount(accountDto));
	}

	/**
	 * Updates password and sets the current activation date with proper update the expiration date
	 * validation:
	 * 1. Account with username exists
	 * 2. New password differs from three last ones
	 * 3. Updating may be only if the account is activated
	 * @param username
	 * @param password
	 */
	@Override
	public void updatePassword(String username, String password)
	{
		operate(username, account -> account.addPassword(encoder.encode(password)));
	}

	/**
	 * revokes account (moves account in the state revoked)
	 * 1. Account with username exists
	 * 2. Account should be in the state "activated"
	 * @param username
	 */
	@Override
	public void revoke(String username)
	{
		operate(username, account -> account.setStatus(Status.REVOKED));
	}

	/**
	 * activates account (moves from the state "revoked" to state "activated"
	 * validation:
	 * 1. Account with username exists
	 * 2. Should be in the state "revoked"
	 * @param username
	 */
	@Override
	public void activate(String username)
	{
		Account account = getAccount(username);
		if (!account.isInactive()) {
			throw new InvalidInputException("Account already activated");
		}

		account.activate();
		accountRepo.save(account);
	}

	/**
	 * adding new role
	 * validation:
	 * 1. Account with username exists
	 * 2. Should be in the state "active"
	 * 3. Not expired
	 * 4. A role doesn't exist
	 * @param username
	 * @param newRole
	 */
	@Override
	public void addRole(String username, String newRole)
	{
		operate(username, account -> account.addRole(newRole));
	}

	/**
	 * deletes a role
	 * validation:
	 * 1. Account with username exists
	 * 2. Should be in the state "active"
	 * 3. Not expired
	 * 4. The role exists
	 * @param username
	 * @param role
	 */
	@Override
	public void deleteRole(String username, String role)
	{
		operate(username, account -> account.deleteRole(role));
	}

	/**
	 *
	 * @param username
	 * @return password code
	 * throws exceptions with the following messages
	 * 1. username doesn't exist
	 * 2. password expired
	 * 3. account revoked
	 */
	@Override
	public String getPasswordCode(String username)
	{
		return getAccount(username).getPassword();

	}

	/**
	 *
	 * @param username
	 * @return roles
	 * throws exceptions with the following messages
	 * 1. username doesn't exist
	 * 2. password expired
	 * 3. account revoked
	 */
	@Override
	public String[] getRoles(String username)
	{
		return getAccount(username)
			.getRoles()
			.toArray(new String[0]);
	}

	private void operate(String username, Consumer<Account> operation)
	{
		Account account = getAccount(username);
		if (!account.isActive()) {
			throw new InvalidInputException("Account is not active");
		}

		operation.accept(account);
		accountRepo.save(account);
	}

	private Account toAccount(AccountDto accountDto)
	{
		String passwordHash = encoder.encode(accountDto.password);

		return new Account(
			accountDto.username,
			passwordHash,
			new HashSet<>(),
			Status.INACTIVE,
			LocalDate.now(),
			null,
			null,
			new LinkedBlockingQueue<>(Collections.singletonList(passwordHash))
		);
	}

	private Account getAccount(String username)
	{
		Account account = accountRepo
			.findById(username)
			.orElseThrow(() -> new InvalidInputException("Account doesn't exist: " + username));

		if (account.isRevoked()) {
			throw new InvalidInputException(String.format("Account %s is revoked", username));
		}

		if (account.isExpired()) {
			throw new InvalidInputException(String.format("Account %s is expired", username));
		}

		return account;
	}
}
