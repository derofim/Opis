package mcp.mobius.opis.events;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import mcp.mobius.opis.data.holders.basetypes.SerialInt;
import mcp.mobius.opis.data.holders.basetypes.SerialLong;
import mcp.mobius.opis.data.holders.newtypes.*;
import mcp.mobius.opis.data.managers.ChunkManager;
import mcp.mobius.opis.data.managers.EntityManager;
import mcp.mobius.opis.data.managers.StringCache;
import mcp.mobius.opis.data.managers.TileEntityManager;
import mcp.mobius.opis.data.profilers.ProfilerPacket;
import mcp.mobius.opis.data.profilers.ProfilerTick;
import mcp.mobius.opis.modOpis;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.server.NetDataList;
import mcp.mobius.opis.network.packets.server.NetDataValue;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayList;
import java.util.HashMap;

public enum OpisServerTickHandler{
	INSTANCE;
	
	public long profilerUpdateTickCounter = 0;	
	public int  profilerRunningTicks;
	public EventTimer timer500   = new EventTimer(500);	
	public EventTimer timer1000  = new EventTimer(1000);
	public EventTimer timer2000  = new EventTimer(2000);
	public EventTimer timer5000  = new EventTimer(5000);
	public EventTimer timer10000 = new EventTimer(10000);
	
	public HashMap<EntityPlayerMP, AccessLevel> cachedAccess = new HashMap<EntityPlayerMP, AccessLevel>(); 
	
	@SubscribeEvent
	public void tickEnd(TickEvent.ServerTickEvent event) {
				
			StringCache.INSTANCE.syncNewCache();		
		
			// One second timer
			if (timer1000.isDone() && PlayerTracker.INSTANCE.playersSwing.size() > 0){
				
				DataTiming tickTiming = new DataTiming(((ProfilerTick)ProfilerSection.TICK.getProfiler()).data.getGeometricMean());
				if (!tickTiming.timing.isNaN()){
					PacketManager.sendPacketToAllSwing(new NetDataValue(Message.NEXUS_DATA,
									new NexusData(
										new SerialLong(((ProfilerPacket)ProfilerSection.PACKET_OUTBOUND.getProfiler()).dataAmount),
										new SerialLong(((ProfilerPacket)ProfilerSection.PACKET_INBOUND.getProfiler()).dataAmount),
										new SerialInt(ChunkManager.INSTANCE.getForcedChunkAmount()),
										new SerialInt(ChunkManager.INSTANCE.getLoadedChunkAmount()),
										new DataTiming(((ProfilerTick)ProfilerSection.TICK.getProfiler()).data.getGeometricMean()),
										new SerialInt(TileEntityManager.INSTANCE.getAmountTileEntities()),
										new SerialInt(EntityManager.INSTANCE.getAmountEntities()),
										new SerialInt(FMLCommonHandler.instance().getMinecraftServerInstance().getCurrentPlayerCount())
									)));			
				}
				// End of summary update
				
			
				for (EntityPlayerMP player : PlayerTracker.INSTANCE.playersSwing){
					if (!cachedAccess.containsKey(player) || cachedAccess.get(player) != PlayerTracker.INSTANCE.getPlayerAccessLevel(player)){
						PacketManager.validateAndSend(new NetDataValue(Message.STATUS_ACCESS_LEVEL, new SerialInt(PlayerTracker.INSTANCE.getPlayerAccessLevel(player).ordinal())), player);
						cachedAccess.put(player, PlayerTracker.INSTANCE.getPlayerAccessLevel(player));
					}
				}
				
				// Dimension data update.
				ArrayList<DataDimension> dimData = new ArrayList<DataDimension>();
				for (int dim : DimensionManager.getIDs()){
					dimData.add(new DataDimension().fill(dim));
				}
				PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_DIMENSION_DATA, dimData));

				// Profiler update (if running)
				if (modOpis.profilerRun){
					PacketManager.sendPacketToAllSwing(new NetDataValue(Message.STATUS_RUNNING,    new SerialInt(modOpis.profilerMaxTicks)));
					PacketManager.sendPacketToAllSwing(new NetDataValue(Message.STATUS_RUN_UPDATE, new SerialInt(profilerRunningTicks)));
				}
				
				((ProfilerPacket)ProfilerSection.PACKET_INBOUND.getProfiler()).dataAmount = 0L;
				((ProfilerPacket)ProfilerSection.PACKET_OUTBOUND.getProfiler()).dataAmount = 0L;
			}
			
			// Two second timer
			if (timer2000.isDone() && PlayerTracker.INSTANCE.playersSwing.size() > 0){
				ArrayList<DataThread> threads = new ArrayList<DataThread>();
				for (Thread t : Thread.getAllStackTraces().keySet()){
					threads.add(new DataThread().fill(t));
				}
				PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_THREADS, threads));				
				
				PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_PLAYERS, EntityManager.INSTANCE.getAllPlayers()));				
			}
			
			// Five second timer
			if (timer5000.isDone() && PlayerTracker.INSTANCE.playersSwing.size() > 0){
				PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_AMOUNT_ENTITIES,  EntityManager.INSTANCE.getCumulativeEntities(false)));
				PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_AMOUNT_TILEENTS,  TileEntityManager.INSTANCE.getCumulativeAmountTileEntities()));

				PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_PACKETS_OUTBOUND, new ArrayList<DataPacket>( ((ProfilerPacket)ProfilerSection.PACKET_OUTBOUND.getProfiler()).data.values())));
				PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_PACKETS_INBOUND,  new ArrayList<DataPacket>( ((ProfilerPacket)ProfilerSection.PACKET_INBOUND.getProfiler()).data.values())));
				
				PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_PACKETS_OUTBOUND_250, new ArrayList<DataPacket250>( ((ProfilerPacket)ProfilerSection.PACKET_OUTBOUND.getProfiler()).data250.values())));
				PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_PACKETS_INBOUND_250,  new ArrayList<DataPacket250>( ((ProfilerPacket)ProfilerSection.PACKET_INBOUND.getProfiler()).data250.values())));

				((ProfilerPacket)ProfilerSection.PACKET_OUTBOUND.getProfiler()).startInterval();
				((ProfilerPacket)ProfilerSection.PACKET_INBOUND.getProfiler()).startInterval();				
				
				/*
				for (DataPacket data : ((ProfilerPacket)ProfilerSection.PACKET_OUTBOUND.getProfiler()).jabbaSpec){
					System.out.printf("[ %d ] %d %d\n", data.id, data.amount, data.size);
				}
				*/
			}
			
			profilerUpdateTickCounter++;
			
			if (profilerRunningTicks < modOpis.profilerMaxTicks && modOpis.profilerRun){
				profilerRunningTicks++;
			}else if (profilerRunningTicks >= modOpis.profilerMaxTicks && modOpis.profilerRun){
				profilerRunningTicks = 0;
				modOpis.profilerRun = false;
				ProfilerSection.desactivateAll(Side.SERVER);
				
				PacketManager.sendPacketToAllSwing(new NetDataValue(Message.STATUS_STOP, new SerialInt(modOpis.profilerMaxTicks)));
				
				for (EntityPlayerMP player : PlayerTracker.INSTANCE.playersSwing){
					PacketManager.sendFullUpdate(player);
				}
			}			
	}
	
}
