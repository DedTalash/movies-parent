package accounting.controller;

import accounting.dto.AccountDto;
import accounting.service.api.AccountingManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("accounts")
public class AccountController
{
	static Logger LOG = LoggerFactory.getLogger(AccountController.class);
	final AccountingManagement accountingManagement;

	public AccountController(AccountingManagement accountingManagement)
	{
		this.accountingManagement = accountingManagement;
	}

	@PostMapping("/")
	AccountDto addAccount(@RequestBody @NotNull AccountDto accountDto)
	{
		accountingManagement.addAccount(accountDto);
		LOG.debug("log with className: {} added", accountDto.username);
		return accountDto;
	}

	@DeleteMapping("/{id}")
	void deleteAccount(@PathVariable String id)
	{
		accountingManagement.revoke(id);
		LOG.debug("Account : {} revoked", id);
	}

	@PutMapping("/{id}")
	void activatingAccount(@PathVariable String id)
	{
		accountingManagement.activate(id);
		LOG.debug("Account : {} activated", id);
	}

	@PutMapping("/{id}/password")
	void updatePassword(
        @PathVariable String id,
        @RequestParam String password
    ) {
		accountingManagement.updatePassword(id, password);
		LOG.debug("Password : {} updated", id);
	}

	@GetMapping("/{id}/password")
	String getPassword(@PathVariable String id)
	{
		return accountingManagement.getPasswordCode(id);
	}

	@GetMapping("/{id}/roles")
	String[] gettingRole(@PathVariable String id)
	{
		return accountingManagement.getRoles(id);
	}

	@PutMapping("/{id}/roles")
	void addRole(@PathVariable String id, String role)
	{
		accountingManagement.addRole(id, role);
		LOG.debug("Account : {} role: {} added", id, role);
	}

	@DeleteMapping("/{id}/roles")
	void deleteRole(@PathVariable String id, String role)
	{
		accountingManagement.deleteRole(id, role);
		LOG.debug("Account : {} role: {} deleted", id, role);
	}
}