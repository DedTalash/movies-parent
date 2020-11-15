package accounting.dto;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Arrays;

@Validated
public class AccountDto
{
	@NotEmpty @Size(min = 5)
	public String username;
	@NotEmpty @Size(min = 6)
	public String password;

	public String[] roles;

	@Override
	public String toString()
	{
		return "AccountDto [username=" + username + ", password=" + password + ", roles=" + Arrays.toString(roles) + "]";
	}
}
