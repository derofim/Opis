package mcp.mobius.opis.data.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.holders.basetypes.AmountHolder;
import mcp.mobius.opis.data.holders.basetypes.SerialInt;
import mcp.mobius.opis.data.holders.basetypes.SerialLong;
import mcp.mobius.opis.data.holders.stats.StatAbstract;
import mcp.mobius.opis.data.holders.stats.StatsChunk;
import mcp.mobius.opis.helpers.ModIdentification;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.swing.SwingUI;
import mcp.mobius.opis.swing.widgets.JButtonAccess;

public class DataCache implements IMessageHandler{

	private static DataCache _instance = new DataCache();
	public  static DataCache instance() { return _instance; };
	
	private long        clockScrew     = 0;
	private AccessLevel clientAccess   = AccessLevel.NONE;
	
	public AccessLevel getAccessLevel(){
		return this.clientAccess;
	}
	
	public long getClockScrew(){
		return this.clockScrew;
	}

	@Override
	public boolean handleMessage(Message msg, PacketBase rawdata) {
		switch(msg){
		case STATUS_ACCESS_LEVEL:{
			this.clientAccess = AccessLevel.values()[((SerialInt)rawdata.value).value];
			break;
		}
		
		case STATUS_CURRENT_TIME:{
			this.clockScrew = System.currentTimeMillis() - ((SerialLong)rawdata.value).value;
			System.out.printf("Adjusting clock screw. Server differential is %d ms.\n", clockScrew);
			break;
		}		
		
		default:
			return false;
			
		}
		return true;
	}
}
