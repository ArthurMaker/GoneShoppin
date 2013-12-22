package net.chunk64.chinwe.goneshoppin.banking;

import net.chunk64.chinwe.goneshoppin.GoneShoppin;
import org.bukkit.configuration.Configuration;

import java.math.BigDecimal;

public enum BankLimit
{
	UNLIMITED, FIRST, SECOND, THIRD;

	private BigDecimal amount;

	public static void loadLimits()
	{
		Configuration config = GoneShoppin.getInstance().getConfigFile().getConfiguration();
		for (BankLimit limit : values())
		{
			if (limit == UNLIMITED)
				limit.amount = new BigDecimal(Integer.MAX_VALUE);
			else
				limit.amount = new BigDecimal(config.getString("bank.limits." + limit.name().toLowerCase()));
		}
	}

	public BigDecimal getAmount()
	{
		return amount;
	}
}
