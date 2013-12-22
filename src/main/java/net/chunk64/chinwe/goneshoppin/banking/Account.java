package net.chunk64.chinwe.goneshoppin.banking;

import net.chunk64.chinwe.goneshoppin.util.Config;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Account implements ConfigurationSerializable
{
	private BigDecimal balance;
	private BankLimit limit;
	private String player;

	public Account(String player)
	{
		this.player = player;
		this.balance = BigDecimal.ZERO;
		this.limit = Config.DefaultLimit;
		Bank.getInstance().addAccount(this);
	}


	public void deposit(int amount)
	{
		this.balance = balance.add(new BigDecimal(amount));
	}

	public void withdraw(int amount)
	{
		this.balance = balance.subtract(new BigDecimal(amount));
	}

	public BigDecimal getBalance()
	{
		return balance;
	}

	public String getName()
	{
		return player;
	}

	public BankLimit getLimit()
	{
		return limit;
	}



	@SuppressWarnings("unused")
	public Account(Map<String, Object> map)
	{
		this.player = (String) map.get("player");
		this.limit = BankLimit.valueOf((String) map.get("limit"));
		this.balance = new BigDecimal((String) map.get("balance"));
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("balance", balance.toString());
		map.put("limit", limit.toString());
		map.put("player", player);

		return map;
	}

	/**
	 * Returns true if amount can be taken out of account
	 */
	public boolean hasBalance(Integer amount)
	{
		return getBalance().doubleValue() >= amount;

	}
}
