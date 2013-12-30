package net.chunk64.chinwe.goneshoppin.banking;

import net.chunk64.chinwe.goneshoppin.util.Config;
import org.bukkit.configuration.Configuration;

import java.math.BigDecimal;

public enum BankLimit
{
	UNLIMITED, FIRST, SECOND, THIRD;

	private BigDecimal amount;

	public static void loadLimits()
	{
		Configuration config = Config.getInstance().getConfiguration();
		for (BankLimit limit : values())
		{
			if (limit == UNLIMITED)
				limit.amount = new BigDecimal(Integer.MAX_VALUE);
			else
				limit.amount = new BigDecimal(config.getString("bank.limits." + limit.name().toLowerCase()));
		}
	}

	public static BankLimit match(String input)
	{
		for (BankLimit limit : values())
			if (input.equalsIgnoreCase(limit.toString()))
				return limit;
		return null;
	}

	public BigDecimal getAmount()
	{
		return amount;
	}

	public String toAmount()
	{
		if (this == UNLIMITED)
			return "infinite GN";
		return getAmount() + "GN";
	}
}
