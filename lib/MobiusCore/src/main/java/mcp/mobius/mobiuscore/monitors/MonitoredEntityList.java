package mcp.mobius.mobiuscore.monitors;

import java.util.HashMap;
import java.util.Map;

import mcp.mobius.mobiuscore.asm.CoreDescription;
import net.minecraft.entity.Entity;

public class MonitoredEntityList<E> extends MonitoredList<E>{
	
	private Map<String, Integer> count = new HashMap<String, Integer>();
	
	@Override
	protected void addCount(E e){
		if (e == null) return;		
		String name = this.getName(e);
		try{
			count.put(name, count.get(name) + 1);
		} catch (NullPointerException ex){
			count.put(name, 1);
		} catch (Exception ex){
			ex.printStackTrace();
			count.put(name, 1);
		}
	}
	
	@Override
	protected void removeCount(int index){
		this.removeCount(this.get(index));
	}
	
	@Override
	protected void removeCount(Object o){
		if (o == null) return;
		String name = this.getName(o);
		
		try{
			this.count.put(name, this.count.get(name) - 1);
		} catch (NullPointerException e){
			this.count.put(name, 0);
		}
	}

	@Override
	protected void clearCount(){
		this.count.clear();
	}	
	
	protected String getName(Object o){
		return ((Entity)o).getCommandSenderName();
		//return o.getClass().getName();
	}

	@Override
	public void printCount(){
		for (String s : this.count.keySet())
			CoreDescription.log.info(String.format("%s : %s", s, this.count.get(s)));
	}
	
	public Map<String, Integer> getCount(){
		return this.count;
	}
	
}
