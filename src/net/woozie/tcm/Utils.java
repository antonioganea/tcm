package net.woozie.tcm;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

class SortById implements Comparator<ItemStack> 
{
    public int compare(ItemStack a, ItemStack b) 
    {
    	int aT = (a != null) ? a.getTypeId() : -1;
    	int bT = (b != null) ? b.getTypeId() : -1;
        return bT - aT;
    }
}

class Tuple<X, Y> { 
	  public final X x; 
	  public final Y y; 
	  public Tuple(X x, Y y) { 
	    this.x = x; 
	    this.y = y; 
	  }
	  @Override
	  public boolean equals(Object a) {
		  if ( a.getClass() == this.getClass() ) {
			  Tuple<X,Y> aa = (Tuple<X,Y>) a;
			  if ( aa.x == this.x && aa.y == this.y ) {
				  return true;
			  }
		  }
		  return false;
	  }
}


class ExtendedVirtualInventory {
	HashMap< Tuple<Material, Short>, Integer > stackables = new HashMap<>();
	
	public boolean addStack(ItemStack itemStack) {
		if ( itemStack.hasItemMeta() == false ) {
			Material mat = itemStack.getType();
			Short durability = itemStack.getDurability();
			
			Tuple<Material, Short> tuple = new Tuple<>(mat,durability);
			
			if ( !stackables.containsKey(tuple) ) {
				stackables.put(tuple, itemStack.getAmount());
			} else {
				int n = stackables.get(tuple);
				stackables.put(tuple, n + itemStack.getAmount());
			}
			return true;
		}
		return false;
	}
	
	public void extractFrom ( Inventory inv ) {
		ItemStack[] contents = inv.getContents();
		int size = inv.getSize();
		for ( int i = 0; i < size; i++ ) {
			if ( contents[i] == null ) { continue; }
			if ( this.addStack(contents[i]) ) {
				inv.remove(contents[i]);
			}
		}
	}
	
	public void pourInto ( Inventory inv ) {
		Iterator<HashMap.Entry<Tuple<Material, Short>, Integer>> it = stackables.entrySet().iterator();
	    while (it.hasNext()) {
	    	HashMap.Entry<Tuple<Material, Short>, Integer> pair = (HashMap.Entry<Tuple<Material, Short>, Integer>)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        
	        Material mat = pair.getKey().x;
	        Short durability = pair.getKey().y;
	        Integer count = pair.getValue();
	        
	        inv.addItem( new ItemStack(mat, count, durability) );
	        
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}

}

public class Utils {

	public static int countNulls( Inventory inv ) {
		ItemStack[] contents = inv.getContents();
		
		int size = inv.getSize();
		
		int nulls = 0;
		
		for ( int i = 0; i < size; i++ ) {
			if ( contents[i] == null ) {
				nulls ++;
			}
		}
		return nulls;
	}
	
	public static void sort ( Inventory inv ) {
		ItemStack[] contents = inv.getContents();
		Arrays.sort(contents, new SortById()); 
		inv.setContents(contents);
	}
	
	public static void compress ( Inventory inv ) {
		ExtendedVirtualInventory stackables = new ExtendedVirtualInventory();
		stackables.extractFrom(inv);
		stackables.pourInto(inv);
	}
}
