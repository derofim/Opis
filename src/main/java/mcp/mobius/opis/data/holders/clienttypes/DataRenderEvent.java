package mcp.mobius.opis.data.holders.clienttypes;

import com.google.common.collect.Table.Cell;
import mcp.mobius.opis.data.holders.newtypes.DataTiming;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class DataRenderEvent implements Comparable {
	public String     event;
	public String     handler;
	public String     package_;
	public String     mod;
	public long       nCalls;
	public DataTiming update;
	
	public DataRenderEvent fill(Cell<Class, String, DescriptiveStatistics> cellData, String modName){
		/*
		String handlerName = cell.getColumnKey().getSimpleName();
		try {
			String[] splitHandler = handlerName.split("_");
			handlerName  = splitHandler[2] + "." + splitHandler[3];
		} catch (Exception e){}
		*/		
		String[] nameRaw = cellData.getColumnKey().split("\\|");
		
		String handlerName = nameRaw[1];
		try {
			String[] splitHandler = handlerName.split("_");
			handlerName  = splitHandler[2] + "." + splitHandler[3];
		} catch (Exception e){}		
		
		this.package_= nameRaw[0];
		this.handler = handlerName;
		this.event   = cellData.getRowKey().getName().replace("net.minecraftforge.event.", "");
		this.nCalls  = cellData.getValue().getN();
		this.mod     = modName;
		
		this.update = new DataTiming(cellData.getValue().getGeometricMean());
		return this;
	}

	@Override
	public int compareTo(Object o) {
		return this.update.compareTo(((DataRenderEvent)o).update);
	}
}
