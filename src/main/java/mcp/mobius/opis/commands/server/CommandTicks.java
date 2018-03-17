package mcp.mobius.opis.commands.server;

import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.commands.IOpisCommand;
import mcp.mobius.opis.events.PlayerTracker;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentText;

public class CommandTicks extends CommandBase implements IOpisCommand {

	@Override
	public String getCommandName() {
		return "opis_ticks";
	}

	@Override
	public String getCommandNameOpis() {
		return this.getCommandName();
	}	
	
	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "";
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		if (astring.length < 1) return;
		try{
			modOpis.profilerMaxTicks = Integer.valueOf(astring[0]);
			icommandsender.addChatMessage(new ChatComponentText(String.format("\u00A7oOpis ticks set to %s ticks.", astring[0])));

		} catch (Exception e){}
	}

	@Override
    public int getRequiredPermissionLevel()
    {
        return 3;
    }	

	@Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
		if (sender instanceof EntityPlayerMP)
			return PlayerTracker.INSTANCE.isPrivileged((EntityPlayerMP)sender);			
		
		if (sender   instanceof DedicatedServer) return true;
		if (!(sender instanceof DedicatedServer) && !(sender instanceof EntityPlayerMP)) return true;

		return false;
    }

	@Override
	public String getDescription() {
		return "Sets the amount of data points to gather.";
	}	
	
}
