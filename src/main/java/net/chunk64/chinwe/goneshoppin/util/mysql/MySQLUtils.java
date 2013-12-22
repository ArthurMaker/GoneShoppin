package net.chunk64.chinwe.goneshoppin.util.mysql;

import org.bukkit.plugin.Plugin;

import java.sql.Connection;

public class MySQLUtils
{

	public static MySQL mySQL;
	public static Connection connection = null;

	public static void init(Plugin plugin)
	{
		mySQL = new MySQL(plugin, "server30.bigwetfish.co.uk", "3306", "chunknet_pricelist", "chunknet_prices", "fwu78e27dstq");
		connection = mySQL.openConnection();
	}




}
